package com.lambda.interfaces

import com.lambda.enums.EPacket

interface IPacket {
    val packet: Byte
    fun getPacket(): EPacket
    fun getPacketData(): ByteArray
    fun getFlags(): FlagType
}