package de.arindy.swingby.core

import de.arindy.swingby.core.data.Body
import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Result
import de.arindy.swingby.core.data.Velocity2D
import kotlin.math.pow
import kotlin.math.sqrt

private const val G = 6.67384E-20

fun swingby(
    planet: Body,
    probe: Body,
    deltaTime: (distance: Double, probeVelocity: Double) -> Double
): Map<Double, Result> {
    val result = ArrayList<Pair<Double, Result>>()
    result.add(
        Pair(
            0.0, Result(
                planet,
                probe,
                distance(planet.position, probe.position)
            )
        )
    )
    val maxDistance = result.last().second.distance
    while (
        (planet.diameter + probe.diameter) / 2 <= result.last().second.distance &&
        result.last().second.distance <= maxDistance
    ) {
        val timeStep = deltaTime(result.last().second.distance, result.last().second.probe.velocity)
        val (nextPlanet, nextProbe) = calculateSwingByStep(
            result.last().second.planet,
            result.last().second.probe,
            timeStep
        )
        result.add(
            Pair(
                result.last().first + timeStep, Result(
                    nextPlanet,
                    nextProbe,
                    distance(nextPlanet.position, nextProbe.position)
                )
            )
        )
    }
    return result.associate { it.first to it.second }
}

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
    val nextPlanet = nextBody(
        body,
        other,
        timeStep
    )
    val nextProbe = nextBody(
        other,
        body,
        timeStep
    )
    return Pair(nextPlanet, nextProbe)
}

private fun nextBody(body: Body, other: Body, timeStep: Double): Body {
    val nextPlanetVelocity =
        nextVelocity(
            body,
            other,
            timeStep
        )
    val nextPlanetPosition = nextPosition(body, nextPlanetVelocity, timeStep)
    return Body(
        name = body.name,
        position = nextPlanetPosition,
        velocity2D = nextPlanetVelocity,
        mass = body.mass,
        diameter = body.diameter
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

private fun distance(first: Coordinates, second: Coordinates): Double {
    return sqrt(
        (first.x - second.x).pow(2) +
            (first.y - second.y).pow(2)
    )
}

fun main() {
    val message = swingby(
        planet = Body(
            name = "Jupiter",
            mass = 1.899E27,
            diameter = 139822.0,
            position = Coordinates(
                x = 3.18385E8,
                y = 6.82157E8,
            ),
            velocity2D = Velocity2D(
                y = -12.0191,
                x = 6.14755
            )
        ),
        probe = Body(
            name = "Cassini",
            mass = 4641.0,
            diameter = 0.0,
            position = Coordinates(
                x = 2.88856E8,
                y = 6.58971E8,
            ),
            velocity2D = Velocity2D(
                y = -3.06151,
                x = 17.373
            )
        )
    ) { distance, probeVelocity ->
        squaredTimeStep(distance, probeVelocity, 1.0)
    }
    println("minDistance: " + message.map { it.value.distance }.min())
    val maxVelocity = message.map { it.value.probe.velocity }.max()
    println("MaxVelocity: $maxVelocity")
    println("VelocityDiff: " + (message.values.last().probe.velocity - message.values.first().probe.velocity))
    println(message)
}
