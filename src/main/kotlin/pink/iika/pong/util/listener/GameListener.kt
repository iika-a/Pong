package pink.iika.pong.util.listener

import pink.iika.pong.logic.server.GameRoom
import pink.iika.pong.util.gameenum.GameOption
import pink.iika.pong.util.gameenum.PowerUpType
import pink.iika.pong.util.gameenum.GameEvent
import java.awt.Color
import javax.swing.DefaultListModel

interface GameListener {
    fun onGameEvent(e: GameEvent)
    fun onGameStart(options: DefaultListModel<GameOption>)
    fun onGameEnd()
    fun onSetKeybind(key: Int, index: Int)
    fun onSetColor(color: Color, index: Int)
    fun onTogglePowerUp(type: PowerUpType, exclude: Boolean)
    fun onJoinRoom(room: GameRoom)
    fun onUpdateRooms(roomList: MutableList<GameRoom>)
    fun onCreateRoom(room: GameRoom)
}