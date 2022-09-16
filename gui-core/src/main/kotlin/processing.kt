package de.arindy.swingby.gui.core

import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.event.MouseEvent

fun PApplet.fill(color: String) {
    val hexCode = color(color).substring(2)
    this.fill(hexCode.toFloat(0, 2), hexCode.toFloat(2, 4), hexCode.toFloat(4, 6), hexCode.toFloat(6))
}

fun PApplet.stroke(color: String) {
    val hexCode = color(color).substring(2)
    this.stroke(hexCode.toFloat(0, 2), hexCode.toFloat(2, 4), hexCode.toFloat(4, 6), hexCode.toFloat(6))
}

private fun String.toFloat(startIndex: Int, endIndex: Int = this.length): Float {
    return this.substring(startIndex, endIndex).toInt(16).toFloat()
}

fun color(color: String): String {
    if (notAValidHexCode(color)) {
        throw IllegalArgumentException("\"$color\" is not a valid color!")
    }
    return color.padEnd(10, 'F')
}

fun notAValidHexCode(color: String) =
    !color.startsWith("0x")
        || color.length > 10
        || color.length < 3
        || color.substring(2).any { invalidHex(it) }

fun invalidHex(char: Char): Boolean {
    return !char.toString().matches(Regex("[0-9a-fA-F]"))
}

fun PApplet.line(start: Position, end: Position) {
    this.line(start.x, start.y, end.x, end.y)
}

fun PApplet.line(x1: Number, y1: Number, x2: Number, y2: Number) {
    this.line(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
}

fun PApplet.rect(position: Position, size: Size) {
    this.rect(position.x, position.y, size.width, size.height)
}

fun PApplet.translate(position: Position) {
    Context.currentTranslation = position
    this.translate(position.x, position.y)
}

fun PApplet.scaleWithContext(scale: Float) {
    Context.currentScale = scale
    scale(scale)
}

fun MouseEvent.position(): Position {
    return Position(this.x.toFloat(), this.y.toFloat())
}

fun MouseEvent.realPosition(): Position {
    return -Context.currentTranslation + position() / Context.currentScale
}

fun PApplet.ellipse(position: Position, width: Float, height: Float = width) {
    ellipse(position.x, position.y, width, height)
}
