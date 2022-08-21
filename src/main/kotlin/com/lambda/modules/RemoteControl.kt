package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.event.events.BaritoneCommandEvent
import com.lambda.client.event.events.PlayerMoveEvent
import com.lambda.client.event.events.PlayerTravelEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.items.originalName
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.runSafe
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EJobEvents
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
import java.time.LocalTime
import java.util.*

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
    val s = when (passType) {
        PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
        PASSWORD_TYPE.NOTRANDOM -> password
    }
    val secretKey = s.encodeToByteArray()
    val logger = WorkerLogger()
    lateinit var socket: SocketManager

    init {

        onEnable {
            runSafe {
                val parsedInt = port.toInt()
                socket = SocketManager(server, parsedInt, player.name, s)
            }
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
            println(args)
            // TODO Execute functions
            println(it.packet.getPacket())
            println(it.packet.byte)
            when(it.packet.getPacket()) {
                EPacket.EXIT -> {}
                EPacket.OK -> {}
                EPacket.HEARTBEAT -> {
                    println("Heartbeat")
                }
                EPacket.LOGIN -> login(ServerData(args[0], args[1], args[3].toBooleanStrict()))
                EPacket.LOGOUT -> logout(args.joinToString(" "))
                EPacket.ADD_WORKER -> {}//addWorker(args.joinToString { it })
                EPacket.REMOVE_WORKER -> {}//removeWorker(args.joinToString { it })
                EPacket.GET_WORKERS -> {
                    println("Get workers")
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
                    // TODO Make command queue
                    println("Baritone command")
                    val blockPos = parseBlockPos(args.joinToString(" "))
                    Job(EWorkerType.BARITONE, blockPos, cancelable = true, player = player)
                        .store()

                    MessageSendHelper.sendBaritoneCommand(*args.toTypedArray())
                }
                EPacket.LAMBDA -> {
                    CommandManager.runCommand(args.joinToString(" "))
                }
                EPacket.ERROR -> {
                    // TODO Handle error and send to server
                    MessageSendHelper.sendChatMessage("Error: ${args[0]}")
                }
            }
        }
        safeListener<PlayerMoveEvent> {
            JobUtils().checkJobs()
            logger.addPosition(player.position)
            logger.saveMemory()
        }
        safeListener<JobEvents> { ev ->
            println("Job event: ${ev.event.name}")
            when(ev.event) {
                JOB_STARTED -> {
                    val packet = Packet(EPacket.JOB.byte, ev.instance.getJob().encodeToByteArray())
                    socket.send(packet)
                }
                JOB_FAILED -> {
                }

                JOB_FINISHED -> TODO()
                JOB_PAUSED -> TODO()
                JOB_RESUMED -> TODO()
                JOB_CANCELLED -> TODO()
                JOB_INITIALIZED -> TODO()
                JOB_SCHEDULED -> TODO()
            }
        }
    }

}
private fun SafeClientEvent.login(server: ServerData) {
    try {
        FMLClientHandler.instance().connectToServer(mc.currentScreen, server);
    } catch (e: Exception) {
        println("Could not login ${e.message}")
        MessageSendHelper.sendChatMessage("Could not login ${e.message}")
    }
}
private fun SafeClientEvent.logout(reason: String) {
    try {
        mc.connection?.networkManager?.closeChannel(TextComponentString(""))
        mc.loadWorld(null as WorldClient?)

        mc.displayGuiScreen(LambdaGuiDisconnected(arrayOf(reason), getScreen(), true, LocalTime.now()))
    } catch (e: Exception) {
        println("Could not logout ${e.message}")
        MessageSendHelper.sendChatMessage("Could not logout ${e.message}")
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
    val split = s.split(",").drop(1)
    if (split.size == 3) {
        return BlockPos(split[0].toInt(), split[1].toInt(), split[2].toInt())
    } else if (split.size == 2) {
        return BlockPos(split[0].toInt(), 0, split[1].toInt())
    }
    return BlockPos.ORIGIN
}
