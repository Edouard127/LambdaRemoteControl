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
        return PacketDataBuilder(type, arrByteArrayToByteArray(data as Array<ByteArray>))
    }
    fun getPacket(data: List<String>): Packet {
        val type = getPacketId(data[0].toByte())
        // Get the data from the index 1 onwards to a bytearray
        val d = arrByteArrayToByteArray(data.subList(1, data.size).map { it.toByteArray() }.toTypedArray())
        return Packet(type.byte, d)
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