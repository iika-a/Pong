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
    private val controls = mutableListOf(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT)

    override fun paintComponent(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        super.paintComponent(g2d)

        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    g2d.color = Color.BLACK
                    g2d.fill(Ellipse2D.Double((gameObject.xPosition - 2) * scaleX, (gameObject.yPosition - 2) * scaleY, (gameObject.width + 4) * scaleX, (gameObject.height + 4) * scaleY))
                    g2d.color = colors[3]
                    g2d.fill(Ellipse2D.Double(gameObject.xPosition * scaleX, gameObject.yPosition * scaleY, gameObject.width * scaleX, gameObject.height * scaleY))
                }

                is Paddle -> {
                    g2d.color = Color.BLACK
                    when (gameObject.side) {
                        2 -> {
                            g2d.fill(Rectangle2D.Double((gameObject.xPosition - 2) * scaleX, 0.0, (gameObject.width + 4) * scaleX, (gameObject.paddleHeight + 2) * scaleY))
                            g2d.color = colors[1]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition * scaleX, 0.0, gameObject.width * scaleX, gameObject.paddleHeight * scaleY))
                        }

                        1 -> {
                            g2d.fill(Rectangle2D.Double((gameObject.xPosition - 2) * scaleX, this.height - gameObject.paddleHeight * scaleY - 2, (gameObject.width + 4) * scaleX, (gameObject.paddleHeight + 4) * scaleY))
                            g2d.color = colors[0]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition * scaleX, this.height - (gameObject.paddleHeight) * scaleY, gameObject.width * scaleX, gameObject.paddleHeight * scaleY))
                        }
                    }
                }
            }
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            controls[0] -> handler.sendPacket(ClientPacketType.PADDLE_LEFT_START, byteArrayOf())
            controls[1] -> handler.sendPacket(ClientPacketType.PADDLE_RIGHT_START, byteArrayOf())
            KeyEvent.VK_ESCAPE -> handler.sendPacket(ClientPacketType.EXIT_ROOM, byteArrayOf())
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        when (e?.keyCode) {
            controls[0] -> handler.sendPacket(ClientPacketType.PADDLE_LEFT_END, byteArrayOf())
            controls[1] -> handler.sendPacket(ClientPacketType.PADDLE_RIGHT_END, byteArrayOf())
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
    fun setLeftControl(left: Int) { controls[0] = left }
    fun setRightControl(right: Int) { controls[1] = right }
}