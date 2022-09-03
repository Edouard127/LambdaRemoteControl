package com.lambda.enums

enum class EFlagType(val byte: Int) {
    SERVER(byte = 0x00),
    CLIENT(byte = 0x01),
    GAME(byte = 0x02),
    BOTH(byte = 0x03),
    SERVER_GAME(byte = 0x04),
}