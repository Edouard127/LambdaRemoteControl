package com.lambda.enums

enum class EPacket(val byte: Byte) {
    EXIT(byte = 0x00),
    OK(byte = 0x01),
    HEARTBEAT(byte = 0x02),
    LOGIN(byte = 0x03),
    LOGOUT(byte = 0x04),
    ADD_WORKER(byte = 0x05),
    REMOVE_WORKER(byte = 0x06),
    GET_WORKERS(byte = 0x07),
    GET_WORKERS_STATUS(byte = 0x08),
    CHAT(byte = 0x09),
    BARITONE(byte = 0x0A),
    LAMBDA(byte = 0x0B),
    ERROR(byte = 0x0C),
}