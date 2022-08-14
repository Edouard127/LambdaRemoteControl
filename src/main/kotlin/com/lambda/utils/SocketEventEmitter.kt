package com.lambda.utils

import com.lambda.interfaces.FlagType
import com.lambda.interfaces.Packet


class SocketEventEmitter {
    fun emit(bit: Packet, flag: FlagType, vararg args: Array<String>) {
        SocketDataReceived(bit, flag, *args)
    }
}