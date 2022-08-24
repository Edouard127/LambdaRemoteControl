package com.lambda.utils

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacket
import kotlin.properties.Delegates

open class Packet(val byte: Int, val length: Int, val args: PacketBuilder) : IPacket {

    override fun getPacket(): EPacket {
        return EPacket.values()[byte]
    }

    override fun getPacket(i: Int): EPacket {
        return EPacket.values()[i]
    }


    override fun getFlags(): EFlagType {
        return when(getPacket()) {
            EPacket.EXIT-> EFlagType.BOTH
            EPacket.OK -> EFlagType.SERVER
            EPacket.HEARTBEAT -> EFlagType.SERVER
            EPacket.LOGIN -> EFlagType.SERVER
            EPacket.LOGOUT -> EFlagType.SERVER
            EPacket.ADD_WORKER -> EFlagType.SERVER
            EPacket.REMOVE_WORKER -> EFlagType.SERVER
            EPacket.GET_WORKERS -> EFlagType.CLIENT
            EPacket.JOB -> EFlagType.CLIENT
            EPacket.CHAT -> EFlagType.CLIENT
            EPacket.BARITONE -> EFlagType.CLIENT
            EPacket.LAMBDA -> EFlagType.CLIENT
            EPacket.ERROR -> EFlagType.BOTH
            EPacket.LISTENER_ADD -> EFlagType.SERVER
            EPacket.LISTENER_REMOVE -> EFlagType.SERVER
            EPacket.HIGHWAY_TOOLS -> EFlagType.CLIENT
            EPacket.SCREENSHOT -> EFlagType.CLIENT
        }
    }

    override fun getData(): ByteArray {
        return args.data
    }

    override fun getString(): String {
        return "$length 0 ${getPacket().byte} ${getFlags().byte} ${args.getString()}"
    }
}




