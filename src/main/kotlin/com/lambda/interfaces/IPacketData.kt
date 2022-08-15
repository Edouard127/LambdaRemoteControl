package com.lambda.interfaces

import com.lambda.enums.EPacket
import com.lambda.utils.PacketDataBuilder


interface IPacketData {
    fun buildPacketData(packetData: List<ByteArray>): PacketDataBuilder
    fun defaultData(): PacketDataBuilder
    fun getDefaultPacketData(packet: EPacket): List<ByteArray>
}