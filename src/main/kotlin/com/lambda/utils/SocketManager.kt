package com.lambda.utils

import com.lambda.client.event.Event
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.util.text.MessageSendHelper
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
import java.util.*

class SocketManager(server: String, port: Int, password: String, username: String, function: () -> Unit) : GameEventManager, SocketEvent, Event {

    private var socket: Socket
    private var outputStreamWriter: OutputStreamWriter
    private var bwriter: BufferedWriter
    private var inputStreamReader: InputStreamReader
    private var breader: BufferedReader
    private var data: String
    private val password: String
    private val SocketEventManager = SocketEventEmitter()

    init {
        this.password = password
        this.socket = Socket(server, port)
        this.outputStreamWriter = OutputStreamWriter(this.socket.getOutputStream())
        this.bwriter = BufferedWriter(this.outputStreamWriter)
        this.inputStreamReader = InputStreamReader(this.socket.getInputStream());
        this.breader = BufferedReader(this.inputStreamReader);
        this.data = "4 $username $password"
        this.Connect()

    }
    fun Connect() {
        Thread {
            try {
                send(this.data, getBufferedWriter())

                while(true) {
                    val line = this.breader.readLine()
                    if (line != null) {
                        val bit = line.split(" ")
                        val args: Array<String> = bit.drop(1).toTypedArray()

                        this.receive(bit[0].toByte(), args)
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

    override fun receive(byte: Byte, vararg args: Array<String>) {
        val packet = Packet(byte)
        this.SocketEventManager.emit(packet, when(packet.getPacket()) {
            EPacket.EXIT-> FlagType.BOTH
            EPacket.OK -> FlagType.SERVER
            EPacket.HEARTBEAT -> FlagType.SERVER
            EPacket.LOGIN -> FlagType.SERVER
            EPacket.LOGOUT -> FlagType.SERVER
            EPacket.GET_WORKERS -> FlagType.CLIENT
            EPacket.GET_WORKERS_STATUS -> FlagType.CLIENT
            EPacket.CHAT -> FlagType.NONE
            EPacket.BARITONE -> FlagType.NONE
            EPacket.LAMBDA -> FlagType.NONE
            EPacket.ERROR -> FlagType.BOTH
            else -> FlagType.NONE
        }, *args)
    }
    override fun send(data: String, bw: BufferedWriter) {
        try {
            bw.write(data)
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
            this.data = ""
        } catch (e: IOException) {
            return false
        }
        return true
    }

    override val SafeClientEvent.getScreen: GuiScreen
        get() = if(mc.isIntegratedServerRunning) GuiMainMenu() else GuiMultiplayer(GuiMainMenu())
}