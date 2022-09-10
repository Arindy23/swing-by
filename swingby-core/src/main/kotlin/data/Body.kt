package de.arindy.swingby.core.data

import kotlin.math.pow
import kotlin.math.sqrt

class Body(
    position: Coordinates,
    velocity2D: Velocity2D,
    mass: Double,
    diameter: Double,
    distanceToNextBody: Double = 0.0
) {
    val position: Coordinates
    val velocity2D: Velocity2D
    val velocity: Double
    val mass: Double
    val diameter: Double
    val distanceToNextBody: Double

    init {
        this.position = position
        this.velocity2D = velocity2D
        this.velocity = velocity(velocity2D)
        this.mass = mass
        this.diameter = diameter
        this.distanceToNextBody = distanceToNextBody
    }

    private fun velocity(velocity2D: Velocity2D): Double {
        return sqrt(velocity2D.x.pow(2) + velocity2D.y.pow(2))
    }

}
