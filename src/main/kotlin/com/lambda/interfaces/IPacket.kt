package com.lambda.interfaces

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket

interface IPacket {
    val packet: Byte
    fun getPacket(): EPacket
    fun getPacketData(): Map<Map<Int, Int>, ByteArray>
    fun getPacketListByte(): List<ByteArray>
    fun getFlags(): EFlagType
}