package com.lambda.interfaces

class Packet(val byte: Byte) : IPacket {
    override fun getPacket(): EPacket {
        return mapOf(
            (0x00).toByte() to EPacket.EXIT,
            (0x01).toByte() to EPacket.OK,
            (0x02).toByte() to EPacket.HEARTBEAT,
            (0x03).toByte() to EPacket.LOGIN,
            (0x04).toByte() to EPacket.LOGOUT,
            (0x07).toByte() to EPacket.GET_WORKERS,
            (0x08).toByte() to EPacket.GET_WORKERS_STATUS,
            (0x09).toByte() to EPacket.CHAT,
            (0x0A).toByte() to EPacket.BARITONE,
            (0x0B).toByte() to EPacket.LAMBDA,
            (0x0C).toByte() to EPacket.ERROR,
        )[byte] ?: EPacket.ERROR
    }
    // Return the current byte value of the packet
    override val packet: Byte
        get() = getPacket().byte
}

interface IPacket {
    val packet: Byte
    fun getPacket(): EPacket
}
enum class EPacket(val byte: Byte) {
    EXIT(byte = 0x00),
    OK(byte = 0x01),
    HEARTBEAT(byte = 0x02),
    LOGIN(byte = 0x03),
    LOGOUT(byte = 0x04),
    GET_WORKERS(byte = 0x07),
    GET_WORKERS_STATUS(byte = 0x08),
    CHAT(byte = 0x09),
    BARITONE(byte = 0x0A),
    LAMBDA(byte = 0x0B),
    ERROR(byte = 0x0C),
}




