package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants.CENTER
import processing.event.MouseEvent
import java.util.*
import kotlin.collections.HashMap

class Button(
    override var position: Position,
    override var size: Size = Size(100F, 25F),
    override var scale: Float = 1F,
    override var name: String = "Test",
    private val color: Color = Colors.primaryInverted,
    private val colorPressed: Color = Colors.primary,
) : Component {

    private val valueReceivers: HashMap<String, () -> Unit> = HashMap()
    private var pressed: Boolean = false

    fun registerAction(name: String = UUID.randomUUID().toString(), block: () -> Unit): Button {
        valueReceivers[name] = block
        return this
    }

    override fun draw() {
        val foreground = if (pressed) {
            colorPressed.foreground
        } else {
            color.foreground
        }
        val background = if (pressed) {
            colorPressed.background
        } else {
            color.background
        }
        with(Context.applet) {
            inMatrix {
                scale(scale)
                stroke(foreground)
                fill(background)
                rect(size, position)
            }
            inMatrix {
                fill(foreground)
                textSize(16F)
                textAlign(CENTER, CENTER)
                text(name, position.x + size.width / 2, position.y - 4F + size.height / 2)
            }
        }
    }

    override fun mousePressed(event: MouseEvent) {
        pressed = inside(event.position())
    }

    override fun mouseReleased(event: MouseEvent) {
        if (inside(event.position())) {
            valueReceivers.forEach { it.value() }
        }
        pressed = false
    }

    override fun mouseDragged(event: MouseEvent) {
        if (pressed) {
            pressed = inside(event.position())
        }
    }
}
