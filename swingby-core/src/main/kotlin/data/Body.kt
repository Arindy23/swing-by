package de.arindy.swingby.core.data

import kotlin.math.pow
import kotlin.math.sqrt

class Body(
    name: String,
    position: Coordinates,
    velocity2D: Velocity2D,
    mass: Double,
    diameter: Double
) {
    val name: String
    val position: Coordinates
    val velocity2D: Velocity2D
    val velocity: Double
    val mass: Double
    val diameter: Double

    init {
        this.name = name
        this.position = position
        this.velocity2D = velocity2D
        this.velocity = velocity(velocity2D)
        this.mass = mass
        this.diameter = diameter
    }

    fun velocity(velocity2D: Velocity2D): Double {
        return sqrt(velocity2D.x.pow(2) + velocity2D.y.pow(2))
    }

    override fun toString(): String {
        return "$name(\n" +
            "    position=$position,\n" +
            "    velocity2D=$velocity2D,\n" +
            "    velocity=$velocity,\n  )"
    }

}
