package com.lambda.modules

import baritone.command.BaritoneChatControl
import baritone.utils.BaritoneProcessHelper
import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.event.events.BaritoneCommandEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.items.originalName
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.runSafe
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EJobEvents.*
import com.lambda.enums.EPacket
import com.lambda.enums.EWorkerType
import com.lambda.utils.*
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.time.LocalTime
import java.util.UUID
import com.lambda.client.module.modules.player.Timer
import com.lambda.client.util.TickTimer
import com.lambda.enums.EWorkerStatus
import net.minecraft.client.Minecraft

internal object RemoteControl : PluginModule(
    name = "Remote Control",
    description = "Control your client remotely",
    category = Category.CLIENT,
    pluginMain = SocketPlugin
) {
    private enum class PASSWORD_TYPE {
        RANDOM, NOTRANDOM
    }
    private val passType by setting("Password Type", PASSWORD_TYPE.RANDOM)
    private val password by setting("Password", "", { passType == PASSWORD_TYPE.NOTRANDOM })
    private val server by setting("Server", "localhost")
    private val port by setting("Port", "1984")
    val debug by setting("Debug", false)
    val s = when (passType) {
        PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
        PASSWORD_TYPE.NOTRANDOM -> password
    }
    val secretKey = s.encodeToByteArray()
    private val logger = WorkerLogger()
    private lateinit var socket: SocketManager
    private val jUtils = JobUtils()
    private val timer = TickTimer()

    init {

        onEnable {
            val parsedInt = port.toInt()
            socket = SocketManager(server, parsedInt, Minecraft.getMinecraft().session.username, s)
        }
        onDisable {
            try {
                //SocketManager.close()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

        safeListener<SocketDataReceived> {
            val args: List<String> = it.parse()
            when(it.packet.getPacket()) {
                EPacket.EXIT -> {}
                EPacket.OK -> {}
                EPacket.HEARTBEAT -> {
                    Debug.log("Heartbeat")
                }
                EPacket.LOGIN -> login(ServerData(args[0], args[1], args[3].toBooleanStrict()))
                EPacket.LOGOUT -> logout(args.joinToString(" "))
                EPacket.ADD_WORKER -> {
                    //addWorker(args.joinToString { it })
                    // TODO: Add worker to friendly list
                }
                EPacket.REMOVE_WORKER -> {
                    //removeWorker(args.joinToString { it })
                    // TODO: Remove worker to friendly list
                }
                EPacket.GET_WORKERS -> {
                    Debug.purple("Get workers")
                    val epacket = it.packet.getPacket()
                    val packet = Packet(epacket.byte, WorkerLogger().playerInformations().encodeToByteArray())
                    it.socket.write("${packet.getPacket().byte} ${packet.getFlags().byte} ${WorkerLogger().playerInformations()}")
                    it.socket.newLine()
                    it.socket.flush()
                }
                EPacket.JOB -> {
                    // TODO
                }
                EPacket.CHAT -> {
                    MessageSendHelper.sendServerMessage(args.joinToString(" "))
                }
                EPacket.BARITONE -> {
                    Debug.purple("Baritone command:", args.joinToString(" "))

                    val goal = parseBlockPos(args.joinToString(" "))

                    jUtils.addJob(Job(
                        type = EWorkerType.BARITONE,
                        goal = goal,
                        cancelable = true,
                        player = player,
                        jobs = jUtils,
                        args = args.toTypedArray()
                    ))
                    //MessageSendHelper.sendBaritoneCommand(args.joinToString(" "))
                }
                EPacket.LAMBDA -> {
                    CommandManager.runCommand(args.joinToString(" "))
                }
                EPacket.ERROR -> {
                    // TODO Handle error and send to server
                    MessageSendHelper.sendChatMessage("Error: ${args[0]}")
                }
                EPacket.LISTENER_ADD -> {} // input > /dev/null
                EPacket.LISTENER_REMOVE -> {} // input > /dev/null
                EPacket.HIGHWAY_TOOLS -> {
                    val hwt = HighwayToolsHandler(it.parseByteArray())
                    CommandManager.runCommand("set highwayTools ${hwt.getPacket().string} ${hwt.getArguments().joinToString(" ")}")
                }
            }
        }
        safeListener<TickEvent.ClientTickEvent> {
            jUtils.checkJobs()
            if (timer.tick(1000L, resetIfTick = true)) {
                logger.addPosition(player.position)
                if (logger.shouldSaveMemory()) logger.saveMemory()
            }
        }
        safeListener<JobEvents> { ev ->
            Debug.purple("Job", ev.event.name)
            // TODO: Job status builder
            when(ev.event) {
                JOB_STARTED -> {
                    val packet = Packet(EPacket.JOB.byte, ev.instance.getJob().encodeToByteArray())
                    socket.send(packet)
                }
                JOB_FAILED -> {}

                JOB_FINISHED -> {
                    val packet = Packet(EPacket.JOB.byte, byteArrayOf(EWorkerStatus.IDLE.byte))
                    socket.send(packet)
                }
                JOB_PAUSED -> {}
                JOB_RESUMED -> {}
                JOB_CANCELLED -> {}
                JOB_SCHEDULED -> {}
            }
        }
        safeListener<BaritoneEvents> {
            println("Baritone event: $it")
            jUtils.executeJob(it.job).run {
                val packet = Packet(EPacket.JOB.byte, it.job.getJob().encodeToByteArray())
                socket.send(packet)
            }
        }
    }

}
private fun SafeClientEvent.login(server: ServerData) {
    try {
        FMLClientHandler.instance().connectToServer(mc.currentScreen, server);
    } catch (e: Exception) {
        e.message?.let { Debug.error("Could not log in", it) }
        e.printStackTrace()
    }
}
private fun SafeClientEvent.logout(reason: String) {
    try {
        mc.connection?.networkManager?.closeChannel(TextComponentString(""))
        mc.loadWorld(null as WorldClient?)

        mc.displayGuiScreen(LambdaGuiDisconnected(arrayOf(reason), getScreen(), true, LocalTime.now()))
    } catch (e: Exception) {
        MessageSendHelper.sendChatMessage("Could not log out ${e.message}")
        e.printStackTrace()
    }
}
private fun SafeClientEvent.getScreen() = if (mc.isIntegratedServerRunning) {
    GuiMainMenu()
} else {
    GuiMultiplayer(GuiMainMenu())
}
fun SafeClientEvent.playerInformations(): String {
    val s = StringBuilder()
    s.append("Player: ${player.name} ")
    s.append("Health: ${player.health} ")
    s.append("Food: ${player.foodStats.foodLevel} ")
    s.append("Players in render: ${mc.world.playerEntities.size} ")
    s.append("Coordinates: ${player.position} ")
    s.append("Main hand: ${player.heldItemMainhand.originalName} ")
    s.append("Off hand: ${player.heldItemOffhand.originalName} ")
    s.append(armorInformations())
    return s.toString()
}
fun SafeClientEvent.armorInformations(): String {
    val s = StringBuilder()
    s.append("Armor: ")
    for (itemStack in player.armorInventoryList.reversed()) {
        val dura = itemStack.maxDamage - itemStack.itemDamage
        val duraMultiplier = dura / itemStack.maxDamage.toFloat()
        val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1).toFloat()
        s.append("${itemStack.originalName}: $duraPercent% ")
    }
    return s.toString()
}

fun parseBlockPos(s: String): BlockPos {
    val split = s.split(" ").drop(1)
    println(split.size)
    if (split.size == 3) {
        return BlockPos(split[0].toInt(), split[1].toInt(), split[2].toInt())
    } else if (split.size == 2) {
        return BlockPos(split[0].toInt(), 0, split[1].toInt())
    }
    return BlockPos.ORIGIN
}
