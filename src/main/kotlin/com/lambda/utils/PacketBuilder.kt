package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketBuilder

class PacketBuilder(val packet: EPacket, val data: ByteArray) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(data.size, this)
    }

    override fun getString(): String {
        return String(data)
    }

}
/*fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}*/

