package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketData
import com.lambda.modules.RemoteControl
import net.minecraft.client.Minecraft
import org.apache.commons.codec.binary.Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class PacketData(val packet: EPacket) : IPacketData {
    override fun defaultData(): PacketDataBuilder {
        return PacketDataBuilder(this.packet, getDefaultPacketData(packet))
    }

    override fun buildPacketData(packetData: List<ByteArray>): PacketDataBuilder {
        return PacketDataBuilder(this.packet, packetData)
    }
    override fun getDefaultPacketData(packet: EPacket): List<ByteArray> {
        // TODO: Implement this method
        return when (packet) {
            EPacket.EXIT -> listOf(byteArrayOf(0x00))
            EPacket.OK -> listOf(byteArrayOf(0x00))
            EPacket.HEARTBEAT -> listOf(byteArrayOf(0x00))
            EPacket.LOGIN -> listOf(byteArrayOf(0x00))
            EPacket.LOGOUT -> listOf(byteArrayOf(0x00))
            EPacket.ADD_WORKER -> listOf(createWorker())
            EPacket.REMOVE_WORKER -> listOf(createWorker())
            EPacket.GET_WORKERS -> listOf(byteArrayOf(0x00))
            EPacket.GET_WORKERS_STATUS -> listOf(byteArrayOf(0x00))
            EPacket.CHAT -> listOf(byteArrayOf(0x00))
            EPacket.BARITONE -> listOf(byteArrayOf(0x00))
            EPacket.LAMBDA -> listOf(byteArrayOf(0x00))
            EPacket.ERROR -> listOf(byteArrayOf(0x00))

        }
    }
}

val defaultHeartbeat = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
val defaultLogin = "2b2t.com.de 25565".encodeToByteArray()
val defaultLogout = "Client received logout packet".encodeToByteArray()
val defaultGetWorkers = byteArrayOf() //TODO: Implement this
val defaultGetWorkersStatus = byteArrayOf() //TODO: Implement this
val defaultChat = "Hello World".encodeToByteArray()
val defaultBaritone = "help".encodeToByteArray()
val defaultLambda = "help".encodeToByteArray()
val defaultError = "Client error".encodeToByteArray()




/*fun getWorker(): ByteArray {
    val worker = RemoteControl.getWorker()
    val workerBytes = ByteArray(worker.size)
    for (i in 0 until worker.size) {
        workerBytes[i] = worker[i].toByte()
    }
    return workerBytes
}*/
fun createWorker(worker: String = "Kamigen"): ByteArray {
    val workerBytes = ByteArray(worker.length)
    for (i in worker.indices) {
        workerBytes[i] = worker[i].code.toByte()
    }
    return workerBytes
}
fun createSignature(data: String, key: String): ByteArray {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    return Hex.encodeHexString(sha256Hmac.doFinal(data.toByteArray())).encodeToByteArray()
}
