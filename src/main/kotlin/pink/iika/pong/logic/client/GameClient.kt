package pink.iika.pong.logic.client

import pink.iika.pong.logic.GameManager
import pink.iika.pong.util.gameenum.ClientPacketType
import pink.iika.pong.util.gameenum.GameEvent
import pink.iika.pong.util.gameenum.ServerPacketType
import java.net.DatagramPacket
import java.nio.ByteBuffer
import kotlin.system.exitProcess

class GameClient(private val handler: ServerHandler, private val contentPanel: OnlineGamePanel, private val gameManager: GameManager) {
    fun startClient() {
        handler.sendPacket(byteArrayOf(ClientPacketType.JOIN_LOBBY.ordinal.toByte()))

        handler.startReceiver { packet ->
            handlePacket(packet)
        }
    }

    private fun handlePacket(packet: DatagramPacket) {
        val buffer = ByteBuffer.wrap(packet.data, 0, packet.length)
        val ordinal = buffer.get().toInt()
        val type = ServerPacketType.entries[ordinal]

        when (type) {
            ServerPacketType.GAME_TICK -> updateGameState(buffer)
            ServerPacketType.JOIN_ACCEPTED -> sendAck()
            ServerPacketType.JOIN_DENIED -> exitProcess(0)
            ServerPacketType.ENABLE_START -> gameManager.onGameEvent(GameEvent.ENABLE_START)
            ServerPacketType.COUNTDOWN_TICK -> {}
            ServerPacketType.STOP_GAME -> {}
            ServerPacketType.ROOMS -> {}
        }
    }

    private fun updateGameState(byteBuffer: ByteBuffer) {
        val shapeStates = mutableListOf(byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble())
        contentPanel.setState(shapeStates)
    }

    fun startGame() = handler.sendPacket(byteArrayOf(ClientPacketType.START_GAME.ordinal.toByte()))
    private fun sendAck() = handler.sendPacket(byteArrayOf(ClientPacketType.JOIN_ACCEPTED_ACK.ordinal.toByte()))
}