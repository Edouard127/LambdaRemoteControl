package com.lambda.utils

import com.lambda.enums.EPacket

class PacketDataBuilder(override val packet: EPacket, override val data: List<ByteArray>) : IPacketDataBuilder {

    override fun writeData(): HashMap<Pair<Int, Int>, ByteArray> {
        val map = HashMap<Pair<Int, Int>, ByteArray>()

        for (i in data.indices) {
            val packetDataLength = data[i].size
            map[i to packetDataLength] = data[i]
        }
        return map
    }
}


// Create an interface for the packet data
interface IPacketDataBuilder {
    val packet: EPacket
    val data: List<ByteArray>
    fun writeData(): HashMap<Pair<Int, Int>, ByteArray>
}