package de.arindy.swingby.core.data

data class Coordinates(
    val x: Double,
    val y: Double,
) {
    override fun toString(): String {
        return "[x=$x, y=$y]"
    }

    operator fun minus(other: Coordinates): Coordinates {
        return Coordinates(this.x - other.x, this.y - other.y)
    }

    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(this.x + other.x, this.y + other.y)
    }
}
