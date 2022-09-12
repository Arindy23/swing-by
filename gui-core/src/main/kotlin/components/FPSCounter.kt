package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context.applet
import de.arindy.swingby.gui.core.Context.deltaTime
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.mousePosition
import de.arindy.swingby.gui.core.Context.resolution
import de.arindy.swingby.gui.core.Context.secondElapsed
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.core.PConstants.CENTER

class FPSCounter(
    override var position: Position = Position.ZERO,
    override var size: Size = Size(80F, 20F),
    override var name: () -> String = { "" }
) : Component {

    override fun draw() {
        position = Position(resolution().width - 90F, 10F)
        with(applet) {
            drawText()
        }
    }

    private fun PApplet.drawText() {
        val color = if (inside(mousePosition())) {
            Colors.secondary.selected
        } else {
            Colors.secondary.foreground
        }
        if (secondElapsed(0.1F)) {
            inMatrix {
                stroke(color)
                line(dimensions.x0, dimensions.y1, dimensions.x1, dimensions.y1)
            }
        }
        inMatrix {
            fill(color)
            textSize(15F)
            textAlign(CENTER, CENTER)
            text("${frameRate.toInt()} ($deltaTime)", position.x + 2F + size.width / 2, position.y - 2F + size.height / 2)
        }
    }

    override fun toString(): String {
        return "FPS [${applet.frameRate.toInt()}]"
    }
}
