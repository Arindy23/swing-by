package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.event.KeyEvent
import processing.event.MouseEvent

class Toggle(
    override var position: Position,
    override val size: Size = Size(175F, 25F),
    override val name: () -> String,
    override var shortcutKey: Int = -1,
    private var toggle: Boolean = false,
    color: Color = Colors.primaryInverted,
    colorPressed: Color = Colors.primary
) : Button(position, size, name, color, colorPressed, shortcutKey) {

    override fun mouseReleased(event: MouseEvent) {
        super.mouseReleased(event)
        if (inside(event.position())) {
            toggle()
        }
    }

    override fun onKeyPressed(event: KeyEvent) {
        super.onKeyPressed(event)
        if (event.keyCode == shortcutKey) {
            toggle()
        }
    }

    private fun toggle() {
        toggle = !toggle
        val tempColor = colorPressed
        colorPressed = color
        color = tempColor
    }

    fun value(): Boolean {
        return toggle
    }

}
