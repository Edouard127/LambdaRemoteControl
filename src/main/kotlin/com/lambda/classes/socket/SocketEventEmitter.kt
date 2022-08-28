package com.lambda.classes.socket

import com.lambda.classes.packet.Packet
import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EFlagType
import com.lambda.events.SocketDataReceived
import java.io.BufferedWriter


class SocketEventEmitter {
    fun emit(packet: Packet, flag: EFlagType, socket: BufferedWriter) {
        LambdaEventBus.post(SocketDataReceived(packet, flag, socket))
    }
}