package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.CENTER

open class Label(
    override var position: Position,
    override var size: Size = Size(325F, 25F),
    override var name: () -> String = { "" },
    private val horizontalAlign: Int = CENTER,
    var color: Color = Colors.primary,
    private val textSize: Float = 16F
) : Component {

    override fun draw() {
        with(Context.applet) {
            inMatrix {
                fill(color.foreground)
                textSize(textSize)
                textAlign(horizontalAlign, CENTER)
                text(
                    name(),
                    position.x + if (horizontalAlign == CENTER) size.width / 2 else 5F,
                    position.y - 4F + size.height / 2
                )
            }
        }
    }
}
