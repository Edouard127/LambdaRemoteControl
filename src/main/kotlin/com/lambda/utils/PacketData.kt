package com.lambda.utils

import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketData
import com.lambda.modules.RemoteControl
import net.minecraft.client.Minecraft
import org.apache.commons.codec.binary.Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class PacketData(val packet: EPacket) : IPacketData {
    override fun defaultData(): ByteArray {
        return when(this.packet) {
            EPacket.EXIT -> byteArrayOf(0x00)
            EPacket.OK -> byteArrayOf(0x00)
            EPacket.HEARTBEAT -> defaultHeartbeat
            EPacket.LOGIN -> defaultLogin
            EPacket.LOGOUT -> defaultLogout
            EPacket.ADD_WORKER -> getWorker()
            EPacket.REMOVE_WORKER -> getWorker()
            EPacket.GET_WORKERS -> defaultGetWorkers
            EPacket.GET_WORKERS_STATUS -> defaultGetWorkersStatus
            EPacket.CHAT -> defaultChat
            EPacket.BARITONE -> defaultBaritone
            EPacket.LAMBDA -> defaultLambda
            EPacket.ERROR -> defaultError
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


fun getWorker(): ArrayList<ByteArray> {
    val data = ArrayList<ByteArray>()
    data.add("Kamigen".encodeToByteArray())
    data.add(createSignature("Kamigen", "Kamigen"))
    return data
}
fun createSignature(data: String, key: String): ByteArray {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    return Hex.encodeHexString(sha256Hmac.doFinal(data.toByteArray())).encodeToByteArray()
}
