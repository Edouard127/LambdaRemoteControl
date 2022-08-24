package com.lambda.interfaces

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket

interface IPacket {
    fun getPacket(): EPacket
    fun getPacket(i: Int): EPacket
    fun getFlags(): EFlagType
    fun getData(): ByteArray
    fun getString(): String
    fun getPacketLength(): Int
}