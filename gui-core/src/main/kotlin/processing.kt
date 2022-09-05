package de.arindy.swingby.gui.core

import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.event.MouseEvent

fun PApplet.fill(color: String) {
    val hexCode = color(color).substring(2)
    this.fill(
        hexCode.substring(0, 2).toInt(16).toFloat(),
        hexCode.substring(2, 4).toInt(16).toFloat(),
        hexCode.substring(4, 6).toInt(16).toFloat(),
        hexCode.substring(6).toInt(16).toFloat()
    )
}

fun PApplet.stroke(color: String) {
    val hexCode = color(color).substring(2)
    this.stroke(
        hexCode.substring(0, 2).toInt(16).toFloat(),
        hexCode.substring(2, 4).toInt(16).toFloat(),
        hexCode.substring(4, 6).toInt(16).toFloat(),
        hexCode.substring(6).toInt(16).toFloat()
    )
}

fun color(color: String): String {
    if (notAValidHexCode(color)) {
        throw IllegalArgumentException("\"$color\" is not a valid color!")
    }
    return color.padEnd(10, 'F')
}

private fun notAValidHexCode(color: String) =
    !color.startsWith("0x")
        || color.length > 10
        || color.length < 3
        || color.substring(2).any { !validHex(it) }

fun validHex(char: Char): Boolean {
    return char.toString().matches(Regex("[0-9a-fA-F]"))
}

fun PApplet.line(x1: Number, y1: Number, x2: Number, y2: Number) {
    this.line(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
}

fun PApplet.rect(size: Size, position: Position) {
    this.rect(position.x, position.y, size.width, size.height)
}

fun PApplet.translate(position: Position) {
    this.translate(position.x, position.y)
}

fun MouseEvent.position(): Position {
    return Position(this.x.toFloat(), this.y.toFloat())
}
