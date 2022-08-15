package com.lambda.utils

import com.lambda.enums.EPacket

class PacketDataBuilder(override val packet: EPacket, override val data: List<ByteArray>) : IPacketDataBuilder {

    override fun writeData(): Map<Map<Int, Int>, ByteArray> {
        val map: MutableMap<Map<Int, Int>, ByteArray> = hashMapOf()

        // Return a Map with the data of the packet, the map is the offset and the length of the data
        data.forEach {
            map[mapOf(it.size to it.size)] = it
        }

        return map
    }
}


// Create an interface for the packet data
interface IPacketDataBuilder {
    val packet: EPacket
    val data: List<ByteArray>
    fun writeData(): Map<Map<Int, Int>, ByteArray>
}