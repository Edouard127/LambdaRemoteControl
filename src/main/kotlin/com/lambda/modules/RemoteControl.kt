package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
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
            socket = SocketManager(server, parsedInt, "Kamigen", s) {
                safeListener<SocketDataReceived> {
                    // TODO Execute functions
                    /*when(it.packet.getPacket()) {
                        EPacket.EXIT -> FlagType.BOTH
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
                    }*/
                }
            }

        }
        onDisable {
            try {
                //SocketManager.close()
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
