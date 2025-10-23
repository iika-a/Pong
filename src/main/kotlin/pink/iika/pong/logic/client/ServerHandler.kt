package pink.iika.pong.logic.client

import pink.iika.pong.util.gameenum.ClientPacketType
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import kotlin.concurrent.thread

class ServerHandler(private val port: Int) {
    private var socket = DatagramSocket()
    private val address: InetAddress = InetAddress.getByName("172.16.96.221")
    @Volatile
    private var isReceiving = false
    private var receiverThread: Thread? = null

    fun startReceiver(onPacket: (DatagramPacket) -> Unit) {
        if (isReceiving) return
        isReceiving = true

        receiverThread = thread(start = true) {
            val buffer = ByteArray(1024)
            while (isReceiving) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    onPacket(packet)
                } catch (e: SocketException) {
                    if (isReceiving) e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopReceiver() {
        isReceiving = false
        socket.close()
        socket = DatagramSocket()
        receiverThread?.join(1000)
        receiverThread = null
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