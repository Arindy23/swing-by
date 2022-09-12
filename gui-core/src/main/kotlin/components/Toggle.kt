package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.event.MouseEvent

class Toggle(
    override var position: Position,
    override val size: Size = Size(175F, 25F),
    override val name: () -> String,
    private var toggle: Boolean = false,
    color: Color = Colors.primaryInverted,
    colorPressed: Color = Colors.primary
) : Button(position, size, name, color, colorPressed) {

    override fun mouseReleased(event: MouseEvent) {
        super.mouseReleased(event)
        if (inside(event.position())) {
            toggle = !toggle
            val tempColor = colorPressed
            colorPressed = color
            color = tempColor
        }
    }

    fun value(): Boolean {
        return toggle
    }

}
