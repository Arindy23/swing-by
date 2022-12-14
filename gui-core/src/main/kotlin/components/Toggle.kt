package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context.componentsHaveFocus
import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.event.KeyEvent
import processing.event.MouseEvent

class Toggle(
    override var position: () -> Position,
    override val size: Size = Size(175F, 25F),
    override val name: () -> String,
    override var shortcutKey: Int = -1,
    private var toggle: (() -> Boolean)? = null,
    color: () -> Color = { Colors.primaryInverted },
    colorPressed: () -> Color = { Colors.primary }
) : Button(
    position,
    size,
    name,
    {
        toggle?.let {
            if (it()) {
                colorPressed()
            } else {
                color()
            }
        } ?: Colors.primaryInverted
    },
    {
        toggle?.let {
            if (it()) {
                color()
            } else {
                colorPressed()
            }
        } ?: Colors.primary
    },
    shortcutKey
) {

    private var simpleToggle: Boolean = false

    override fun mouseReleased(event: MouseEvent) {
        super.mouseReleased(event)
        if (inside(event.position())) {
            doToggle()
        }
    }

    override fun onKeyPressed(event: KeyEvent) {
        super.onKeyPressed(event)
        if (!componentsHaveFocus() && event.keyCode == shortcutKey) {
            doToggle()
        }
    }

    private fun doToggle() {
        simpleToggle = !simpleToggle
        if (toggle == null) {
            val tempColor = colorPressed
            colorPressed = color
            color = tempColor
        }
    }

    fun value(): Boolean {
        return toggle?.let { it() } ?: simpleToggle
    }

}
