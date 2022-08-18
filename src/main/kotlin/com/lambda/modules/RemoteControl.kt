package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.event.events.BaritoneCommandEvent
import com.lambda.client.event.events.PlayerTravelEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.items.originalName
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EPacket
import com.lambda.utils.*
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
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
    lateinit var socket: SocketManager

    init {

        onEnable {
            val parsedInt = port.toInt()
            socket = SocketManager(server, parsedInt, "Kamigen", s)
        }
        onDisable {
            try {
                //SocketManager.close()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

        safeListener<SocketDataReceived> { it ->
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
                EPacket.LOGOUT -> logout(args.joinToString { it })
                EPacket.ADD_WORKER -> {}//addWorker(args.joinToString { it })
                EPacket.REMOVE_WORKER -> {}//removeWorker(args.joinToString { it })
                EPacket.GET_WORKERS -> {
                    println("Get workers")
                    val epacket = it.packet.getPacket()
                    val getPacket = PacketUtils.getPacketBuilder(epacket, playerInformations().encodeToByteArray())
                    it.socket.write("${epacket.byte} ${getPacket.data}")
                    it.socket.newLine()
                    it.socket.flush()
                }
                EPacket.GET_WORKERS_STATUS -> {
                    // TODO
                }
                EPacket.CHAT -> {
                    MessageSendHelper.sendServerMessage(args.joinToString(" "))
                }
                EPacket.BARITONE -> {
                    // TODO Make command queue
                    println("Baritone command")
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
        safeListener<PlayerTravelEvent> {
            val logger = WorkerLogger()
            logger.addPosition(player.position)
            logger.saveMemory()
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
    s.append("Player: ${player.name}\n")
    s.append("Health: ${player.health}\n")
    s.append("Food: ${player.foodStats.foodLevel}\n")
    s.append("Players in render: ${mc.world.playerEntities.size}\n")
    s.append("Coordinates: ${player.position}\n")
    s.append("Main hand: ${player.heldItemMainhand.originalName}\n")
    s.append("Off hand: ${player.heldItemOffhand.originalName}\n")
    s.append(armorInformations())
    return s.toString()
}
fun SafeClientEvent.armorInformations(): String {
    val s = StringBuilder()
    s.append("Armor: \n")
    for ((index, itemStack) in player.armorInventoryList.reversed().withIndex()) {
        val dura = itemStack.maxDamage - itemStack.itemDamage
        val duraMultiplier = dura / itemStack.maxDamage.toFloat()
        val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1).toFloat()
        s.append("${itemStack.originalName}: $duraPercent%\n")
    }
    return s.toString()
}
