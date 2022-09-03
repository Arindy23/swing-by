package de.arindy.swingby.gui

import de.arindy.swingby.gui.components.Component
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import processing.core.PApplet

object CONTEXT {

    var deltaTime: Float = 0F
    private var lastTime: Long = 0

    lateinit var applet: PApplet
    private val components = ArrayList<Component>()

    fun register(applet: PApplet) {
        this.applet = applet
    }

    fun register(component: Component) {
        this.components.add(component)
    }

    fun components(): ImmutableList<Component> {
        return components.toImmutableList()
    }

    fun updateDeltaTime(currentTimeMillis: Long) {
        this.deltaTime = (currentTimeMillis - lastTime).toFloat() / 1000
        this.lastTime = currentTimeMillis
    }

}

fun resolution(): Size {
    return Size(CONTEXT.applet.width.toFloat(), CONTEXT.applet.height.toFloat())
}

fun mousePosition(): Position {
    return Position(CONTEXT.applet.mouseX.toFloat(), CONTEXT.applet.mouseY.toFloat())
}
