package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.client.event.ListenerManager
import com.lambda.enums.EFlagType
import java.io.BufferedWriter
import java.net.Socket


class SocketEventEmitter {
    fun emit(packet: Packet, flag: EFlagType, socket: BufferedWriter) {
        LambdaEventBus.post(SocketDataReceived(packet, flag, socket))
    }
}