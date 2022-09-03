package de.arindy.swingby.gui.components

import de.arindy.swingby.gui.CONTEXT
import de.arindy.swingby.gui.CONTEXT.applet
import de.arindy.swingby.gui.Position
import de.arindy.swingby.gui.fill
import de.arindy.swingby.gui.resolution
import processing.core.PApplet

class FPSCounter : Component {

    override var position = Position.ZERO
    override var scale = 1F

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
        fill("0xFFFFFF")
        text("${frameRate.toInt()} (${CONTEXT.deltaTime})", position.x, position.y + 5F)
        popMatrix()
    }

}
