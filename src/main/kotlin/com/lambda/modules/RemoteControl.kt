package com.lambda.modules

import baritone.api.utils.Helper.mc
import com.lambda.SocketPlugin
import com.lambda.classes.packet.Fragment
import com.lambda.classes.packet.FragmentedPacket
import com.lambda.classes.packet.Packet
import com.lambda.classes.packet.PacketBuilder
import com.lambda.classes.socket.SocketManager
import com.lambda.classes.worker.Job
import com.lambda.classes.worker.utils.JobTracker
import com.lambda.classes.worker.utils.JobUtils
import com.lambda.client.command.CommandManager
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.event.listener.listener
import com.lambda.client.manager.managers.FriendManager
import com.lambda.client.module.Category
import com.lambda.client.module.modules.combat.AutoLog
import com.lambda.client.module.modules.combat.AutoLog.log
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.EntityUtils.isFakeOrSelf
import com.lambda.client.util.items.originalName
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EJobEvents
import com.lambda.enums.EPacket
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import com.lambda.events.*
import com.lambda.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ScreenShotHelper
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.ceil


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
    private val rUtils = RotationUtils()
    private val fUtils = FriendUtils()
    private var gameState = GameState.NONE
    private var serverData: ServerData? = null
    private var getScreenShot = false
    private var jobTracker: JobTracker? = null

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

        safeListener<SocketDataReceived> { it ->
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
                EPacket.INFORMATION -> {
                    val epacket = it.packet.getPacket()
                    val playerInfo = playerInformations().encodeToByteArray()
                    val packetBuilder = PacketBuilder(epacket, playerInfo)
                    val packet = Packet(playerInfo.size, packetBuilder)
                    socket.send(packet)
                }
                EPacket.JOB -> {
                    // TODO
                }
                EPacket.CHAT -> {
                    MessageSendHelper.sendServerMessage(args.joinToString(" "))
                }
                EPacket.BARITONE -> {
                    jUtils.currentJob()?.run {
                        // TODO: hacky fix for baritone
                    } ?: run {
                        MessageSendHelper.sendBaritoneCommand(args.joinToString(" "))
                    }
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
                EPacket.GET_JOBS -> {
                    val epacket = it.packet.getPacket()
                    val jobsInfo = jUtils.getJobsString().encodeToByteArray()
                    val packetBuilder = PacketBuilder(epacket, jobsInfo)
                    val packet = Packet(jobsInfo.size, packetBuilder)
                    socket.send(packet)
                }
                EPacket.ROTATE -> {
                    val playerRotation = rUtils.getRotation(args[0], args[1])
                    player.rotationYaw = playerRotation.x
                    player.rotationPitch = playerRotation.y

                    val epacket = it.packet.getPacket()
                    val rotation = it.packet.getData()
                    val packetBuilder = PacketBuilder(epacket, rotation)
                    val packet = Packet(rotation.size, packetBuilder)
                    socket.send(packet)
                }
            }
        }
        safeListener<TickEvent.ClientTickEvent> {
            jUtils.checkJobs()
            bUtils.pathingGoalCheck()
            // TODO: Worker settings
            for (entity in world.loadedEntityList) {
                if (entity !is EntityPlayer) continue
                if (entity.isFakeOrSelf) continue
                if (!fUtils.isFriend(entity)) continue
                gameState = GameState.LOGOUT
            }
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

                // Compress buffer image


                val bImage = bufferImage.compress(360, 640).toByteArray("png")


                // TODO: Find better way to get the length of the free memory
                val pLength = PacketBuilder(EPacket.SCREENSHOT, byteArrayOf()).buildPacket().getPacketLength()

                val size = 1024-pLength


                val chunks = bImage.chunk(size)

                val fragmentOffsets = List(chunks.size) { i ->
                    i * size
                }
                val hashCode = bImage.hashCode()
                val chunkFragments = chunks.map { bytes ->
                    Fragment(
                    fragment = bytes,
                    offset = fragmentOffsets.sum(),
                    length = size,
                    hash = hashCode,
                    sum = chunks.sumOf { it.size }
                ) }


                println("Chunks: ${chunks.size}")

                chunks.forEach {
                    val packetBuilder = PacketBuilder(EPacket.SCREENSHOT, it)
                    // TODO: Get the exact length of the packet
                    val packet = Packet(pLength+size, packetBuilder)
                    val fragmentPacket = FragmentedPacket(packet, chunkFragments)
                    socket.send(fragmentPacket)
                }
            }
        }
        safeListener<JobEvents> {
            if (it.instance is Job) {
                    val epacket = EPacket.JOB
                    val jobInfo = it.instance.getJob().encodeToByteArray()
                    val packetBuilder = PacketBuilder(epacket, jobInfo)
                    val packet = Packet(jobInfo.size, packetBuilder)
                    socket.send(packet)
            }
            // TODO: Make this better
            if (it.instance == null) {
                val epacket = EPacket.JOB
                val jobInfo = byteArrayOf(EWorkerStatus.IDLE.byte)
                val packetBuilder = PacketBuilder(epacket, jobInfo)
                val packet = Packet(jobInfo.size, packetBuilder)
                socket.send(packet)
            }
        }
        safeListener<StartPathingEvent> {
            val job = JobTracker(Job(
                type = EWorkerType.BARITONE,
                goal = it.goal,
                entity = this.player
            ))
            jUtils.addJob(job)
        }
        safeListener<StopPathingEvent> {
            jUtils.currentJob()?.run {
                this.job.end()
            }
        }
        safeListener<UpdatePathingEvent> {
            jUtils.currentJob()?.run {
                if (this.isStuck()) {
                    this.job.emitEvent(EJobEvents.JOB_STUCK)
                }
            }
        }
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

fun SafeClientEvent.playerInformations(): String =
                "Player:${mc.player.name} " +
                "Health:${mc.player.health} " +
                "Food:${mc.player.foodStats.foodLevel} " +
                "PlayersRender:${mc.world.playerEntities.size} " +
                "Coordinates:${mc.player.position} " +
                "MainHand:${mc.player.heldItemMainhand.originalName} " +
                "OffHand:${mc.player.heldItemOffhand.originalName} "+
                if(player.isSprinting) "Sprinting " else "" +
                if(mc.player.isSneaking) "Sneaking " else ""+
                inventory()+
                if(hasArmor()) armorInformations() else ""+
                serverData()
fun SafeClientEvent.armorInformations(): String = mc.player.armorInventoryList.reversed().joinToString(" ") { "${it.originalName}:${MathUtils.round((it.maxDamage - it.itemDamage) / it.maxDamage.toFloat() * 100.0f, 1).toFloat()}" }
fun SafeClientEvent.hasArmor(): Boolean = mc.player.armorInventoryList.any { !it.isEmpty }
fun SafeClientEvent.inventory(): String = "Inventory "+player.inventory.mainInventory.joinToString(separator = " ") { it.originalName }
fun SafeClientEvent.serverData(): String {
    player.server?.let {
        return "Players:${it.playerList.players.size} "+
                "MaxPlayers:${it.playerList.maxPlayers} "
    }
    return "No server data"
}

fun BufferedImage.compress(w: Int, h: Int): BufferedImage {
    val img: BufferedImage = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    img.graphics.drawImage(this.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null)
    return img
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
    // val random = Random(this.hashCode())
    val fragments = ArrayList<ByteArray>()
    val nFragments = ceil((this.size / size).toDouble())

    for (i in 0 until nFragments.toInt()) {
        val fragment = ByteArray(size)
        val remaining = this.size - i * size
        val bytes = if (remaining < size) remaining else size

        // println("Remaining: $remaining Bytes: $bytes")
        try {
            System.arraycopy(this, i * bytes, fragment, 0, bytes)
        } catch (e: Exception) {
            e.printStackTrace()
            break
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            break
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
            break
        }
        fragments.add(fragment)
    }
    return fragments
}



enum class GameState {
    LOGIN,
    LOGOUT,
    NONE
}
