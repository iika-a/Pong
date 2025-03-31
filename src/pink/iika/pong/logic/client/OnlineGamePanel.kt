package pink.iika.pong.logic.client

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import javax.swing.SwingUtilities

class OnlineGamePanel(private val handler: ServerHandler): JPanel(), KeyListener {
    private var shapes = listOf(Shape(100.0, 100.0), Shape(500.0, 500.0))

    override fun paintComponent(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        super.paintComponent(g2d)

        g2d.color = Color.BLACK
        g2d.fill(Rectangle2D.Double(shapes[0].x- 2, shapes[0].y - 2, 54.0, 54.0))
        g2d.fill(Ellipse2D.Double(shapes[1].x - 2, shapes[1].y - 2, 54.0, 54.0))

        g2d.color = Color(0xE4A8CA)
        g2d.fill(Rectangle2D.Double(shapes[0].x, shapes[0].y, 50.0, 50.0))
        g2d.color = Color(0xCCAA8C)
        g2d.fill(Ellipse2D.Double(shapes[1].x, shapes[1].y, 50.0, 50.0))
    }

    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_UP -> handler.sendPacket(byteArrayOf(ClientPacket.UP_START.ordinal.toByte()))
            KeyEvent.VK_DOWN -> handler.sendPacket(byteArrayOf(ClientPacket.DOWN_START.ordinal.toByte()))
            KeyEvent.VK_LEFT -> handler.sendPacket(byteArrayOf(ClientPacket.LEFT_START.ordinal.toByte()))
            KeyEvent.VK_RIGHT -> handler.sendPacket(byteArrayOf(ClientPacket.RIGHT_START.ordinal.toByte()))
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_UP -> handler.sendPacket(byteArrayOf(ClientPacket.UP_END.ordinal.toByte()))
            KeyEvent.VK_DOWN -> handler.sendPacket(byteArrayOf(ClientPacket.DOWN_END.ordinal.toByte()))
            KeyEvent.VK_LEFT -> handler.sendPacket(byteArrayOf(ClientPacket.LEFT_END.ordinal.toByte()))
            KeyEvent.VK_RIGHT -> handler.sendPacket(byteArrayOf(ClientPacket.RIGHT_END.ordinal.toByte()))
        }
    }

    fun setState(shapeStates: MutableList<Double>) {
        shapes[0].x = shapeStates[0]
        shapes[0].y = shapeStates[1]
        shapes[1].x = shapeStates[2]
        shapes[1].y = shapeStates[3]

        println(shapes)

        SwingUtilities.invokeLater {
            this.repaint()
        }
    }

    //implementation not needed
    override fun keyTyped(e: KeyEvent?) {}
}