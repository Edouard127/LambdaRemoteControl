package com.lambda.interfaces

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket

interface IPacket {
    val packet: Byte
    fun getPacket(): EPacket
    fun getPacketData(): HashMap<Pair<Int, Int>, ByteArray>
    fun getPacketListByte(): List<ByteArray>
    fun getFlags(): EFlagType
}