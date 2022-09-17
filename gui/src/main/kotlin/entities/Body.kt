package de.arindy.swingby.gui.entities

import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Velocity2D
import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.currentScale
import de.arindy.swingby.gui.core.Context.currentTranslation
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.register
import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.components.Label
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
import processing.core.PConstants.LEFT
import processing.event.MouseEvent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import de.arindy.swingby.core.data.Body as BodyData

class Body(
    override var position: Position,
    infoPosition: Position,
    var diameter: Float,
    private var drawDiameter: Float = diameter,
    override var size: Size = Size(drawDiameter, drawDiameter),
    override var name: () -> String,
    private var color: Color,
    private var trail: Boolean = false,
    mass: Double,
    velocity2D: Velocity2D
) : Component {

    private var action: (Body) -> Unit = {}
    private val positions: ArrayList<Position> = ArrayList()
    private var stepCounter: Int = 1
    var lastPosition: Position = position
    private var data: BodyData
    private var trailStep: Int
    private val info: BodyInfo
    private val label: Label
    private var labelPosition = Position.ZERO

    init {
        positions.add(position)
        data = BodyData(
            position = position.asCoordinates(),
            velocity2D = velocity2D,
            mass = mass,
            diameter = diameter.toDouble()
        )
        trailStep = 200
        label = register(
            Label(
                position = labelPosition,
                size = Size(40F, 20F),
                name = name,
                textSize = 12F,
                horizontalAlign = LEFT,
                color = color
            ), gui = true
        ) as Label
        info = register(
            BodyInfo(
                infoPosition,
                body = data,
                name = name,
                color = color
            ).follow {
                this.action(this)
            }.changePosition {
                this.positions.clear()
                this.positions.add(it)
                this.lastPosition = it
            }.changeData {
                this.data = it
            }.changeColor {
                this.color = it
                label.color = it
            },
            gui = true
        ) as BodyInfo
    }

    fun data(): BodyData {
        return data
    }

    fun update(data: BodyData) {
        this.data = data
        trailStep = max(10, min((data.distanceToNextBody / 20000).toInt(), 200))
        moveTo(data.position.asPosition())
        info.body = data
    }

    fun enableTrail(trail: Boolean = true): Body {
        this.trail = trail
        return this
    }

    override fun draw() {
        label.position = (
            lastPosition + Position(diameter / 2 + 10F / currentScale, -(diameter / 2 + 30F / currentScale))
                + currentTranslation
            ) * currentScale
        this.diameter = data.diameter.toFloat()
        this.drawDiameter = this.diameter
        this.size = Size(this.diameter, this.diameter)
        drawDiameter = if (drawDiameter * currentScale > 0.1F) drawDiameter else 0.11F / currentScale
        with(Context.applet) {
            drawTrail()
            drawBody()
            drawLabelPointer()
        }
    }

    override fun mousePressed(event: MouseEvent) {
        val mouseToCenter = lastPosition - event.realPosition()
        if (!Context.insideGuiComponent() && sqrt(mouseToCenter.x.pow(2) + mouseToCenter.y.pow(2)) <= drawDiameter / 2) {
            action(this)
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

    private fun PApplet.drawLabelPointer() {
        inMatrix {
            strokeWeight(1 / currentScale)
            stroke(color.background)
            line(
                lastPosition + Position(diameter / 2 + 5F / currentScale, -(diameter / 2 + 5F / currentScale)),
                lastPosition + Position(diameter / 2 + 15F / currentScale, -(diameter / 2 + 15F / currentScale))
            )
            strokeWeight(1F)
        }
    }

    fun onClick(action: (Body) -> Unit): Body {
        this.action = action
        return this
    }

    companion object {
        const val MAX_POSITIONS: Int = 2000
    }

}

fun Coordinates.asPosition(): Position {
    return Position(this.x.toFloat(), this.y.toFloat())
}

fun Position.asCoordinates(): Coordinates {
    return Coordinates(this.x.toDouble(), this.y.toDouble())
}
