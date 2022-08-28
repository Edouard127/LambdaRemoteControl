package com.lambda.classes.packet.utils

import com.lambda.classes.packet.Packet
import com.lambda.classes.packet.PacketBuilder
import com.lambda.enums.EPacket
import com.lambda.utils.Debug
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
    fun getPacketBuilder(type: EPacket, vararg data: ByteArray): PacketBuilder {
        return PacketBuilder(type, arrByteArrayToByteArray(data as Array<ByteArray>))
    }
    fun getPacket(byte: Byte, data: ByteArray): Packet {
        Debug.blue("Packet Size:", data.size.toString())
        Debug.blue("Packet Type:", getPacketId(byte).toString())

        val type = getPacketId(byte)

        return Packet(data.size, getPacketBuilder(type, data))
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