package com.lambda.utils

import com.lambda.client.event.SafeClientEvent
import com.lambda.enums.EPacket
import com.lambda.interfaces.IPacketBuilder
import com.lambda.modules.RemoteControl
import com.lambda.utils.Worker
import net.minecraft.client.Minecraft
import org.apache.commons.codec.binary.Hex
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class PacketBuilder(val byte: Byte, val data: ByteArray) : IPacketBuilder {
    override fun buildPacket(): Packet {
        return Packet(byte, data)
    }

}

val defaultHeartbeat = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
val defaultLogin = byteArrayOf("2b2t.com.de".toByte(), "25565".toByte())
val defaultLogout = byteArrayOf("Client received logout packet".toByte())
val defaultGetWorkers = byteArrayOf() //TODO: Implement this
val defaultGetWorkersStatus = byteArrayOf() //TODO: Implement this
val defaultChat = byteArrayOf("Hello World".toByte())
val defaultBaritone = byteArrayOf("help".toByte())
val defaultLambda = byteArrayOf("help".toByte())
val defaultError = byteArrayOf("Client error".toByte())

fun getWorker(): ByteArray {
    val mc = Minecraft.getMinecraft()
    return byteArrayOf(mc.player.name.toByte(), createSignature(mc.player.name, RemoteControl.secretKey))
}
fun createSignature(data: String, key: String): Byte {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    return Hex.encodeHexString(sha256Hmac.doFinal(data.toByteArray())).toByte()
}

/*fun getWorkerStatus(worker: IWorker): Byte {
    return worker.status.byte
}*/

