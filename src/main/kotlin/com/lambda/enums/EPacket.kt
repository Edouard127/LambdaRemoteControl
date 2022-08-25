package com.lambda.enums

enum class EPacket(val byte: Int) {
    EXIT(byte = 0x00),
    OK(byte = 0x01),
    HEARTBEAT(byte = 0x02),
    LOGIN(byte = 0x03),
    LOGOUT(byte = 0x04),
    ADD_WORKER(byte = 0x05),
    REMOVE_WORKER(byte = 0x06),
    GET_WORKERS(byte = 0x07),
    JOB(byte = 0x08),
    CHAT(byte = 0x09),
    BARITONE(byte = 0x0A),
    LAMBDA(byte = 0x0B),
    ERROR(byte = 0x0C),
    LISTENER_ADD(byte = 0x0D),
    LISTENER_REMOVE(byte = 0x0E),
    HIGHWAY_TOOLS(byte = 0x0F),
    SCREENSHOT(byte = 0x10),
    GET_JOBS(byte = 0x11),
    ROTATE(byte = 0x12),
}