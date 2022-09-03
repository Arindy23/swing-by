package de.arindy.swingby.gui.components

import de.arindy.swingby.gui.CONTEXT.applet
import de.arindy.swingby.gui.Position
import de.arindy.swingby.gui.fill
import de.arindy.swingby.gui.resolution
import processing.event.KeyEvent
import processing.event.MouseEvent

interface Component {

    var position: Position
    var scale: Float
    val name: String
        get() = this.javaClass.name

    fun draw() {
        with(applet) {
            pushMatrix()
            translate(20F, resolution().height - 20F)
            fill("0xFF0000")
            textSize(30F)
            text("$name has no draw Method!", 0F, 0F)
            popMatrix()
        }
    }

    fun onKeyPressed(event: KeyEvent) {}

    fun mouseReleased(event: MouseEvent) {}

}
