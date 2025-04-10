package pink.iika.pong.logic.server

import pink.iika.pong.logic.gameobject.Ball
import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.util.gameenum.ClientPacketType
import java.util.concurrent.CopyOnWriteArrayList

data class GameRoom(val name: String, val id: Int, val clients: MutableList<ClientInfo>, private val handler: ClientHandler) {
    private val logic = GameLogic(CopyOnWriteArrayList<GameObject>().apply {
        add(Ball())
        add(Paddle(side = 1))
        add(Paddle(side = 2))
    }, handler)
    private val loop = LogicLoop(logic)
    private val ackMap = mutableMapOf<ClientPacketType, MutableSet<ClientInfo>>()

    fun getAckMap(): MutableMap<ClientPacketType, MutableSet<ClientInfo>> = ackMap

    fun acknowledge(client: ClientInfo, ackType: ClientPacketType) {
        if (!hasAck(client, ackType)) {
            ackMap.getOrPut(ackType) { mutableSetOf() }.add(client)
        }
    }

    fun hasAck(client: ClientInfo, ackType: ClientPacketType): Boolean {
        return ackMap[ackType]?.contains(client) == true
    }

    fun getLogic() = logic
    fun getLoop() = loop
}