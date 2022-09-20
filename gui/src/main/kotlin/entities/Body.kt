package de.arindy.swingby.gui.entities

import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.core.data.Velocity2D
import de.arindy.swingby.gui.core.Context.applet
import de.arindy.swingby.gui.core.Context.currentScale
import de.arindy.swingby.gui.core.Context.currentTranslation
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.insideScreen
import de.arindy.swingby.gui.core.Context.isRegistered
import de.arindy.swingby.gui.core.Context.register
import de.arindy.swingby.gui.core.Context.unregister
import de.arindy.swingby.gui.core.color
import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.ellipse
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.line
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Direction
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.core.PConstants.LEFT
import kotlin.math.max
import kotlin.math.min
import de.arindy.swingby.core.data.Body as BodyData

class Body(
    override var position: () -> Position,
    var diameter: Float,
    private var drawDiameter: Float = diameter,
    override var size: Size = Size(drawDiameter, drawDiameter),
    override var name: () -> String,
    var color: Color,
    private var trail: Boolean = false,
    mass: Double,
    velocity2D: Velocity2D
) : Component {

    private var action: (Body) -> Unit = {}
    private val positions: ArrayList<Position> = ArrayList()
    private var stepCounter: Int = 1
    var lastPosition: Position = position()
    private var data: BodyData
    private var trailStep: Int
    private val info: BodyInfo
    private val label: Label
    var following: Boolean = false
    private var vVec: Boolean = false

    init {
        positions.add(position())
        data = BodyData(
            position = position().asCoordinates(),
            velocity2D = velocity2D,
            mass = mass,
            diameter = diameter.toDouble()
        )
        trailStep = 200
        label = register(
            Label(
                position = { insideScreen(labelPosition(), Size(applet.textWidth(name()) + 12F, 20F)) },
                size = Size(40F, 20F),
                name = name,
                textSize = 12F,
                horizontalAlign = LEFT,
                color = { color },
            ), gui = true
        ) as Label
        val bodInfoSize = Size(width = 280F, 220F)
        info = BodyInfo(
            size = bodInfoSize,
            position = { insideScreen(labelPosition() + Position(15F, 10F), bodInfoSize) },
            body = data,
            name = name,
            color = color
        ).changePosition {
            this.positions.clear()
            this.positions.add(it)
            this.lastPosition = it
        }.changeData {
            this.data = it
        }.changeColor {
            this.color = it
            label.color = { it }
        }.changeName {
            this.name = { it }
            label.name = { it }
        }.toggleVVec {
            vVec = !vVec
        }
    }

    private fun labelPosition(): Position {
        return (lastPosition + Position(diameter / 2 + 10F / currentScale, -(diameter / 2 + 30F / currentScale))
            + currentTranslation) * currentScale
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
        this.diameter = data.diameter.toFloat()
        this.drawDiameter = this.diameter
        this.size = Size(this.diameter, this.diameter)
        drawDiameter = if (drawDiameter * currentScale > 0.1F) drawDiameter else 0.11F / currentScale
        with(applet) {
            drawTrail()
            drawBody()
            drawLabelPointer()
            if (vVec) {
                drawVelocityArrow()
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
            if (trail && positions.size > 1) {
                val trailShape = createShape()
                trailShape.beginShape()
                for (index in 0 until positions.size) {
                    trailShape.vertex(positions[index].x, positions[index].y)
                }
                trailShape.vertex(lastPosition.x, lastPosition.y)
                trailShape.noFill()
                trailShape.endShape()
                trailShape.setStroke(color(color.background))
                trailShape.setStrokeWeight(1 / currentScale)
                shape(trailShape)
            }
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

    private fun PApplet.drawVelocityArrow() {
        inMatrix {
            strokeWeight(1 / currentScale)
            stroke("0xFFFFFF")
            val velocity = Position(
                data().velocity2D.x.toFloat() / currentScale,
                data().velocity2D.y.toFloat() / currentScale
            )
            line(lastPosition, lastPosition + velocity)
            strokeWeight(1F)
        }
    }

    fun onClick(action: (Body) -> Unit): Body {
        this.action = action
        return this
    }

    fun follow(): Body {
        this.following = true
        return this
    }

    fun unfollow(): Body? {
        this.following = false
        return null
    }

    fun toggleInfo() {
        if (infoVisible()) {
            register(label, gui = true)
            info.hide()
        } else {
            unregister(label, gui = true)
            info.show()
        }
    }

    fun infoVisible(): Boolean {
        return isRegistered(info, gui = true)
    }

    companion object {
        const val MAX_POSITIONS: Int = 16000
    }

}

fun Coordinates.asPosition(): Position {
    return Position(this.x.toFloat(), this.y.toFloat())
}

fun Position.asCoordinates(): Coordinates {
    return Coordinates(this.x.toDouble(), this.y.toDouble())
}
