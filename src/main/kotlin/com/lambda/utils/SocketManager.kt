package com.lambda.utils

import com.lambda.client.event.Event
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.enums.EPacket
import com.lambda.interfaces.*
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

class SocketManager(server: String, port: Int, password: String, function: () -> Unit) : IGameEventManager, ISocketEvent, Event {

    private var socket: Socket
    private var outputStreamWriter: OutputStreamWriter
    private var bwriter: BufferedWriter
    private var inputStreamReader: InputStreamReader
    private var breader: BufferedReader
    private val password: String
    private val SocketEventManager = SocketEventEmitter()

    init {
        this.password = password
        this.socket = Socket(server, port)
        this.outputStreamWriter = OutputStreamWriter(this.socket.getOutputStream())
        this.bwriter = BufferedWriter(this.outputStreamWriter)
        this.inputStreamReader = InputStreamReader(this.socket.getInputStream());
        this.breader = BufferedReader(this.inputStreamReader);
        this.Connect()
    }
    private fun Connect() {
        Thread {
            try {
                val packetData = PacketDataBuilder(EPacket.ADD_WORKER, listOf(PacketData(EPacket.ADD_WORKER).defaultData()))
                val packet = PacketBuilder(EPacket.ADD_WORKER, packetData)

                send(packet.buildPacket(), getBufferedWriter())

                while(true) {
                    val line = this.breader.readLine()
                    if (line != null) {
                        val epacket = PacketUtils.getPacketId(line.split(" ")[0].toByte())

                        val packetArgs = PacketBuilder(epacket, PacketDataBuilder(epacket, line.split(" ").drop(1).map { it.toByteArray() }.toList()))

                        val packetBuilder = Packet(epacket.byte, packetArgs.data.writeData())
                        this.receive(packetBuilder)
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
        this.SocketEventManager.emit(packet, packet.getFlags())
    }


    override fun send(packet: Packet, bw: BufferedWriter) {
        try {
            val epacket = packet.getPacket()
            val packetData = PacketDataBuilder(epacket, PacketData(epacket).buildPacketData(packet.getPacketListByte()).data)

            val args = PacketBuilder(epacket, packetData)

            bw.write("${args.packet.byte} ${args.getString()}")
            bw.newLine()
            bw.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun SafeClientEvent.login(server: ServerData) {
        try {
            FMLClientHandler.instance().connectToServer(mc.currentScreen, server);
        } catch (e: Exception) {
            println("Could not login ${e.message}")
            MessageSendHelper.sendChatMessage("Could not login ${e.message}")
        }
    }
    override fun SafeClientEvent.logout(reason: String) {
        try {
            mc.connection?.networkManager?.closeChannel(TextComponentString(""))
            mc.loadWorld(null as WorldClient?)

            mc.displayGuiScreen(LambdaGuiDisconnected(arrayOf(reason), this.getScreen, true, LocalTime.now()))
        } catch (e: Exception) {
            println("Could not logout ${e.message}")
            MessageSendHelper.sendChatMessage("Could not logout ${e.message}")
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