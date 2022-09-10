package de.arindy.swingby.gui.entities

import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Velocity2D
import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.currentScale
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.ellipse
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.line
import de.arindy.swingby.gui.core.realPosition
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Direction
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.event.MouseEvent
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import de.arindy.swingby.core.data.Body as BodyData

class Body(
    override var position: Position,
    val diameter: Float,
    private val drawDiameter: Float = if (diameter < 1000F) 100F else diameter,
    override var size: Size = Size(drawDiameter, drawDiameter),
    override var name: String,
    private val color: Color,
    private var trail: Boolean = false,
    mass: Double,
    velocity2D: Velocity2D
) : Component {

    private var action: Optional<(Body) -> Unit> = Optional.empty()
    private val positions: ArrayList<Position> = ArrayList()
    private var stepCounter: Int = 1
    var lastPosition: Position = position
    private var data: BodyData
    private var trailStep: Int

    init {
        positions.add(position)
        data = BodyData(
            position = position.asCoordinates(),
            velocity2D = velocity2D,
            mass = mass,
            diameter = diameter.toDouble()
        )
        trailStep = 200
    }

    fun data(): BodyData {
        return data
    }

    fun update(data: BodyData) {
        this.data = data
        trailStep = max(10, min((data.distanceToNextBody / 20000).toInt(), 200))
        moveTo(data.position.asPosition())
    }

    fun enableTrail(trail: Boolean = true): Body {
        this.trail = trail
        return this
    }

    override fun draw() {
        with(Context.applet) {
            drawTrail()
            drawBody()
        }
    }

    override fun mousePressed(event: MouseEvent) {
        val mouseToCenter = lastPosition - event.realPosition()
        if (sqrt(mouseToCenter.x.pow(2) + mouseToCenter.y.pow(2)) <= drawDiameter / 2) {
            if (this.action.isPresent) {
                this.action.get()(this)
            }
        }
    }

    private fun moveTo(position: Position) {
        if (positions.size > 2
            && Direction(
                positions[positions.size - 1],
                positions.last(),
            ) == Direction(
                positions.last(),
                position
            )
        ) {
            positions.removeLast()
        }
        if (positions.size == MAX_POSITIONS) {
            positions.removeFirst()
        }
        if (stepCounter % trailStep == 0) {
            positions.add(position)
            stepCounter = 1
        }
        stepCounter++
        this.lastPosition = position
    }

    private fun PApplet.drawBody() {
        inMatrix {
            fill(color.background)
            ellipse(lastPosition, drawDiameter)
        }
    }

    private fun PApplet.drawTrail() {
        inMatrix {
            strokeWeight(1 / currentScale)
            if (trail && positions.size > 1) {
                for (index in 1 until positions.size) {
                    stroke(color.background)
                    line(positions[index - 1], positions[index])
                }

                stroke(color.background)
                line(positions.last(), lastPosition)
            }
            if (drawDiameter * currentScale > 20F) {
                if (trail && positions.size > 1) {
                    for (index in 0 until positions.size) {
                        stroke(color.background)
                        ellipse(positions[index], 4F / currentScale)
                    }
                }
            }
            strokeWeight(1F)
        }
    }

    fun onClick(action: (Body) -> Unit): Body {
        this.action = Optional.of(action)
        return this
    }

    companion object {
        const val MAX_POSITIONS: Int = 2000
    }

}

private fun Coordinates.asPosition(): Position {
    return Position(this.x.toFloat(), this.y.toFloat())
}

private fun Position.asCoordinates(): Coordinates {
    return Coordinates(this.x.toDouble(), this.y.toDouble())
}
