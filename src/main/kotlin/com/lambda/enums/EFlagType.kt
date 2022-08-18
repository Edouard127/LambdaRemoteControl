package com.lambda.enums

enum class EFlagType(val byte: Int) {
    SERVER(byte = 0x00),
    CLIENT(byte = 0x01),
    BOTH(byte = 0x02),
    NONE(byte = 0x03);
}