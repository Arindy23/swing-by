package de.arindy.swingby.core.data

data class Velocity2D(
    val x: Double,
    val y: Double,
) {
    override fun toString(): String {
        return "[x=$x, y=$y]"
    }

    operator fun times(other: Velocity2D): Double {
        return this.x * other.y + this.y * other.y
    }
}
