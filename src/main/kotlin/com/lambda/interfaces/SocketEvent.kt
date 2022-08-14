package com.lambda.interfaces

import java.io.BufferedWriter

interface SocketEvent {
    fun receive(byte: Byte, vararg args: Array<String>)
    fun close(): Boolean
    fun send(data: String, bw: BufferedWriter)
}