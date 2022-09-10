package de.arindy.swingby.gui.core.units

import de.arindy.swingby.gui.core.color
import de.arindy.swingby.gui.core.invalidHex

class Color(background: String, foreground: String, selected: String) {

    val background: String
    val foreground: String
    val selected: String

    init {
        this.background = color(background)
        this.foreground = color(foreground)
        this.selected = color(selected)
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
