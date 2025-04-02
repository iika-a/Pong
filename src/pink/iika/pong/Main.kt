package pink.iika.pong

import com.formdev.flatlaf.FlatLightLaf

import pink.iika.pong.logic.MenuPanel
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.logic.ScoreKeeper
import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.logic.GameManager
import pink.iika.pong.logic.GamePanel
import pink.iika.pong.logic.client.OnlineGamePanel
import pink.iika.pong.logic.client.ServerHandler
import pink.iika.pong.logic.gameobject.Ball
import pink.iika.pong.util.gameenum.GameEvent
import pink.iika.pong.util.listener.ButtonMouseListener

import java.util.concurrent.CopyOnWriteArrayList
import java.awt.event.KeyEvent
import javax.swing.OverlayLayout
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException
import javax.swing.JFrame
import javax.swing.JPanel

fun main() {
    val gameObjectList = CopyOnWriteArrayList<GameObject>()

    val paddle1 = Paddle(side = 2, leftKey = KeyEvent.VK_A, rightKey = KeyEvent.VK_D)
    gameObjectList.add(paddle1)
    val paddle2 = Paddle(side = 1, leftKey = KeyEvent.VK_LEFT, rightKey = KeyEvent.VK_RIGHT)
    gameObjectList.add(paddle2)
    val scoreKeeper = ScoreKeeper(0.0, 0.0)

    try {
        UIManager.setLookAndFeel(FlatLightLaf())
    } catch (e: UnsupportedLookAndFeelException) {
        e.printStackTrace()
        System.err.println("Failed to initialize FlatLaf.")
    }

    SwingUtilities.invokeLater {
        val gameFrame = JFrame("iika's Pong")
        gameFrame.setSize(1920, 1080)
        gameFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        gameFrame.isResizable = false
        gameFrame.focusTraversalKeysEnabled = false

        val buttonMouseListener = ButtonMouseListener()

        val gamePanel = GamePanel(gameObjectList, scoreKeeper, buttonMouseListener)
        gamePanel.layout = null
        gamePanel.isVisible = true
        gamePanel.isFocusable = true

        val handler = ServerHandler(2438)

        val onlineGamePanel = OnlineGamePanel(handler, CopyOnWriteArrayList<GameObject>().apply {
            add(Ball())
            add(Paddle(side = 1))
            add(Paddle(side = 2))
        })
        onlineGamePanel.layout = null
        onlineGamePanel.isVisible = false
        onlineGamePanel.isFocusable = true
        onlineGamePanel.addKeyListener(onlineGamePanel)

        val menuPanel = MenuPanel(scoreKeeper, buttonMouseListener)
        menuPanel.isVisible = true
        menuPanel.isFocusable = true

        val contentPane = JPanel()
        contentPane.layout = OverlayLayout(contentPane)
        contentPane.add(menuPanel)
        contentPane.add(gamePanel)
        contentPane.add(onlineGamePanel)

        gameFrame.contentPane = contentPane
        gameFrame.isVisible = true

        gameFrame.addWindowFocusListener(object : java.awt.event.WindowFocusListener {
            override fun windowGainedFocus(e: java.awt.event.WindowEvent?) {
                if (gamePanel.isVisible) gamePanel.requestFocusInWindow()
                else if (onlineGamePanel.isVisible) onlineGamePanel.requestFocusInWindow()
                else menuPanel.requestFocusInWindow()
            }

            //not needed
            override fun windowLostFocus(e: java.awt.event.WindowEvent?) {
            }
        })

        gamePanel.initializeComponents()

        val gameManager = GameManager(gamePanel, menuPanel, onlineGamePanel, scoreKeeper, gameObjectList, handler)
        gamePanel.setGameListener(gameManager)
        menuPanel.setGameListener(gameManager)
        onlineGamePanel.setGameListener(gameManager)

        gameManager.onGameEvent(GameEvent.EXIT_TO_MENU)
    }
}