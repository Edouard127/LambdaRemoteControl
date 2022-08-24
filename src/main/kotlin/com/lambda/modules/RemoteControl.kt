package com.lambda.modules

import baritone.api.utils.Helper.mc
import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.event.listener.listener
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.items.originalName
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EPacket
import com.lambda.enums.EWorkerType
import com.lambda.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.ScreenShotHelper
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.random.Random

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
    private lateinit var socket: SocketManager
    private val jUtils = JobUtils()
    private val bUtils = BaritoneUtils()
    private var gameState = GameState.NONE
    private var serverData: ServerData? = null
    private var getScreenShot = false

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
            println(it.packet.getPacket())
            val args: List<String> = it.parse()
            when(it.packet.getPacket()) {
                EPacket.EXIT -> {}
                EPacket.OK -> {}
                EPacket.HEARTBEAT -> {
                    Debug.log("Heartbeat")
                }
                EPacket.LOGIN -> {
                    Debug.log(args.joinToString(" "))
                    serverData = ServerData(args[0], args[1], args[2].toBooleanStrict())
                    gameState = GameState.LOGIN
                }
                EPacket.LOGOUT -> gameState = GameState.LOGOUT
                EPacket.ADD_WORKER -> {
                    //addWorker(args.joinToString { it })
                    // TODO: Add worker to friendly list
                }
                EPacket.REMOVE_WORKER -> {
                    //removeWorker(args.joinToString { it })
                    // TODO: Remove worker to friendly list
                }
                EPacket.GET_WORKERS -> {
                    val epacket = it.packet.getPacket()
                    val playerInfo = playerInformations().encodeToByteArray()
                    val packetBuilder = PacketBuilder(epacket, playerInfo)
                    val packet = Packet(epacket.byte, playerInfo.size, packetBuilder)
                    socket.send(packet)
                }
                EPacket.JOB -> {
                    // TODO
                }
                EPacket.CHAT -> {
                    MessageSendHelper.sendServerMessage(args.joinToString(" "))
                }
                EPacket.BARITONE -> {
                    MessageSendHelper.sendBaritoneCommand(args.joinToString(" "))
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
                EPacket.SCREENSHOT -> getScreenShot = true
            }
        }
        safeListener<TickEvent.ClientTickEvent> {
            jUtils.checkJobs()
            bUtils.pathingGoalCheck()
        }
        listener<TickEvent.ClientTickEvent> {
            when (gameState) {
                GameState.LOGIN -> {
                    serverData?.serverIP?.let { ServerData(serverData!!.serverName, it, false) }?.let { login(it) }
                    gameState = GameState.NONE
                }
                GameState.LOGOUT -> {
                    logout("Client sent logout packet")
                    gameState = GameState.NONE
                }
                GameState.NONE -> {}
            }
            if (getScreenShot) {
                getScreenShot = false
                val width = FMLClientHandler.instance().client.displayWidth
                val height = FMLClientHandler.instance().client.displayHeight
                val frameBuffer = FMLClientHandler.instance().client.framebuffer
                val bufferImage = ScreenShotHelper.createScreenshot(width, height, frameBuffer)

                val bImage = bufferImage.toByteArray("png")


                val chunks = bImage.chunk(1024)

                val fragmentOffsets = chunks.mapIndexed { i, _ ->
                    i * 1024
                }
                val hashCode = bImage.hashCode()
                val chunkFragments = chunks.map { bytes ->
                    Fragment(
                    fragment = bytes,
                    offset = fragmentOffsets.sum(),
                    length = bytes.size,
                    hash = hashCode,
                    sum = chunks.sumOf { it.size }
                ) }


                println("Chunks: ${chunks.size}")

                chunks.forEach {
                    val packetBuilder = PacketBuilder(EPacket.SCREENSHOT, it)
                    // TODO: Get the exact length of the packet
                    val packet = Packet(EPacket.SCREENSHOT.byte, 1024, packetBuilder)
                    val fragmentPacket = FragmentedPacket(packet, chunkFragments)
                    socket.send(packet)
                }
            }
        }
        safeListener<JobEvents> {
            // TODO: Job status builder
            val job = PacketBuilder(EPacket.JOB, it.instance.getJob().encodeToByteArray())
            val packet = Packet(EPacket.JOB.byte, job.data.size, job)
            socket.send(packet)
        }
        safeListener<StartPathingEvent> {
            println("Start pathing event: ${it.goal}")
            jUtils.addJob(Job(
                type = EWorkerType.BARITONE,
                goal = it.goal,
            ))
        }
        safeListener<StopPathingEvent> {
            println("Baritone stopped pathing")
            jUtils.currentJob()?.run {
                this.end()
            }
        }
        safeListener<UpdatePathingEvent> {}
    }

}



private fun login(server: ServerData) {
    try {
        FMLClientHandler.instance().connectToServer(GuiMainMenu(), server)
    } catch (e: Exception) {
        e.message?.let { Debug.error("Could not log in", it) }
        e.printStackTrace()
    }
}
private fun logout(vararg reason: String) {
    mc.player.connection.networkManager.closeChannel(TextComponentString(""))
    mc.loadWorld(null as WorldClient?)

    mc.displayGuiScreen(GuiDisconnected(getScreen(), "disconnect.lost", TextComponentString(reason.joinToString(" "))))
}
private fun getScreen() = if (mc.isIntegratedServerRunning) {
    GuiMainMenu()
} else {
    GuiMultiplayer(GuiMainMenu())
}

fun SafeClientEvent.playerInformations(): String = "Player:${mc.player.name} Health:${mc.player.health} Food:${mc.player.foodStats.foodLevel} PlayersRender:${mc.world.playerEntities.size} Coordinates:${mc.player.position} MainHand:${mc.player.heldItemMainhand.originalName} OffHand:${mc.player.heldItemOffhand.originalName} ${armorInformations()}"
fun SafeClientEvent.armorInformations(): String {
    val s = StringBuilder()
    s.append("Armor: ")
    for (itemStack in mc.player.armorInventoryList.reversed()) {
        if (itemStack.isEmpty) continue
        val dura = itemStack.maxDamage - itemStack.itemDamage
        val duraMultiplier = dura / itemStack.maxDamage.toFloat()
        val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1).toFloat()
        s.append("${itemStack.originalName}:$duraPercent% ")
    }
    return s.toString()
}

// convert BufferedImage to byte[]
fun BufferedImage.toByteArray(format: String): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, format, baos)
    return baos.toByteArray()
}

fun ByteArray.chunk(size: Int): ArrayList<ByteArray> {
    // Make sure we have enough bytes to chunk
    if (this.size < size) {
        throw IllegalArgumentException("Byte array is too small to chunk")
    }
    // use the IP address as a seed for the random number generator
    val random = Random(this.hashCode())
    val fragments = ArrayList<ByteArray>()
    var i = 0
    while (i < this.size) {
        val fragment = ByteArray(size)
        val hash = ByteArray(8)

        random.nextBytes(hash)
        // Copy the hash into the fragment
        System.arraycopy(hash, 0, fragment, 0, hash.size)
        val remaining = (this.size - hash.size) - i
        val bytes = if (remaining < size) remaining else size
        println("Remaining: $remaining, bytes: $bytes")
        System.arraycopy(this, i, fragment, i+hash.size, bytes)
        fragments.add(fragment)
        i += size
    }
    return fragments
}


enum class GameState {
    LOGIN,
    LOGOUT,
    NONE,
}
