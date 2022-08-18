package com.lambda.interfaces

import com.lambda.utils.Packet

interface ISocketManager {
    fun receive(packet: Packet)
    fun close(): Boolean
    fun send(packet: Packet)
}