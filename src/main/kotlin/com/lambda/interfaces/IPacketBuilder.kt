package com.lambda.interfaces

import com.lambda.classes.packet.Packet

interface IPacketBuilder {
    fun buildPacket(): Packet
    fun getString(): String

}