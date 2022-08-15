package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketData

class PacketData(val packet: EPacket) : IPacketData {
    override fun defaultData(): ByteArray {
        return when(this.packet) {
            EPacket.EXIT -> byteArrayOf(0x00)
            EPacket.OK -> byteArrayOf(0x00)
            EPacket.HEARTBEAT -> defaultHeartbeat
            EPacket.LOGIN -> defaultLogin
            EPacket.LOGOUT -> defaultLogout
            EPacket.ADD_WORKER -> getWorker()
            EPacket.REMOVE_WORKER -> getWorker()
            EPacket.GET_WORKERS -> defaultGetWorkers
            EPacket.GET_WORKERS_STATUS -> defaultGetWorkersStatus
            EPacket.CHAT -> defaultChat
            EPacket.BARITONE -> defaultBaritone
            EPacket.LAMBDA -> defaultLambda
            EPacket.ERROR -> defaultError
        }
    }
}