package de.arindy.swingby.gui.core.units

data class Position(val x: Float, val y: Float) {
    companion object {
        val ZERO: Position = Position(0F, 0F)
    }

    override fun toString(): String {
        return "[x: ${this.x}, y: ${this.y}]"
    }

    operator fun plus(other: Position): Position {
        return Position(this.x + other.x, this.y + other.y)
    }

    operator fun unaryMinus(): Position {
        return Position(-this.x, -this.y)
    }

    operator fun minus(other: Position): Position {
        return this + -other
    }

    operator fun div(skalar: Float): Position {
        return Position((this.x.toDouble() / skalar).toFloat(), (this.y.toDouble() / skalar).toFloat())
    }

    operator fun times(skalar: Float): Position {
        return Position((this.x.toDouble() * skalar).toFloat(), (this.y.toDouble() * skalar).toFloat())
    }
}
