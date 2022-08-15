package com.lambda.utils

object ArrayUtils {
    fun toStringArray(bytes: ByteArray): Array<String> {
        val strings = Array(bytes.size) { "" }
        for (i in bytes.indices) {
            strings[i] = bytes[i].toString()
        }
        return strings
    }
}