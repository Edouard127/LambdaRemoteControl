package com.lambda.enums

enum class EJobEvents(val byte: Int) {
    JOB_STARTED(byte = 0x00),
    JOB_FINISHED(byte = 0x01),
    JOB_FAILED(byte = 0x02),
    JOB_PAUSED(byte = 0x03),
    JOB_RESUMED(byte = 0x04),
    JOB_CANCELLED(byte = 0x05),
    JOB_SCHEDULED(byte = 0x06),
    JOB_STUCK(byte = 0x07),
}