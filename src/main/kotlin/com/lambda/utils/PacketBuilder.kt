package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketBuilder
import com.lambda.interfaces.IWorker

class PacketBuilder(val byte: Byte, val data: ByteArray) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(byte, data)
    }
    override fun defaultData(packet: EPacket): ByteArray {
        return when(packet) {
            EPacket.EXIT -> byteArrayOf(0x00)
            EPacket.OK -> byteArrayOf(0x00)
            EPacket.HEARTBEAT -> defaultHeartbeat
            EPacket.LOGIN -> defaultLogin
            EPacket.LOGOUT -> defaultLogout
            EPacket.GET_WORKERS -> defaultGetWorkers
        }
    }
}

val defaultHeartbeat = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
val defaultLogin = byteArrayOf("2b2t.com.de".toByte(), "25565".toByte())
val defaultLogout = byteArrayOf("Client received logout packet".toByte())
val defaultGetWorkers = byteArrayOf()

fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}

