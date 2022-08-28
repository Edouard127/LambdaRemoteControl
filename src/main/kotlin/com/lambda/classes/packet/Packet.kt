package com.lambda.classes.packet

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacket

open class Packet(val length: Int, val builder: PacketBuilder) : IPacket {

    override fun getPacket(): EPacket {
        return EPacket.values()[builder.packet.byte]
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
            EPacket.INFORMATIONS -> EFlagType.CLIENT
            EPacket.JOB -> EFlagType.CLIENT
            EPacket.CHAT -> EFlagType.CLIENT
            EPacket.BARITONE -> EFlagType.CLIENT
            EPacket.LAMBDA -> EFlagType.CLIENT
            EPacket.ERROR -> EFlagType.BOTH
            EPacket.LISTENER_ADD -> EFlagType.SERVER
            EPacket.LISTENER_REMOVE -> EFlagType.SERVER
            EPacket.HIGHWAY_TOOLS -> EFlagType.CLIENT
            EPacket.SCREENSHOT -> EFlagType.CLIENT
            EPacket.GET_JOBS -> EFlagType.CLIENT
            EPacket.ROTATE -> EFlagType.CLIENT
        }
    }

    override fun getData(): ByteArray {
        return builder.data
    }

    override fun getPacketLength(): Int {
        return "${builder.data.size} 0 ${getPacket().byte} ${getFlags().byte}".encodeToByteArray().size
    }
    override fun getString(): String {
        return "${builder.data.size} 0 ${getPacket().byte} ${getFlags().byte} ${builder.getString()}"
    }
}




