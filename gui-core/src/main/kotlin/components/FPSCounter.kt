package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.CONTEXT.applet
import de.arindy.swingby.gui.core.CONTEXT.deltaTime
import de.arindy.swingby.gui.core.CONTEXT.inMatrix
import de.arindy.swingby.gui.core.CONTEXT.mousePosition
import de.arindy.swingby.gui.core.CONTEXT.resolution
import de.arindy.swingby.gui.core.CONTEXT.secondElapsed
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.core.PConstants.CENTER

class FPSCounter(
    override var position: Position = Position.ZERO,
    override var size: Size = Size(80F, 20F),
    override var scale: Float = 1F,
    override var name: String = ""
) : Component {

    override fun draw() {
        position = Position(resolution().width - 90F, 10F)
        with(applet) {
            drawText()
        }
    }

    private fun PApplet.drawText() {
        val color = if (inside(mousePosition())) {
            "0x00FFFF"
        } else {
            "0xFFFFFF"
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
