package com.lambda.enums

enum class EPacket(val byte: Int) {
    EXIT(byte = 0),
    OK(byte = 1),
    HEARTBEAT(byte = 2),
    LOGIN(byte = 3),
    LOGOUT(byte = 4),
    ADD_WORKER(byte = 5),
    REMOVE_WORKER(byte = 6),
    GET_WORKERS(byte = 7),
    GET_WORKERS_STATUS(byte = 8),
    CHAT(byte = 9),
    BARITONE(byte = 10),
    LAMBDA(byte = 11),
    ERROR(byte = 12),
}