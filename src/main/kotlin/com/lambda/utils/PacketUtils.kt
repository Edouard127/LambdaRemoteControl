package com.lambda.utils

import com.lambda.enums.EPacket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object PacketUtils {
    fun <T> encode(packet: T): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(packet)
        oos.close()
        return baos.toByteArray()
    }
    fun <T> decode(data: ByteArray): T {
        val bais = ByteArrayInputStream(data)
        val ois = ObjectInputStream(bais)
        val packet = ois.readObject() as T
        ois.close()
        return packet
    }
    fun getPacketId(data: Byte): EPacket {
        return EPacket.values()[data.toInt()]
    }
    fun getPacketBuilder(type: EPacket, vararg data: ByteArray): PacketDataBuilder {
        /* TODO: add support for multiple data types */
        return PacketDataBuilder(type, data.toList())
    }
}