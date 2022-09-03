package de.arindy.swingby.gui

import processing.core.PApplet

fun PApplet.fill(color: String) {
    if (!color.startsWith("0x") || color.length > 10 || color.length < 3 || color.substring(2).any { c -> c.uppercaseChar() > 'F'  }) {
        throw IllegalArgumentException("\"$color\" is not a valid color!")
    }
    var hexCode = color.substring(2)
    for (range in color.length..9) {
        hexCode += "F"
    }
    this.fill(
        hexCode.substring(0, 2).toInt(16).toFloat(),
        hexCode.substring(2, 4).toInt(16).toFloat(),
        hexCode.substring(4, 6).toInt(16).toFloat(),
        hexCode.substring(6).toInt(16).toFloat()
    )
}

fun PApplet.line(x1: Number, y1: Number, x2: Number, y2: Number) {
    this.line(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
}

fun PApplet.rect(size: Size, position: Position = Position.ZERO) {
    this.rect(position.x, position.y, size.width, size.height)
}

fun PApplet.translate(position: Position) {
    this.translate(position.x, position.y)
}
