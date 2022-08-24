package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.ClientEvent
import com.lambda.client.event.Event
import com.lambda.enums.EFlagType
import java.io.BufferedWriter
import java.net.Socket

class SocketDataReceived(val packet: Packet, val flag: EFlagType, val socket: BufferedWriter) : Event, Cancellable() {
    fun parse(): List<String> {
        return String(packet.getData()).split(" ").drop(3)
    }
    fun parseByteArray(): ByteArray {
        return String(packet.getData()).split(" ").drop(2).joinToString(" ").encodeToByteArray()
    }
}