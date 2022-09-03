package de.arindy.swingby.gui

class Position(val x: Float, val y: Float) {
    companion object {
        val ZERO: Position = Position(0F, 0F)
    }

    override fun toString(): String {
        return "[${this.x} : ${this.y}]"
    }
}
