package com.lambda.utils

import com.lambda.client.event.Event
import com.lambda.enums.EPacket
import com.lambda.interfaces.*
import com.lambda.modules.RemoteControl
import java.io.*
import java.net.Socket

class SocketManager(server: String, port: Int, username: String, password: String) : ISocketManager, Event {

    private lateinit var socket: Socket
    lateinit var outputStreamWriter: OutputStreamWriter
    private lateinit var bwriter: BufferedWriter
    private lateinit var inputStreamReader: InputStreamReader
    private lateinit var breader: BufferedReader
    private val password: String = password
    private val username: String = username
    private val socketEventEmitter = SocketEventEmitter()

    init {
        try {
            this.socket = Socket(server, port)
            this.outputStreamWriter = OutputStreamWriter(this.socket.getOutputStream())
            this.bwriter = BufferedWriter(this.outputStreamWriter)
            this.inputStreamReader = InputStreamReader(this.socket.getInputStream());
            this.breader = BufferedReader(this.inputStreamReader);
            this.Connect()
        } catch (e: Exception) {
            e.message?.let { Debug.error("Could not initialise the socket connection", it) }

            e.printStackTrace()
            RemoteControl.disable()
        }
    }
    private fun Connect() {
        Thread {
            try {

                val epacket = EPacket.ADD_WORKER
                val getPacket = PacketUtils.getPacketBuilder(epacket, this.username.toByteArray(), this.password.toByteArray())
                val packetBuilder = PacketBuilder(epacket, getPacket.data)

                send(packetBuilder.buildPacket())
                while(true) {
                    val line = this.breader.readLine()
                    if (line != null && line.isNotEmpty()) {
                        // Remove null bytes
                        val input = line.replace("\u0000", "").split(" ")
                        println(input)
                        val byte = input[2].toByte()

                        val body = input.subList(5, input.size-1).joinToString(" ").encodeToByteArray()

                        val packet = PacketUtils.getPacket(byte, body)
                        this.receive(packet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun getSocket(): Socket {
        return this.socket
    }
    fun getInputStream(): InputStreamReader {
        return this.inputStreamReader
    }
    fun getOutputStream(): OutputStreamWriter {
        return this.outputStreamWriter
    }
    fun getBufferedReader(): BufferedReader {
        return this.breader
    }
    fun getBufferedWriter(): BufferedWriter {
        return this.bwriter
    }

    override fun receive(packet: Packet) {
        this.socketEventEmitter.emit(packet, packet.getFlags(), getBufferedWriter())
    }


    override fun send(packet: Any?) {
        try {
            when (packet) {
                is Packet -> {
                    //println("Sending packet")
                    //println(packet.getString().length)
                    this.bwriter.write(packet.getString())
                    this.bwriter.newLine()
                    this.bwriter.flush()
                }
                is FragmentedPacket -> {
                    //println("Sending fragmented packet")
                    //println(packet.getString().length)
                    this.bwriter.write(packet.getString())
                    this.bwriter.newLine()
                    this.bwriter.flush()
                }
                else -> throw Exception("Invalid packet type")
            }
        } catch (e: IOException) {
            e.message?.let { Debug.error("Could not send packet", it) }
            e.printStackTrace()
        }
    }
    override fun close(): Boolean {
        try {
            this.socket.close()
            this.outputStreamWriter.close()
            this.bwriter.close()
            this.inputStreamReader.close()
            this.breader.close()
        } catch (e: IOException) {
            return false
        }
        return true
    }

}