package com.lambda.utils

import com.lambda.client.event.Event
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.interfaces.GameEventManager
import com.lambda.interfaces.SocketEvent
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

class SocketManager(server: String, port: Int, password: String) : GameEventManager, SocketEvent, Event {

    private var socket: Socket
    private var outputStreamWriter: OutputStreamWriter
    private var bwriter: BufferedWriter
    private var inputStreamReader: InputStreamReader
    private var breader: BufferedReader
    private var data: List<String>
    private val password: String
    private val SocketEventManager = SocketEventEmitter()

    init {
        this.password = password
        this.socket = Socket(server, port)
        this.outputStreamWriter = OutputStreamWriter(socket.getOutputStream())
        this.bwriter = BufferedWriter(outputStreamWriter)
        this.inputStreamReader = InputStreamReader(socket.getInputStream());
        this.breader = BufferedReader(inputStreamReader);
        this.data = listOf("4", '"'+this.password+'"')
    }
    fun init() {
        this.getBufferedWriter().write("ifrh4fuiorhjforhio")
        Thread {
            try {

                this.send(this.data.toString(), this.bwriter)

                while(true) {
                    val line = breader.readLine()
                    if (line != null) {
                        val decompressed = line.decode()
                        val bit = decompressed.split(" ")
                        val args: Array<String> = bit.drop(1).toTypedArray()

                        this.receive(bit, args)
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

    override fun receive(bit: List<String>, vararg args: Array<String>) {
        this.SocketEventManager.emit(bit, *args)
    }

    override fun send(data: String, bw: BufferedWriter) {
        bw.write("cyiurhe4fuirht4ufhr4ueo")
        try {
            bw.write(data.trimIndent().encode()+"\r\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun String.encode(): String {
        return Base64.getEncoder().encodeToString(this.toByteArray())
    }

    override fun String.decode(): String {
        return Base64.getDecoder().decode(this).decodeToString()
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
            this.data = listOf()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    override val SafeClientEvent.getScreen: GuiScreen
        get() = if(mc.isIntegratedServerRunning) GuiMainMenu() else GuiMultiplayer(GuiMainMenu())
}