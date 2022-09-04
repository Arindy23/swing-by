package de.arindy.swingby.gui.core.units

class Dimensions(
    val x0: Float,
    val x1: Float,
    val y0: Float,
    val y1: Float,
) {

    constructor(position: Position, size: Size) : this(
        position.x,
        position.x + size.width,
        position.y,
        position.y + size.height
    )

    override fun toString(): String {
        return "[x0: $x0, x1: $x1, y0: $y0, y1: $y1]"
    }
}
