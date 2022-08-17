package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.client.event.ListenerManager
import com.lambda.enums.EFlagType


class SocketEventEmitter {
    fun emit(packet: Packet, flag: EFlagType) {
        LambdaEventBus.post(SocketDataReceived(packet, flag))
    }
}