package com.lambda.interfaces

import com.lambda.utils.Packet
import java.io.BufferedWriter

interface ISocketEvent {
    fun receive(packet: Packet)
    fun close(): Boolean
    fun send(packet: Packet, bw: BufferedWriter)
}