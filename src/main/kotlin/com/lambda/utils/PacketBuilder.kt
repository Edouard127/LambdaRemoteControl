package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketBuilder

class PacketBuilder(val packet: EPacket, val data: PacketDataBuilder) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(packet.byte, PacketDataBuilder(packet, data.data).data)
    }

    override fun getString(): String {
        return String(data.data)
    }

}
/*fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}*/

