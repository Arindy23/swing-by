package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context.applet
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.CENTER

open class Label(
    override var position: () -> Position,
    override var size: Size = Size(325F, 25F),
    override var name: () -> String,
    private val horizontalAlign: Int = CENTER,
    var color: () -> Color = { Colors.primary },
    private val textSize: Float = 16F,
    private val border: Boolean = false
) : Component {

    override fun draw() {
        with(applet) {
            inMatrix {
                textSize(textSize)
                if (border) {
                    stroke(color().foreground)
                    noFill()
                    rect(position(), size())
                }
                fill(color().foreground)
                textAlign(horizontalAlign, CENTER)
                text(
                    name(),
                    position().x + if (horizontalAlign == CENTER) size.width / 2 else 5F,
                    position().y - 4F + size.height / 2
                )
            }
        }
    }

    private fun size(): Size {
        var result: Size = size
        applet.inMatrix {
            applet.textSize(textSize)
            result = Size(applet.textWidth(name()) + textSize, (textSize * 1.4).toFloat())
        }
        return result
    }
}
