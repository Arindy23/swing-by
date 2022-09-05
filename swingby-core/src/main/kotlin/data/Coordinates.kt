package de.arindy.swingby.core.data

data class Coordinates(
    val x: Double,
    val y: Double,
) {
    override fun toString(): String {
        return "[x=$x, y=$y]"
    }
}
