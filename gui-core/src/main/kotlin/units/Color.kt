package de.arindy.swingby.gui.core.units

import de.arindy.swingby.gui.core.asHexColor
import de.arindy.swingby.gui.core.invalidHex

class Color(background: String, foreground: String, selected: String) {

    val background: String
    val foreground: String
    val selected: String

    init {
        this.background = asHexColor(background)
        this.foreground = asHexColor(foreground)
        this.selected = asHexColor(selected)
    }

    override fun toString(): String {
        return "[background: $background, foreground: $foreground], selected: $selected"
    }

    fun withAlpha(alpha: String): Color {
        if (alpha.length != 2 && alpha.any { invalidHex(it) }) {
            throw IllegalArgumentException("$alpha is not a valid string")
        }
        return Color(
            background = this.background.substring(0, 9) + alpha,
            foreground = this.foreground.substring(0, 9) + alpha,
            selected = this.selected.substring(0, 9) + alpha,
        )
    }

}
