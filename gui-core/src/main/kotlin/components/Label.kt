package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants

class Label(
    override var position: Position,
    override var size: Size = Size(100F, 25F),
    override var scale: Float = 1F,
    override var name: String,
    private val color: Color = Colors.primary
) : Component {

    override fun draw() {
        with(Context.applet) {
            inMatrix {
                fill(color.foreground)
                textSize(16F)
                textAlign(PConstants.CENTER, PConstants.CENTER)
                text(name, position.x + size.width / 2, position.y - 4F + size.height / 2)
            }
        }
    }
}
