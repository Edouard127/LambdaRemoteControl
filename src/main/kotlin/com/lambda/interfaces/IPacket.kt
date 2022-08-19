package com.lambda.interfaces

import com.lambda.enums.EFlagType
import com.lambda.enums.EPacket

interface IPacket {
    val packet: Int
    fun getPacket(): EPacket
    fun getFlags(): EFlagType
    fun getData(): ByteArray
}