package de.arindy.swingby.gui.core.units

class Color(
    val background: String = "0x000000",
    val foreground: String = "0xFFFFFF",
    val selected: String = "0x00FF00",
) {

    override fun toString(): String {
        return "[background: $background, foreground: $foreground], selected: $selected"
    }
}
