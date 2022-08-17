package com.lambda.utils

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacket

class Packet(val byte: Int, val args: ByteArray) : IPacket {
    override fun getPacket(): EPacket {
        return mapOf(
            0 to EPacket.EXIT,
            1 to EPacket.OK,
            2 to EPacket.HEARTBEAT,
            3 to EPacket.LOGIN,
            4 to EPacket.LOGOUT,
            5 to EPacket.ADD_WORKER,
            6 to EPacket.REMOVE_WORKER,
            7 to EPacket.GET_WORKERS,
            8 to EPacket.GET_WORKERS_STATUS,
            9 to EPacket.CHAT,
            10 to EPacket.BARITONE,
            11 to EPacket.LAMBDA,
            12 to EPacket.ERROR,
        )[byte] ?: EPacket.ERROR
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
    override val packet: Int
        get() = getPacket().byte
}




