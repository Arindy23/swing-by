package de.arindy.swingby.core

import de.arindy.swingby.core.data.Body
import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Velocity2D
import kotlin.math.pow
import kotlin.math.sqrt

private const val G = 6.67384E-20

fun calculateSwingByStep(
    bodies: Map<String, Body>,
    timeStep: Double
): Map<String, Body> {
    return bodies.keys.associateWith {
        val others = HashMap(bodies)
        val bodyToCalculate = others.remove(it)!!
        nextBody(bodyToCalculate, others.values, timeStep)
    }
}

fun distance(first: Coordinates, second: Coordinates): Double {
    return sqrt(
        (first.x - second.x).pow(2) +
            (first.y - second.y).pow(2)
    )
}

private fun nextBody(body: Body, others: MutableCollection<Body>, timeStep: Double): Body {
    val nextVelocity =
        nextVelocity(
            body,
            others,
            timeStep = timeStep
        )
    val nextPosition = nextPosition(body, nextVelocity, timeStep)
    return Body(
        position = nextPosition,
        velocity2D = nextVelocity,
        mass = body.mass,
        diameter = body.diameter,
        distanceToNextBody = others.minOf { distance(body.position, it.position) }
    )
}

private fun nextVelocity(body: Body, others: MutableCollection<Body>, timeStep: Double): Velocity2D {
    var crash: Body? = null
    val (x, y) = others.map {
        val distance = distance(body.position, it.position)
        if (distance <= (body.diameter + it.diameter) / 2) {
            crash = it
        }
        Pair(
            it.mass / distance.pow(3) * (body.position.x - it.position.x),
            it.mass / distance.pow(3) * (body.position.y - it.position.y)
        )
    }.reduce { acc, pair ->
        Pair(acc.first + pair.first, acc.second + pair.second)
    }
    return if (crash != null) {
        Velocity2D(
            (body.mass * body.velocity2D.x + crash!!.mass * crash!!.velocity2D.x) / (body.mass + crash!!.mass),
            (body.mass * body.velocity2D.y + crash!!.mass * crash!!.velocity2D.y) / (body.mass + crash!!.mass),
        )
    } else {
        Velocity2D(
            x = body.velocity2D.x - G * x * timeStep,
            y = body.velocity2D.y - G * y * timeStep,
        )
    }
}

private fun nextPosition(body: Body, nextVelocity: Velocity2D, timeStep: Double): Coordinates {
    return Coordinates(
        x = body.position.x + timeStep * ((body.velocity2D.x + nextVelocity.x) / 2),
        y = body.position.y + timeStep * ((body.velocity2D.y + nextVelocity.y) / 2),
    )
}
