package com.lambda.utils

import com.lambda.interfaces.IPacketBuilder

class PacketBuilder(val byte: Byte, val data: ByteArray) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(byte, data)
    }

}
/*fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}*/

