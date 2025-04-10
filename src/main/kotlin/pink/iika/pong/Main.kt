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
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font

import java.util.concurrent.CopyOnWriteArrayList
import java.awt.event.KeyEvent
import javax.swing.*

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

        val resolutionFrame = JFrame("Resolution Select")
        resolutionFrame.setSize(300, 125)
        resolutionFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        resolutionFrame.isResizable = false
        val resolutionPanel = JPanel().apply { background = Color(0xFFD1DC); layout = BorderLayout() }
        resolutionPanel.add(JPanel().apply { background = Color(0xFFD1DC) }, BorderLayout.NORTH)
        resolutionPanel.add(JLabel("Select Resolution:").apply { setLabelSettings(this) } , BorderLayout.CENTER)

        val bottomPanel = JPanel().apply { background = Color(0xFFD1DC) }
        val comboBox = JComboBox<String>().apply {
            addItem("1280 x 720")
            addItem("1600 x 900")
            addItem("1920 x 1080")
            addItem("2560 x 1440")
            addItem("3840 x 2160")
            selectedItem = "1920 x 1080"
        }
        bottomPanel.add(comboBox)
        bottomPanel.add(JButton("Confirm").apply { addActionListener {
            var scale = 1.0
            when (comboBox.selectedItem) {
                "1280 x 720" ->  scale = 2/3.0
                "1600 x 900" -> scale = 5/6.0
                "1920 x 1080" -> scale = 1.0
                "2560 x 1440" -> scale = 4/3.0
                "3840 x 2160" -> scale = 2.0
            }

            gamePanel.setScale(scale, scale)
            menuPanel.setScale(scale, scale)
            onlineGamePanel.setScale(scale, scale)
            gameFrame.setSize((1920 * scale).toInt(), (1080 * scale).toInt())
            resolutionFrame.dispose()
            gameFrame.isVisible = true
        }})
        resolutionPanel.add(bottomPanel, BorderLayout.SOUTH)

        resolutionFrame.add(resolutionPanel)
        resolutionFrame.isVisible = true
        resolutionFrame.setLocationRelativeTo(null)
    }
}

private fun setLabelSettings(label: JLabel) {
    val labelSize = Dimension(275, 85)
    val menuFont = Font("Segoe UI", 0, 20)

    label.preferredSize = labelSize
    label.font = menuFont
    label.horizontalAlignment = JLabel.CENTER
    label.verticalAlignment = JLabel.TOP
}