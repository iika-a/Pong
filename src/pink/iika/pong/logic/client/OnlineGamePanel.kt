package pink.iika.pong.logic.client

import pink.iika.pong.logic.gameobject.Ball
import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.util.gameenum.ClientPacketType
import pink.iika.pong.util.listener.GameListener
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JPanel
import javax.swing.SwingUtilities

class OnlineGamePanel(private val handler: ServerHandler, private val gameObjectList: CopyOnWriteArrayList<GameObject>): JPanel(), KeyListener {
    private var scaleX = 1.0
    private var scaleY = 1.0
    private var colors = arrayOf(Color(0xE4A8CA), Color(0xCCAA87), Color(0xBB6588), Color(0x8889CC))
    private var gameManager: GameListener? = null

    override fun paintComponent(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        super.paintComponent(g2d)

        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    g2d.color = Color.BLACK
                    g2d.fill(Ellipse2D.Double(gameObject.xPosition - 2, gameObject.yPosition - 2, gameObject.width + 4, gameObject.height + 4))
                    g2d.color = colors[3]
                    g2d.fill(Ellipse2D.Double(gameObject.xPosition, gameObject.yPosition, gameObject.width, gameObject.height))
                }

                is Paddle -> {
                    g2d.color = Color.BLACK
                    when (gameObject.side) {
                        2 -> {
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition - 2, 0.0, gameObject.width + 4, gameObject.paddleHeight + 2))
                            g2d.color = colors[1]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition, 0.0, gameObject.width, gameObject.paddleHeight))
                        }

                        1 -> {
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition - 2, this.height - gameObject.paddleHeight - 2, gameObject.width + 4, gameObject.paddleHeight + 4))
                            g2d.color = colors[0]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition, this.height - gameObject.paddleHeight, gameObject.width, gameObject.paddleHeight))
                        }
                    }
                }
            }
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_LEFT -> handler.sendPacket(byteArrayOf(ClientPacketType.PADDLE_LEFT_START.ordinal.toByte()))
            KeyEvent.VK_RIGHT -> handler.sendPacket(byteArrayOf(ClientPacketType.PADDLE_RIGHT_START.ordinal.toByte()))
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_LEFT -> handler.sendPacket(byteArrayOf(ClientPacketType.PADDLE_LEFT_END.ordinal.toByte()))
            KeyEvent.VK_RIGHT -> handler.sendPacket(byteArrayOf(ClientPacketType.PADDLE_RIGHT_END.ordinal.toByte()))
        }
    }

    fun setState(gameStates: MutableList<Double>) {
        gameObjectList[0].xPosition = gameStates[0]
        gameObjectList[0].yPosition = gameStates[1]
        gameObjectList[1].xPosition = gameStates[2]
        gameObjectList[2].xPosition = gameStates[3]

        SwingUtilities.invokeLater {
            this.requestFocusInWindow()
            this.isVisible = true
            this.repaint()
        }
    }

    //implementation not needed
    override fun keyTyped(e: KeyEvent?) {}
    fun setGameListener(listener: GameListener) { this.gameManager = listener }
    fun setColors(colors: Array<Color>) { this.colors = colors }
    fun setScale(sx: Double, sy: Double) { scaleX = sx; scaleY = sy; repaint() }
}