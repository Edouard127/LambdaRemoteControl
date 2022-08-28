package com.lambda.events

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import com.lambda.enums.EFlagType
import com.lambda.classes.packet.Packet
import java.io.BufferedWriter

class SocketDataReceived(val packet: Packet, val flag: EFlagType, val socket: BufferedWriter) : Event, Cancellable() {
    fun parse(): List<String> {
        return String(packet.getData()).split(" ")
    }
    fun parseByteArray(): ByteArray {
        return packet.getData()
    }
}