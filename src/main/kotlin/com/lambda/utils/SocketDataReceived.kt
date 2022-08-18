package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.ClientEvent
import com.lambda.client.event.Event
import com.lambda.enums.EFlagType
import java.io.BufferedWriter
import java.net.Socket

class SocketDataReceived(val packet: Packet, val flag: EFlagType, val socket: BufferedWriter) : Event, Cancellable() {
    fun parse(): List<String> {
        return String(packet.args).split(" ").drop(3)
    }
}