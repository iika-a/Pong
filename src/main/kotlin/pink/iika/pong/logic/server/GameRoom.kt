package pink.iika.pong.logic.server

import kotlinx.serialization.Serializable

@Serializable
data class GameRoom(val name: String, var id: Int, val clients: MutableList<ClientInfo>) {
    override fun toString() = name
}