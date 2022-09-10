package de.arindy.swingby.core

import de.arindy.swingby.core.data.Body
import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Velocity2D
import kotlin.math.pow
import kotlin.math.sqrt

private const val G = 6.67384E-20

fun squaredTimeStep(distance: Double, probeVelocity: Double, incrementFactor: Double): Double {
    return distance / (sqrt(probeVelocity) * (incrementFactor * 10))
}

fun linearTimeStep(distance: Double, probeVelocity: Double, incrementFactor: Double): Double {
    return distance / (probeVelocity * (incrementFactor * 10))
}

fun calculateSwingByStep(
    body: Body,
    other: Body,
    timeStep: Double
): Pair<Body, Body> {
    val nextBody = nextBody(
        body,
        other,
        timeStep
    )
    val nextOther =
        if (other.distanceToNextBody > 0 && (other.distanceToNextBody) <= (body.diameter + other.diameter) / 2) {
            Body(
                position = Coordinates(
                    x = other.position.x + timeStep * nextBody.velocity2D.x,
                    y = other.position.y + timeStep * nextBody.velocity2D.y,
                ),
                velocity2D = nextBody.velocity2D,
                mass = other.mass,
                diameter = other.diameter,
                distanceToNextBody = other.distanceToNextBody
            )
        } else {
            nextBody(
                other,
                body,
                timeStep
            )
        }
    return Pair(nextBody, nextOther)
}

fun distance(first: Coordinates, second: Coordinates): Double {
    return sqrt(
        (first.x - second.x).pow(2) +
            (first.y - second.y).pow(2)
    )
}

private fun nextBody(body: Body, other: Body, timeStep: Double): Body {
    val nextVelocity =
        nextVelocity(
            body,
            other,
            timeStep
        )
    val nextPosition = nextPosition(body, nextVelocity, timeStep)
    return Body(
        position = nextPosition,
        velocity2D = nextVelocity,
        mass = body.mass,
        diameter = body.diameter,
        distanceToNextBody = distance(body.position, other.position)
    )
}

private fun nextVelocity(body: Body, other: Body, timeStep: Double): Velocity2D {
    val distance = distance(body.position, other.position)
    return Velocity2D(
        x = body.velocity2D.x - G * other.mass / distance.pow(3) * (body.position.x - other.position.x) * timeStep,
        y = body.velocity2D.y - G * other.mass / distance.pow(3) * (body.position.y - other.position.y) * timeStep,
    )
}

private fun nextPosition(body: Body, nextVelocity: Velocity2D, timeStep: Double): Coordinates {
    return Coordinates(
        x = body.position.x + timeStep * ((body.velocity2D.x + nextVelocity.x) / 2),
        y = body.position.y + timeStep * ((body.velocity2D.y + nextVelocity.y) / 2),
    )
}
