package de.arindy.swingby.gui

import de.arindy.swingby.gui.CONTEXT.components
import de.arindy.swingby.gui.CONTEXT.updateDeltaTime
import processing.core.PApplet
import processing.event.KeyEvent
import processing.event.MouseEvent
import processing.javafx.PSurfaceFX.PApplicationFX.surface
import kotlin.system.measureTimeMillis

class Simulation : PApplet() {

    init {
        CONTEXT.register(this)
    }

    override fun settings() {
        size(1024, 768)
    }

    override fun setup() {
        setupSurface()
        frameRate(120f)
        background(0x000000)
        GUI().build()
    }

    private fun setupSurface() {
        surface.setIcon(loadImage(icon))
        surface.setTitle(title)
        surface.setResizable(true)
    }

    override fun draw() {
        background(0x000000)
        components().forEach { component -> component.draw() }
        updateDeltaTime(System.currentTimeMillis())
    }

    override fun keyPressed(event: KeyEvent?) {
        components().forEach { component -> component.onKeyPressed(event!!) }
    }

    override fun mouseReleased(event: MouseEvent) {
        components().forEach { component -> component.mouseReleased(event) }
    }

    companion object {
        const val title = "Swing-By"
        const val icon = "icon.png"
    }

}
