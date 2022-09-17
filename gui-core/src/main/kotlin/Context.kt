package de.arindy.swingby.gui.core

import de.arindy.swingby.gui.core.components.Component
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import processing.core.PApplet

object Context {

    var currentScale: Float = 1F
    var currentTranslation: Position = Position.ZERO
    var deltaTime: Float = 0F
    private var lastTime: Long = 0
    private var elapsed: Float = 0F

    lateinit var applet: PApplet
    private val guiComponents = HashSet<Component>()
    private val components = HashSet<Component>()

    fun register(applet: PApplet) {
        Context.applet = applet
    }

    fun register(component: Component, gui: Boolean = false): Component {
        if (gui) {
            guiComponents.add(component)
        } else {
            components.add(component)
        }
        return component
    }

    fun unregister(component: Component, gui: Boolean = false) {
        if (gui) {
            guiComponents.remove(component)
        } else {
            components.remove(component)
        }
    }

    fun isRegistered(component: Component, gui: Boolean = false): Boolean {
        return if (gui) {
            guiComponents.contains(component)
        } else {
            components.contains(component)
        }
    }

    fun componentsHaveFocus(): Boolean {
        return components().any { it.isFocused() } || guiComponents().any { it.isFocused() }
    }

    fun guiComponents(): ImmutableList<Component> {
        return guiComponents.toImmutableList()
    }

    fun components(): ImmutableList<Component> {
        return components.toImmutableList()
    }

    fun updateDeltaTime(currentTimeMillis: Long) {
        deltaTime = (currentTimeMillis - lastTime).toFloat() / 1000
        lastTime = currentTimeMillis
        elapsed += deltaTime
        if (elapsed >= 1) {
            elapsed = 0F
        }
    }

    fun insideGuiComponent(): Boolean {
        return guiComponents().any { component -> component.inside(mousePosition()) }
    }

    fun secondElapsed(delay: Float = 0F): Boolean {
        return elapsed <= delay
    }

    fun resolution(): Size {
        return Size(applet.width.toFloat(), applet.height.toFloat())
    }

    fun mousePosition(): Position {
        return Position(applet.mouseX.toFloat(), applet.mouseY.toFloat())
    }

    fun realMousePosition(): Position {
        return currentTranslation + mousePosition() / currentScale
    }

    fun coordinatesInCenter(): Position {
        return -currentTranslation + Position(
            (resolution().width / currentScale) / 2,
            (resolution().height) / currentScale / 2
        )
    }

    fun PApplet.inMatrix(block: () -> Unit) {
        pushMatrix()
        block()
        popMatrix()
    }
}
