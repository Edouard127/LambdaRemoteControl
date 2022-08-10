package com.lambda.interfaces

import java.io.BufferedWriter

interface SocketEvent {
    fun receive(bit: List<String>, vararg args: Array<String>)
    fun close(): Boolean
    fun send(data: String, bw: BufferedWriter)
    fun String.encode(): String
    fun String.decode(): String
}