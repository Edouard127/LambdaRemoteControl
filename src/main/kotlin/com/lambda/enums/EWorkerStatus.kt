package com.lambda.enums


enum class EWorkerStatus(val byte: Byte) {
    BUSY(byte = 0x00),
    IDLE(byte = 0x01)
}