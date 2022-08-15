package com.lambda.utils

import com.lambda.enums.EFlagType


class SocketEventEmitter {
    fun emit(packet: Packet, flag: EFlagType) {
        SocketDataReceived(packet, flag)
    }
}