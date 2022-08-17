package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.ClientEvent
import com.lambda.client.event.Event
import com.lambda.enums.EFlagType

class SocketDataReceived(val packet: Packet, val flag: EFlagType) : Event, Cancellable() {
    fun parse(): List<String> {
        return String(packet.args).split(" ").drop(3)
    }
}