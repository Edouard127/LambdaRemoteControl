package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.gui.mc.LambdaGuiDisconnected
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.client.FMLClientHandler
import java.io.*
import java.net.Socket
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

internal object RemoteControl : PluginModule(
    name = "Remote Control",
    description = "Control your client remotely",
    category = Category.CLIENT,
    pluginMain = SocketPlugin
) {
    private enum class PASSWORD_TYPE {
        RANDOM, NOTRANDOM
    }

    lateinit var socket: Socket
    lateinit var outputStreamWriter: OutputStreamWriter
    lateinit var bwriter: BufferedWriter
    lateinit var inputStreamReader: InputStreamReader
    lateinit var breader: BufferedReader
    val data = ArrayList<String>()
    private val passType by setting("Password Type", PASSWORD_TYPE.RANDOM)
    private val password by setting("Password", "", { passType == PASSWORD_TYPE.NOTRANDOM })
    private val server by setting("Server", "localhost")
    private val port by setting("Port", "1984")

    init {

        onEnable {
            val parsedPort = port.filter { it.isDigit() }.toInt()
            socket = Socket(server, parsedPort)
            outputStreamWriter = OutputStreamWriter(socket.getOutputStream())
            bwriter = BufferedWriter(outputStreamWriter)

            inputStreamReader = InputStreamReader(socket.getInputStream());
            breader = BufferedReader(inputStreamReader);

            val pass = when (passType) {
                PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
                PASSWORD_TYPE.NOTRANDOM -> password
            }
            Thread {
                try {
                    data.add("4")
                    data.add('"' + pass + '"')

                    try {
                        sendString(bwriter, data.toString() + "\r\n")
                    }
                    catch(e: IOException) {
                        println(e.message)
                    }
                    while(true) {
                        val line = breader.readLine()
                        if (line != null) {
                            val decompressed = line.decode()
                            println(decompressed)
                            val bit = decompressed.split(" ")
                            val args: Array<String> = bit.drop(1).toTypedArray()
                            when (bit[0]) {
                                "0" -> logout("Client received command ${bit[0]}")
                                "PING" -> sendString(bwriter, listOf("1", '"'+bit[1]+'"').toString())
                                "2" -> login(ServerData(bit[1], bit[2], false))
                                "3" -> logout("Client received command ${bit[0]}")
                                "6" -> MessageSendHelper.sendServerMessage(args.joinToString(" "))
                                "7" -> MessageSendHelper.sendBaritoneCommand(*args)
                                "8" -> CommandManager.runCommand(args.joinToString(" "))
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
        onDisable {
            try {
                socket.close()
                outputStreamWriter.close()
                bwriter.close()
                inputStreamReader.close()
                breader.close()
                data.clear()
            } catch(e: Exception){
                println("Exception: $e")
            }
        }
    }
    fun sendString(bw: BufferedWriter, str: String) {
        try {
            bw.write(str.trimIndent().encode()+"\r\n")
            bw.flush()
        } catch (e: Exception) {
            println("Exception: $e")
        }
    }

    private fun login(server: ServerData) {
        FMLClientHandler.instance().connectToServer(mc.currentScreen, server);
    }
    private fun logout(reason: String) {
        mc.connection?.networkManager?.closeChannel(TextComponentString(""))
        mc.loadWorld(null as WorldClient?)

        mc.displayGuiScreen(LambdaGuiDisconnected(arrayOf(reason), getScreen(), true, LocalTime.now()))
    }

    private fun getScreen() = if (mc.isIntegratedServerRunning) {
        GuiMainMenu()
    } else {
        GuiMultiplayer(GuiMainMenu())
    }

    fun String.encode(): String {
        return Base64.getEncoder().encodeToString(this.toByteArray())
    }
    fun String.decode(): String {
        return Base64.getDecoder().decode(this).decodeToString()
    }

}