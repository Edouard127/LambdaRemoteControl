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
    fun Byte.intify(): Byte = (this - 0x30).toByte()
    fun getPacketId(data: Byte): EPacket {
        return EPacket.values()[data.toInt()]
    }
    fun getPacketBuilder(type: EPacket, vararg data: ByteArray): PacketDataBuilder {
        /* TODO: add support for multiple data types */
        return PacketDataBuilder(type, arrByteArrayToByteArray(data as Array<ByteArray>))
    }
    fun getPacket(byte: Byte, data: ByteArray): Packet {
        println("Packet Size: ${data.size}")
        println("Packet Type: ${getPacketId(byte)}")

        val type = getPacketId(byte)

        return Packet(type.byte, data)
    }
    fun arrByteArrayToByteArray(arr: Array<ByteArray>): ByteArray {
        val baos = ByteArrayOutputStream()
        // Add the length of each array and append it to the first position of the array
        for (i in arr.indices) {
            // Add the length of the array to the first position of the array
            // if (i == 0) baos.write(arr[i].size)
            // Write a whitespace between each array
            if (i != 0) baos.write(' '.code)

            baos.write(arr[i])
        }
        return baos.toByteArray()
    }

}