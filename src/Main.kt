import java.util.ArrayList
import java.awt.event.KeyEvent
import javax.swing.OverlayLayout
import javax.swing.SwingUtilities
import javax.swing.JFrame
import javax.swing.JPanel

fun main() {
    val gameObjectList = ArrayList<GameObject>()

    val ball = Ball()
    gameObjectList.add(ball)
    val paddle1 = Paddle(side = 2, leftKey = KeyEvent.VK_A, rightKey = KeyEvent.VK_D)
    gameObjectList.add(paddle1)
    val paddle2 = Paddle(side = 1, leftKey = KeyEvent.VK_LEFT, rightKey = KeyEvent.VK_RIGHT)
    gameObjectList.add(paddle2)
    val scoreKeeper = ScoreKeeper(0.0, 0.0)

    SwingUtilities.invokeLater {
        val gameFrame = JFrame("iika's Pong")
        gameFrame.setSize(1280, 720)
        gameFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        gameFrame.isResizable = false

        val buttonMouseListener = ButtonMouseListener()

        val gamePanel = GamePanel(gameObjectList, scoreKeeper, buttonMouseListener)
        gamePanel.layout = null
        gamePanel.isVisible = true
        gamePanel.isFocusable = true

        val menuPanel = MenuPanel(scoreKeeper, buttonMouseListener)
        menuPanel.isVisible = true
        menuPanel.isFocusable = true

        val contentPane = JPanel()
        contentPane.layout = OverlayLayout(contentPane)
        contentPane.add(menuPanel)
        contentPane.add(gamePanel)

        gameFrame.contentPane = contentPane
        gameFrame.isVisible = true

        gamePanel.initializeComponents()

        val gameManager = GameManager(gamePanel, menuPanel, scoreKeeper, gameObjectList)
        gamePanel.setGameEventListener(gameManager)
        menuPanel.setGameEventListener(gameManager)

        gameManager.onGameEvent(GameEvent.EXIT_TO_MENU)
    }
}