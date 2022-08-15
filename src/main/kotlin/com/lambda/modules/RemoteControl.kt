package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
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
    private val s = when (passType) {
        PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
        PASSWORD_TYPE.NOTRANDOM -> password
    }
    val secretKey = s.hashCode().toString()
    lateinit var socket: SocketManager

    init {

        onEnable {
            val parsedInt = port.toInt()
            socket = SocketManager(server, parsedInt, mc.player.name, s) {
                safeListener<SocketDataReceived> {
                    // TODO Execute functions
                    when(it.packet.getPacket()) {
                        EPacket.EXIT -> EFlagType.BOTH
                        EPacket.OK -> EFlagType.SERVER
                        EPacket.HEARTBEAT -> EFlagType.SERVER
                        EPacket.LOGIN -> EFlagType.SERVER
                        EPacket.LOGOUT -> EFlagType.SERVER
                        EPacket.ADD_WORKER -> EFlagType.SERVER
                        EPacket.REMOVE_WORKER -> EFlagType.SERVER
                        EPacket.GET_WORKERS -> EFlagType.CLIENT
                        EPacket.GET_WORKERS_STATUS -> EFlagType.CLIENT
                        EPacket.CHAT -> EFlagType.NONE
                        EPacket.BARITONE -> EFlagType.NONE
                        EPacket.LAMBDA -> EFlagType.NONE
                        EPacket.ERROR -> EFlagType.BOTH
                    }
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
