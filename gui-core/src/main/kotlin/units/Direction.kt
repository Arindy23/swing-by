package de.arindy.swingby.gui.core.units

import kotlin.math.pow
import kotlin.math.sqrt

class Direction(start: Position, end: Position, precisionDecimalPoints: Float = 0.1F) {

    val x: Float
    val y: Float

    init {
        val precision = 10F.pow(precisionDecimalPoints)
        val newX = (end.x - start.x)
        val newY = (end.y - start.y)
        val m = sqrt(newX.pow(2) + newY.pow(2))
        x = (((if (m == 0F || m == 1F) newX else newX / m) * precision) / precision)
        y = (((if (m == 0F || m == 1F) newY else newY / m) * precision) / precision)

    }

    override fun equals(other: Any?): Boolean {
        return (other is Direction)
            && this.x == other.x
            && this.y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "Direction(x=$x, y=$y)"
    }


}
