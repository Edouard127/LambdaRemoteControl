package com.lambda.utils

import com.lambda.enums.EHighwayArguments
import com.lambda.interfaces.IHighwayToolsHandler
import com.lambda.utils.PacketUtils.intify
import java.util.*
import kotlin.reflect.KTypeParameter

class HighwayToolsHandler(val args: ByteArray) : IHighwayToolsHandler {
    override fun getPacket(): EHighwayArguments {
        return EHighwayArguments.values()[args[0].intify().toInt()]
    }
    override fun getArguments(): Array<String> {
        return String(args.copyOfRange(2, args.size)).split(" ").toTypedArray()
    }

    override fun createJob(args: Job) {
        
    }
}
enum class HighwayMode(val byte: Int) {
    HIGHWAY(byte = 0x00),
    FLAT(byte = 0x01),
    TUNNEL(byte = 0x02)
}

enum class DisableMode(val byte: Int) {
    NONE(byte = 0x00),
    ANTI_AFK(byte = 0x01),
    LOGOUT(byte = 0x02)
}

