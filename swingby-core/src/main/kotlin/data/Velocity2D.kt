package de.arindy.swingby.core.data

data class Velocity2D(
    val x: Double,
    val y: Double,
) {
    override fun toString(): String {
        return "[x=$x, y=$y]"
    }
}
