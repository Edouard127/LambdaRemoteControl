package com.lambda.utils

import com.lambda.client.event.Event
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
import com.lambda.interfaces.*
import com.lambda.modules.RemoteControl
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import java.io.*
import java.net.Socket
import java.time.LocalTime

class SocketManager(server: String, port: Int, username: String, password: String) : IGameEventManager, ISocketManager, Event {

    private lateinit var socket: Socket
    lateinit var outputStreamWriter: OutputStreamWriter
    private lateinit var bwriter: BufferedWriter
    private lateinit var inputStreamReader: InputStreamReader
    private lateinit var breader: BufferedReader
    private val password: String = password
    private val username: String = username
    private val socketEventEmitter = SocketEventEmitter()

    init {
        try {
            this.socket = Socket(server, port)
            this.outputStreamWriter = OutputStreamWriter(this.socket.getOutputStream())
            this.bwriter = BufferedWriter(this.outputStreamWriter)
            this.inputStreamReader = InputStreamReader(this.socket.getInputStream());
            this.breader = BufferedReader(this.inputStreamReader);
            this.Connect()
        } catch (e: Exception) {
            e.message?.let { Debug.error("Could not initialise the socket connection", it) }

            e.printStackTrace()
            RemoteControl.disable()
        }
    }
    private fun Connect() {
        Thread {
            try {

                val epacket = EPacket.ADD_WORKER
                val getPacket = PacketUtils.getPacketBuilder(epacket, EFlagType.CLIENT, this.username.toByteArray(), this.password.toByteArray())
                val packetBuilder = PacketBuilder(epacket, getPacket)

                send(packetBuilder.buildPacket())
                while(true) {
                    val line = this.breader.readLine()
                    if (line != null && line.isNotEmpty()) {
                        val input = line.split(" ")
                        val byte = input[0].toByte()

                        val body = input.subList(2, input.size-1).joinToString(" ").encodeToByteArray()

                        val packet = PacketUtils.getPacket(byte, body)
                        this.receive(packet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun getSocket(): Socket {
        return this.socket
    }
    fun getInputStream(): InputStreamReader {
        return this.inputStreamReader
    }
    fun getOutputStream(): OutputStreamWriter {
        return this.outputStreamWriter
    }
    fun getBufferedReader(): BufferedReader {
        return this.breader
    }
    fun getBufferedWriter(): BufferedWriter {
        return this.bwriter
    }

    override fun receive(packet: Packet) {
        this.socketEventEmitter.emit(packet, packet.getFlags(), getBufferedWriter())
    }


    override fun send(packet: Packet) {
        try {
            val epacket = packet.getPacket()
            val flag = packet.getFlags()
            val packetData = PacketDataBuilder(epacket, packet.args)

            val args = PacketBuilder(epacket, packetData)

            val bw = getBufferedWriter()
            bw.write("${args.packet.byte} ${flag.byte} ${args.getString()}")
            bw.newLine()
            bw.flush()
        } catch (e: IOException) {
            e.message?.let { Debug.error("Could not send packet", it) }
            e.printStackTrace()
        }
    }


    override fun SafeClientEvent.login(server: ServerData) {
        try {
            FMLClientHandler.instance().connectToServer(mc.currentScreen, server);
        } catch (e: Exception) {
            e.message?.let { Debug.error("Could not log in", it) }
            e.printStackTrace()
        }
    }
    override fun SafeClientEvent.logout(reason: String) {
        try {
            mc.connection?.networkManager?.closeChannel(TextComponentString(""))
            mc.loadWorld(null as WorldClient?)

            mc.displayGuiScreen(LambdaGuiDisconnected(arrayOf(reason), this.getScreen, true, LocalTime.now()))
        } catch (e: Exception) {
            e.message?.let { Debug.error("Could not log out", it) }
            e.printStackTrace()
        }
    }

    override fun close(): Boolean {
        try {
            this.socket.close()
            this.outputStreamWriter.close()
            this.bwriter.close()
            this.inputStreamReader.close()
            this.breader.close()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    override val SafeClientEvent.getScreen: GuiScreen
        get() = if(mc.isIntegratedServerRunning) GuiMainMenu() else GuiMultiplayer(GuiMainMenu())
}