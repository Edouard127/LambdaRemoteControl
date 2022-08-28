package com.lambda.interfaces

import com.lambda.classes.packet.Packet

interface ISocketManager {
    fun receive(packet: Packet)
    fun close(): Boolean
    fun send(packet: Any?)
}