import java.awt.Color
import javax.swing.DefaultListModel

interface GameEventListener {
    fun onGameEvent(e: GameEvent)
    fun onGameStart(options: DefaultListModel<GameOption>)
    fun onGameEnd()
    fun onSetKeybind(key: Int, index: Int)
    fun onSetColor(color: Color, index: Int)
    fun onTogglePowerUp(type: PowerUpType, exclude: Boolean)
}