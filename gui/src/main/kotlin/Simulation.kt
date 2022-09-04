package de.arindy.swingby.gui

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.components
import de.arindy.swingby.gui.core.Context.guiComponents
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.updateDeltaTime
import de.arindy.swingby.gui.core.components.FPSCounter
import de.arindy.swingby.gui.core.components.draw
import de.arindy.swingby.gui.core.components.keyPressed
import de.arindy.swingby.gui.core.components.mouseDragged
import de.arindy.swingby.gui.core.components.mousePressed
import de.arindy.swingby.gui.core.components.mouseReleased
import processing.core.PApplet
import processing.event.KeyEvent
import processing.event.MouseEvent

class Simulation : PApplet() {

    private val fpsCounter = FPSCounter()

    init {
        Context.register(this)
    }

    override fun settings() {
        size(1024, 768)
    }

    override fun setup() {
        setupSurface()
        frameRate(60f)
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
        inMatrix {
            components().draw()
        }
        guiComponents().draw()
        updateDeltaTime(System.currentTimeMillis())
    }

    override fun keyPressed(event: KeyEvent) {
        if (event.keyCode == java.awt.event.KeyEvent.VK_F11) {
            if (Context.isRegistered(fpsCounter)) {
                Context.unregister(fpsCounter, gui = true)
            } else {
                Context.register(fpsCounter, gui = true)
            }
        }
        guiComponents().keyPressed(event)
    }

    override fun mouseReleased(event: MouseEvent) {
        guiComponents().mouseReleased(event)
    }

    override fun mousePressed(event: MouseEvent) {
        guiComponents().mousePressed(event)
    }

    override fun mouseDragged(event: MouseEvent) {
        guiComponents().mouseDragged(event)
    }

    override fun exit() {
        if (key != ESC) {
            super.exit()
        }
    }

    companion object {
        const val title = "Swing-By"
        const val icon = "icon.png"
    }

}
