package com.lambda.utils

import com.lambda.enums.EPacket

class PacketDataBuilder(override val packet: EPacket, override val data: ByteArray) : IPacketDataBuilder


// Create an interface for the packet data
interface IPacketDataBuilder {
    val packet: EPacket
    val data: ByteArray
}