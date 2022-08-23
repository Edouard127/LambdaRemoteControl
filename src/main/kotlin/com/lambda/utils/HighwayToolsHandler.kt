package com.lambda.utils

import com.lambda.enums.EHighwayArguments
import com.lambda.interfaces.IHighwayToolsHandler
import java.util.*
import kotlin.reflect.KTypeParameter

class HighwayToolsHandler(val args: ByteArray) : IHighwayToolsHandler {
    override fun getPacket(): EHighwayArguments {
        return EHighwayArguments.values()[args[0].toInt()]
    }
    override fun getArguments(): Array<String> {
        println(String(args.copyOfRange(2, args.size-1)).split(" ").toTypedArray())
        return String(args.copyOfRange(2, args.size-1)).split(" ").toTypedArray()
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

