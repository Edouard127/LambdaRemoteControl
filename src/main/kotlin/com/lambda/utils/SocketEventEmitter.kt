package com.lambda.utils


class SocketEventEmitter {
    fun emit(bit: List<String>, vararg args: Array<String>) {
        SocketDataReceived(bit, *args)
    }
}