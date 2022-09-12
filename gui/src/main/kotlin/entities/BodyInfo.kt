package de.arindy.swingby.gui.entities

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.LEFT
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.round
import de.arindy.swingby.core.data.Body as BodyData

class BodyInfo(
    override val position: Position,
    override val size: Size = Size(width = 280F, 160F),
    var body: BodyData,
    private val color: () -> Color,
    override val name: () -> String
) : Component {

    private var followFunction: () -> Unit = {}
    private val decimalFormat: DecimalFormat = DecimalFormat("#.0#")

    init {
        with(Context) {
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 5F),
                    size = Size(width = (size.width - 10F) / 2, 25F),
                    name = { name() }
                ), gui = true
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
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Mass in kg: ${body.mass}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 55F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Diameter in km: ${body.diameter}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 75F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "X-coordinate: ${body.position.x}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 95F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Y-coordinate: ${body.position.y}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 115F),
                    size = Size(width = size.width - 10F, 18F),
                    name = { "Distance to next Body: ${round(body.distanceToNextBody * 1E4 / 1E4)}" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ), gui = true
            )
            register(
                Label(
                    position = Position(position.x + 5F, position.y + 135F),
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
            stroke(color().foreground)
            fill("0xFFFFFF00")
            rect(position, size)
        }
    }

    fun Double.format(digits: Int): String {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = digits
        return numberFormat.format(this)
    }

    fun follow(follow: () -> Unit): Component {
        this.followFunction = follow
        return this
    }
}
