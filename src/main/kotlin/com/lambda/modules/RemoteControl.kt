package com.lambda.modules

import com.lambda.SocketPlugin
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import com.lambda.interfaces.EPacket
import com.lambda.interfaces.FlagType
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

    init {

        onEnable {
            val pass = when (passType) {
                PASSWORD_TYPE.RANDOM -> UUID.randomUUID().toString()
                PASSWORD_TYPE.NOTRANDOM -> password
            }
            val parsedInt = port.toInt()
            SocketManager(server, parsedInt, mc.player.name, pass) {
                safeListener<SocketDataReceived> {
                    // TODO Execute functions
                    when(it.bit.byte) {
                        EPacket.EXIT.byte -> FlagType.BOTH
                        EPacket.OK.byte -> FlagType.SERVER
                        EPacket.HEARTBEAT.byte -> FlagType.SERVER
                        EPacket.LOGIN.byte -> FlagType.SERVER
                        EPacket.LOGOUT.byte -> FlagType.SERVER
                        EPacket.GET_WORKERS.byte -> FlagType.CLIENT
                        EPacket.GET_WORKERS_STATUS.byte -> FlagType.CLIENT
                        EPacket.CHAT.byte -> FlagType.NONE
                        EPacket.BARITONE.byte -> FlagType.NONE
                        EPacket.LAMBDA.byte -> FlagType.NONE
                        EPacket.ERROR.byte -> FlagType.BOTH
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
