package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.command.CommandManager
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.text.MessageSendHelper.sendServerMessage
import com.lambda.client.util.threads.safeListener
import com.lambda.utils.SocketDataReceived
import com.lambda.utils.SocketManager
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
    lateinit var SocketManager: SocketManager
    private val passType by setting("Password Type", PASSWORD_TYPE.RANDOM)
    private val password by setting("Password", "", { passType == PASSWORD_TYPE.NOTRANDOM })
    private val server by setting("Server", "localhost")
    private val port by setting("Port", "1984")

    init {

        onEnable {
            val pass = when (passType) {
                PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
                PASSWORD_TYPE.NOTRANDOM -> password
            }
            val parsedInt = port.toInt()
            SocketManager = SocketManager(server, parsedInt, pass)

            SocketManager.init()

            safeListener<SocketDataReceived> {
                when (it.bit[0]) {
                    //"0" -> logout("Client received command ${bit[0]}")
                    "PING" -> SocketManager.send(listOf("1", '"'+it.bit[1]+'"').toString(), SocketManager.getBufferedWriter())
                    //"2" -> login(ServerData(bit[1], bit[2], false))
                    //"3" -> logout("Client received command ${bit[0]}")
                    "6" -> MessageSendHelper.sendServerMessage(it.args.joinToString(" "))
                    "7" -> MessageSendHelper.sendBaritoneCommand(it.args.toString())
                    "8" -> CommandManager.runCommand(it.args.joinToString(" "))
                }
            }

        }
        onDisable {
            try {
                SocketManager.close()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
