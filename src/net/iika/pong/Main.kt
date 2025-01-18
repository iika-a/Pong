package net.iika.pong

import com.formdev.flatlaf.FlatLightLaf

import net.iika.pong.logic.MenuPanel
import net.iika.pong.logic.gameobject.Paddle
import net.iika.pong.logic.ScoreKeeper
import net.iika.pong.logic.gameobject.GameObject
import net.iika.pong.logic.GameManager
import net.iika.pong.logic.GamePanel
import net.iika.pong.util.gameenum.GameEvent
import net.iika.pong.util.listener.ButtonMouseListener

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
        gamePanel.setGameListener(gameManager)
        menuPanel.setGameListener(gameManager)

        gameManager.onGameEvent(GameEvent.STARTUP)
    }
}