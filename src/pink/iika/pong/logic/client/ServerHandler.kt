package pink.iika.pong.logic.client

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class ServerHandler(private val port: Int) {
    val socket = DatagramSocket()
    val address: InetAddress = InetAddress.getByName("172.20.10.8")

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

    fun sendPacket(data: ByteArray) {
        try {
            val packet = DatagramPacket(data, data.size, address, port)
            socket.send(packet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}