package de.arindy.swingby.gui.core.units

data class Position(val x: Float, val y: Float) {
    companion object {
        val ZERO: Position = Position(0F, 0F)
    }

    override fun toString(): String {
        return "[x: ${this.x}, y: ${this.y}]"
    }
}
