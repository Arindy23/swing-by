package de.arindy.swingby.gui.components

import de.arindy.swingby.gui.CONTEXT
import de.arindy.swingby.gui.CONTEXT.applet
import de.arindy.swingby.gui.Position
import de.arindy.swingby.gui.Size
import de.arindy.swingby.gui.fill
import de.arindy.swingby.gui.mousePosition
import de.arindy.swingby.gui.resolution
import processing.core.PApplet

class FPSCounter(
    override var position: Position = Position.ZERO,
    override var size: Size = Size(80F, 10F),
    override var scale: Float = 1F
) : Component {

    init {
        CONTEXT.register(this)
    }

    override fun draw() {
        position = Position(resolution().width - 90F, 20F)
        with(applet) {
            drawText()
        }
    }

    private fun PApplet.drawText() {
        pushMatrix()
        textSize(15F)
        if (inside(mousePosition())) {
            fill("0x00FFFF")
        } else {
            fill("0xFFFFFF")
        }
        text("${frameRate.toInt()} (${CONTEXT.deltaTime})", position.x, position.y)
        popMatrix()
    }

}
