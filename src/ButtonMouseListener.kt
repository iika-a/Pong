import java.awt.Color
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JButton
import javax.swing.border.LineBorder

class ButtonMouseListener: MouseListener {
    override fun mouseEntered(e: MouseEvent?) {
        when (e?.source) {
            is JButton -> {
                val button = e.source as JButton
                if (button.isEnabled) {
                    button.background = Color(0xADD2DB)
                    button.border = LineBorder(Color(0x888888), 3)
                }
            }
        }
    }

    override fun mouseExited(e: MouseEvent?) {
        when (e?.source) {
            is JButton -> {
                val button = e.source as JButton
                if (button.isEnabled) {
                    button.background = Color(0xD1F6FF)
                    button.border = LineBorder(Color.WHITE, 3)
                }
            }
        }
    }

    //no implementation needed
    override fun mouseClicked(e: MouseEvent?) {}
    override fun mousePressed(e: MouseEvent?) {}
    override fun mouseReleased(e: MouseEvent?) {}
}