package com.lambda.interfaces

import com.lambda.enums.EPacket
import com.lambda.utils.Packet

interface IPacketBuilder {
    fun buildPacket(): Packet
    fun defaultData(packet: EPacket): ByteArray
}