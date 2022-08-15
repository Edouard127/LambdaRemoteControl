package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketBuilder

class PacketBuilder(val packet: EPacket, val data: PacketDataBuilder) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(packet.byte, PacketDataBuilder(packet, data.data).writeData())
    }

    override fun getString(): String {
        // Return a string of the packet in the format "n args, offset, length, data"
        val string = StringBuilder()
        data.writeData().forEach { (t, u) ->
            string.append("${data.data.size} ${t[0]} ${t[1]} ${String(u)}\n")
        }
        return string.toString()
    }

}
/*fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}*/

