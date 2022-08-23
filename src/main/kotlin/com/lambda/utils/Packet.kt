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
            8 to EPacket.JOB,
            9 to EPacket.CHAT,
            10 to EPacket.BARITONE,
            11 to EPacket.LAMBDA,
            12 to EPacket.ERROR,
            15 to EPacket.HIGHWAY_TOOLS
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
            EPacket.JOB -> EFlagType.CLIENT
            EPacket.CHAT -> EFlagType.CLIENT
            EPacket.BARITONE -> EFlagType.CLIENT
            EPacket.LAMBDA -> EFlagType.CLIENT
            EPacket.ERROR -> EFlagType.BOTH
            EPacket.HIGHWAY_TOOLS -> EFlagType.CLIENT
        }
    }

    override fun getData(): ByteArray {
        return args
    }


    // Return the current byte value of the packet
    override val packet: Int
        get() = getPacket().byte
}




