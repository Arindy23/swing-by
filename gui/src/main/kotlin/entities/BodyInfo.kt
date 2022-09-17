package de.arindy.swingby.gui.entities

import de.arindy.swingby.core.data.Body
import de.arindy.swingby.core.data.Coordinates
import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.components.TextField
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.notAValidHexCode
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.LEFT
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.round

class BodyInfo(
    override val position: Position,
    override val size: Size = Size(width = 280F, 180F),
    var body: Body,
    private var color: Color,
    override val name: () -> String,
) : Component {

    private var followFunction: () -> Unit = {}
    private var changePositionFunction: (Position) -> Unit = {}
    private var updateDataFunction: (Body) -> Unit = {}
    private var changeColorFunction: (Color) -> Unit = {}
    private var changeNameFunction: (String) -> Unit = {}
    private val decimalFormat: DecimalFormat = DecimalFormat("#.0#")

    init {
        with(Context) {
            register(
                TextField(
                    position = Position(position.x + 5F, position.y + 5F),
                    size = Size(width = (size.width - 15F) / 2, 25F),
                    name = name,
                    value = name
                ).register { _, newValue -> changeNameFunction(newValue) }, gui = true
            )
            register(
                Button(
                    position = Position(position.x + 5F + (size.width - 10F) / 2, position.y + 5F),
                    size = Size(width = (size.width - 10F) / 2, 25F),
                    name = { "follow" }
                ).registerAction {
                    run { followFunction() }
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 35F),
                    size = Size(width = size.width / 2 - 25F, 18F),
                    name = { "Color:" },
                    textSize = 12F,
                    horizontalAlign = LEFT
                ), gui = true
            )
            register(
                TextField(
                    position = Position(position.x + 120F, position.y + 35F),
                    size = Size(width = size.width / 2 + 15F, 18F),
                    name = { color.foreground },
                    value = { color.foreground },
                    textSize = 12F
                ).register { _, newValue ->
                    if (!notAValidHexCode(newValue)) {
                        color =
                            Color(
                                background = newValue,
                                foreground = newValue,
                                selected = color.selected
                            )
                        changeColorFunction(color)
                    }
                    updateDataFunction(body)
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 55F),
                    size = Size(width = size.width / 2 - 25F, 18F),
                    name = { "Mass in kg:" },
                    textSize = 12F,
                    horizontalAlign = LEFT
                ), gui = true
            )
            register(
                TextField(
                    position = Position(position.x + 120F, position.y + 55F),
                    size = Size(width = size.width / 2 + 15F, 18F),
                    name = { "${body.mass}" },
                    value = { body.mass.toString() },
                    textSize = 12F
                ).register { _, newValue ->
                    body = Body(
                        position = body.position,
                        velocity2D = body.velocity2D,
                        mass = newValue.toDoubleOrNull() ?: 0.0,
                        diameter = body.diameter,
                        distanceToNextBody = body.distanceToNextBody
                    )
                    updateDataFunction(body)
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 75F),
                    size = Size(width = size.width / 2 - 25F, 18F),
                    name = { "Diameter in km:" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                TextField(
                    position = Position(position.x + 120F, position.y + 75F),
                    size = Size(width = size.width / 2 + 15F, 18F),
                    name = { "${body.diameter}" },
                    value = { body.diameter.toString() },
                    textSize = 12F
                ).register { _, newValue ->
                    body = Body(
                        position = body.position,
                        velocity2D = body.velocity2D,
                        mass = body.mass,
                        diameter = newValue.toDoubleOrNull() ?: 0.0,
                        distanceToNextBody = body.distanceToNextBody
                    )
                    updateDataFunction(body)
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 95F),
                    size = Size(width = size.width / 2 - 25F, 18F),
                    name = { "X-coordinate:" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                TextField(
                    position = Position(position.x + 120F, position.y + 95F),
                    size = Size(width = size.width / 2 + 15F, 18F),
                    name = { "${body.position.x}" },
                    value = { body.position.x.toString() },
                    textSize = 12F,
                ).register { _, newValue ->
                    body = Body(
                        position = Coordinates(newValue.toDoubleOrNull() ?: 0.0, body.position.y),
                        velocity2D = body.velocity2D,
                        mass = body.mass,
                        diameter = body.diameter,
                        distanceToNextBody = body.distanceToNextBody
                    )
                    changePositionFunction(body.position.asPosition())
                    updateDataFunction(body)
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 115F),
                    size = Size(width = size.width / 2 - 15F, 18F),
                    name = { "Y-coordinate:" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                TextField(
                    position = Position(position.x + 120F, position.y + 115F),
                    size = Size(width = size.width / 2 + 15F, 18F),
                    name = { "${body.position.y}" },
                    value = { body.position.y.toString() },
                    textSize = 12F,
                ).register { _, newValue ->
                    body = Body(
                        position = Coordinates(body.position.x, newValue.toDoubleOrNull() ?: 0.0),
                        velocity2D = body.velocity2D,
                        mass = body.mass,
                        diameter = body.diameter,
                        distanceToNextBody = body.distanceToNextBody
                    )
                    changePositionFunction(body.position.asPosition())
                    updateDataFunction(body)
                }, gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 135F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Distance to next Body: ${round(body.distanceToNextBody * 1E4 / 1E4)}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 155F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Speed in km/s: " + decimalFormat.format(body.velocity) },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
        }
    }

    override fun draw() {
        with(Context.applet) {
            stroke(color.foreground)
            fill(color.background.replaceRange(IntRange(7, 8), "11"))
            rect(position, size)
        }
    }

    fun Double.format(digits: Int): String {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = digits
        return numberFormat.format(this)
    }

    fun follow(follow: () -> Unit): BodyInfo {
        this.followFunction = follow
        return this
    }

    fun changePosition(function: (Position) -> Unit): BodyInfo {
        this.changePositionFunction = function
        return this
    }

    fun changeData(function: (Body) -> Unit): BodyInfo {
        this.updateDataFunction = function
        return this
    }

    fun changeColor(function: (Color) -> Unit): BodyInfo {
        this.changeColorFunction = function
        return this
    }

    fun changeName(function: (String) -> Unit): BodyInfo {
        this.changeNameFunction = function
        return this
    }
}
