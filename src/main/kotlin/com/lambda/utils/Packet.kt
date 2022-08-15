package com.lambda.utils

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacket

class Packet(val byte: Byte, val args: Map<Map<Int, Int>, ByteArray>) : IPacket {
    override fun getPacket(): EPacket {
        return mapOf(
            (0x00).toByte() to EPacket.EXIT,
            (0x01).toByte() to EPacket.OK,
            (0x02).toByte() to EPacket.HEARTBEAT,
            (0x03).toByte() to EPacket.LOGIN,
            (0x04).toByte() to EPacket.LOGOUT,
            (0x07).toByte() to EPacket.GET_WORKERS,
            (0x08).toByte() to EPacket.GET_WORKERS_STATUS,
            (0x09).toByte() to EPacket.CHAT,
            (0x0A).toByte() to EPacket.BARITONE,
            (0x0B).toByte() to EPacket.LAMBDA,
            (0x0C).toByte() to EPacket.ERROR,
        )[byte] ?: EPacket.ERROR
    }

    override fun getPacketData(): Map<Map<Int, Int>, ByteArray> {
        return args
    }

    override fun getPacketListByte(): List<ByteArray> {
        return args.values.toList()
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
            EPacket.GET_WORKERS_STATUS -> EFlagType.CLIENT
            EPacket.CHAT -> EFlagType.NONE
            EPacket.BARITONE -> EFlagType.NONE
            EPacket.LAMBDA -> EFlagType.NONE
            EPacket.ERROR -> EFlagType.BOTH
        }
    }


    // Return the current byte value of the packet
    override val packet: Byte
        get() = getPacket().byte
}




