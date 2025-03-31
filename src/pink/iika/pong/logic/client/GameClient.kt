package pink.iika.pong.logic.client

import java.net.DatagramPacket
import java.nio.ByteBuffer
import kotlin.system.exitProcess

class GameClient(private val handler: ServerHandler, private val contentPanel: ContentPanel) {
    fun startClient() {
        handler.sendPacket(byteArrayOf(ClientPacket.JOIN.ordinal.toByte()))

        handler.startReceiver { packet ->
            handlePacket(packet)
        }
    }

    fun handlePacket(packet: DatagramPacket) {
        val buffer = ByteBuffer.wrap(packet.data, 0, packet.length)
        val ordinal = buffer.get().toInt()
        val type = ServerPacket.entries[ordinal]

        when (type) {
            ServerPacket.STATE -> updateGameState(buffer)
            ServerPacket.JOIN_ACCEPTED -> sendAck()
            ServerPacket.JOIN_DENIED -> exitProcess(0)
            ServerPacket.ENABLE_START -> startGame()
        }
    }

    fun updateGameState(byteBuffer: ByteBuffer) {
        val shapeStates = mutableListOf(byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble())
        contentPanel.setState(shapeStates)
    }

    fun startGame() = handler.sendPacket(byteArrayOf(ClientPacket.START.ordinal.toByte()))
    fun sendAck() = handler.sendPacket(byteArrayOf(ClientPacket.JOIN_ACK.ordinal.toByte()))
}