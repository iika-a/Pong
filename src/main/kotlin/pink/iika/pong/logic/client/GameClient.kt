package pink.iika.pong.logic.client

import kotlinx.serialization.json.Json
import pink.iika.pong.logic.GameManager
import pink.iika.pong.logic.server.GameRoom
import pink.iika.pong.util.gameenum.ClientPacketType
import pink.iika.pong.util.gameenum.GameEvent
import pink.iika.pong.util.gameenum.ServerPacketType
import java.net.DatagramPacket
import java.nio.ByteBuffer
import kotlin.system.exitProcess

class GameClient(private val handler: ServerHandler, private val contentPanel: OnlineGamePanel, private val gameManager: GameManager) {
    private var currentRoom: GameRoom = GameRoom("placeholder", -1, mutableListOf())

    fun startClient() {
        handler.startReceiver { packet ->
            handlePacket(packet)
        }
    }

    fun stopClient() {
        handler.stopReceiver()
    }

    private fun handlePacket(packet: DatagramPacket) {
        val buffer = ByteBuffer.wrap(packet.data, 0, packet.length)
        val ordinal = buffer.get().toInt()
        val type = ServerPacketType.entries[ordinal]

        when (type) {
            ServerPacketType.GAME_TICK -> updateGameState(buffer)
            ServerPacketType.JOIN_ACCEPTED -> handler.sendPacket(ClientPacketType.JOIN_ACCEPTED_ACK, byteArrayOf())
            ServerPacketType.JOIN_DENIED -> exitProcess(0)
            ServerPacketType.ENABLE_START -> gameManager.onGameEvent(GameEvent.ENABLE_START)
            ServerPacketType.COUNTDOWN_TICK -> {}
            ServerPacketType.STOP_GAME -> gameManager.onGameEvent(GameEvent.EXIT_TO_MENU)
            ServerPacketType.ROOMS -> gameManager.onUpdateRooms(Json.decodeFromString<MutableList<GameRoom>>(String(packet.data, 1, packet.length - 1, Charsets.UTF_8)))
            ServerPacketType.ROOM_STATE -> currentRoom = Json.decodeFromString(String(packet.data, 1, packet.length - 1, Charsets.UTF_8))
        }
    }

    private fun updateGameState(byteBuffer: ByteBuffer) {
        val shapeStates = mutableListOf(byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble(), byteBuffer.getDouble())
        contentPanel.setState(shapeStates)
    }

    fun joinRoom(room: GameRoom) {
        currentRoom = room
        handler.sendPacket(ClientPacketType.JOIN_ROOM, Json.encodeToString(room).toByteArray())
    }

    fun createRoom(room: GameRoom) {
        handler.sendPacket(ClientPacketType.CREATE_ROOM, Json.encodeToString(room).toByteArray())
    }

    fun getRooms() {
        handler.sendPacket(ClientPacketType.GET_ROOMS, byteArrayOf())
    }

    fun startGame() = handler.sendPacket(ClientPacketType.START_GAME, byteArrayOf())
}