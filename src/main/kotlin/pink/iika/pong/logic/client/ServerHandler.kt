package pink.iika.pong.logic.client

import pink.iika.pong.util.gameenum.ClientPacketType
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class ServerHandler(private val port: Int) {
    private val socket = DatagramSocket()
    private val address: InetAddress = InetAddress.getByName("172.16.96.221")

    fun startReceiver(onPacket: (DatagramPacket) -> Unit) {
        thread(start = true) {
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    onPacket(packet)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun sendPacket(header: ClientPacketType, data: ByteArray) {
        try {
            val payload = byteArrayOf(header.ordinal.toByte()) + data
            val packet = DatagramPacket(payload, payload.size, address, port)
            socket.send(packet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}