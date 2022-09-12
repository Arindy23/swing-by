package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context.applet
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.resolution
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Dimensions
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.BASELINE
import processing.core.PConstants.LEFT
import processing.event.KeyEvent
import processing.event.MouseEvent

interface Component {

    val position: Position
    val size: Size
    val componentName: String
        get() = this.javaClass.simpleName
    val name: () -> String
    val dimensions: Dimensions
        get() = Dimensions(position, size)

    fun draw() {
        with(applet) {
            inMatrix {
                fill(Colors.warn.foreground)
                textSize(15F)
                textAlign(LEFT, BASELINE)
                text("${asString()} has no draw Method!", 20F, resolution().height - 20F)
            }
        }
    }

    fun onKeyPressed(event: KeyEvent) {}

    fun mouseReleased(event: MouseEvent) {}

    fun mousePressed(event: MouseEvent) {}

    fun mouseDragged(event: MouseEvent) {}

    fun isFocused(): Boolean {
        return false
    }

    fun inside(position: Position): Boolean {
        val dimensions = dimensions
        return dimensions.x0 < position.x
            && dimensions.x1 > position.x
            && dimensions.y0 < position.y
            && dimensions.y1 > position.y
    }

    fun asString(): String {
        return "$componentName[\"$name\", ($dimensions)]"
    }

    fun hasShortcut(): Boolean {
        return false
    }

}
