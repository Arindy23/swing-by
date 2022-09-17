package de.arindy.swingby.gui

import de.arindy.swingby.core.calculateSwingByStep
import de.arindy.swingby.core.data.Velocity2D
import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.components
import de.arindy.swingby.gui.core.Context.currentScale
import de.arindy.swingby.gui.core.Context.currentTranslation
import de.arindy.swingby.gui.core.Context.deltaTime
import de.arindy.swingby.gui.core.Context.guiComponents
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.register
import de.arindy.swingby.gui.core.Context.resolution
import de.arindy.swingby.gui.core.Context.unregister
import de.arindy.swingby.gui.core.Context.updateDeltaTime
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.FPSCounter
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.components.Toggle
import de.arindy.swingby.gui.core.components.draw
import de.arindy.swingby.gui.core.components.keyPressed
import de.arindy.swingby.gui.core.components.mouseDragged
import de.arindy.swingby.gui.core.components.mousePressed
import de.arindy.swingby.gui.core.components.mouseReleased
import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.realPosition
import de.arindy.swingby.gui.core.scaleWithContext
import de.arindy.swingby.gui.core.translate
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import de.arindy.swingby.gui.core.units.middle
import de.arindy.swingby.gui.entities.Body
import processing.core.PApplet
import processing.core.PFont
import processing.event.KeyEvent
import processing.event.MouseEvent
import java.awt.Font
import java.awt.Font.BOLD
import java.awt.Font.MONOSPACED

class Simulation : PApplet() {

    init {
        register(this)
    }

    private val fpsCounter = FPSCounter()
    private var mouseClickedPosition: Position = Position.ZERO
    private var following: Body? = null
    private var animate = false
    private var centerOnScreen = false
    private lateinit var gui: GUI
    private val bodyLabels: ArrayList<Pair<Label, Button>> = ArrayList()

    private val jupiter = Body(
        name = { "Jupiter" },
        color = Colors.jupiter,
        diameter = 139822F,
        position = {
            Position(
                x = 3.1E8.toFloat(),
                y = 6.8E8.toFloat(),
            )
        },
        mass = 1.899E27,
        velocity2D = Velocity2D(
            y = -12.0,
            x = 6.1
        )
    )
    private val cassini = Body(
        name = { "Cassini" },
        color = Colors.cassini,
        diameter = 0F,
        position = {
            Position(
                x = 2.8E8.toFloat(),
                y = 6.5E8.toFloat(),
            )
        },
        mass = 4641.0,
        velocity2D = Velocity2D(
            y = -8.9,
            x = 9.5
        )
    )

    private var timeFactor = 100000.0
    private var elapsedTime = 0F
    private var elapsedTimeDays = 0F
    private var minDistance = -1F

    override fun settings() {
        size(1240, 768)
    }

    override fun setup() {
        setupSurface()
        frameRate(1440f)
        background(0x000000)
        textFont(PFont(Font(MONOSPACED, BOLD, 5), true))
        with(Context) {
            registerBody(cassini)
            registerBody(jupiter)
        }
        bodyLabels.forEach {
            register(it.first, gui = true)
            register(it.second, gui = true)
        }
        gui = GUI().build(
            animate = mapOf(Pair("animate") {
                animate = !animate
            }),
            timeFactor = mapOf(Pair("timeFactor") { _, newValue ->
                run { timeFactor = newValue.toDoubleOrNull() ?: timeFactor }
            }),
            centerOnScreen = mapOf(Pair("centerOnScreen") {
                centerOnScreen = !centerOnScreen
                if (centerOnScreen) {
                    centerOnScreen()
                }
            }),
        )
        centerOnScreen()
    }

    private fun Context.registerBody(body: Body) {
        val verticalPosition = bodyLabels.size * 25F
        bodyLabels.add(Pair(
            Label(
                position = { Position(10F, 200F + verticalPosition) },
                size = Size(width = 100F, 20F),
                name = { body.name() },
                color = { body.color },
                textSize = 16F
            ),
            Toggle(
                position = { Position(120F, 200F + verticalPosition) },
                size = Size(width = 100F, 20F),
                name = { if (body.infoVisible()) "hide info" else "show info" },
                toggle = { body.infoVisible() }
            ).registerAction {
                body.toggleInfo()
            }
        ))
        register(
            body.enableTrail().onClick(
                action = {
                    following = tryFollow(it)
                }
            ),
        )
    }

    private fun centerOnScreen() {
        following = following?.unfollow()
        currentScale = min(
            resolution().width / (abs(cassini.lastPosition.x - jupiter.lastPosition.x) * 2F),
            resolution().height / (abs(cassini.lastPosition.y - jupiter.lastPosition.y) * 2F),
        )
        currentTranslation = -middle(jupiter.lastPosition, cassini.lastPosition) + Position(
            (resolution().width / currentScale) / 2,
            (resolution().height) / currentScale / 2
        )
    }

    private fun tryFollow(it: Body): Body? {
        following?.unfollow()
        return if (following != it) {
            if (it.diameter * currentScale > resolution().height / 2) {
                currentScale = resolution().height / (it.diameter * 2F)
            }
            it.follow()
        } else {
            null
        }
    }

    private fun setupSurface() {
        surface.setIcon(loadImage(icon))
        surface.setTitle(title)
        surface.setResizable(true)
    }

    override fun draw() {
        background(0x000000)
        if (animate) {
            val (body, other) = calculateSwingByStep(
                jupiter.data(),
                cassini.data(),
                deltaTime * timeFactor
            )
            elapsedTime += deltaTime
            elapsedTimeDays += (deltaTime * timeFactor / 3600 / 24).toFloat()
            gui.updateElapsedTime(elapsedTime)
            gui.updateElapsedRealTime(elapsedTimeDays)
            jupiter.update(body)
            cassini.update(other)
            minDistance = if (
                minDistance < 0 ||
                minDistance > cassini.data().distanceToNextBody
            ) cassini.data().distanceToNextBody.toFloat()
            else minDistance
        }
        if (centerOnScreen) {
            centerOnScreen()
        } else {
            following?.let {
                currentTranslation = -it.lastPosition + Position(
                    (resolution().width / currentScale) / 2,
                    (resolution().height) / currentScale / 2
                )
            }
        }
        inMatrix {
            scaleWithContext(currentScale)
            translate(currentTranslation)
            components().draw()
        }
        guiComponents().draw()
        updateDeltaTime(System.currentTimeMillis())
    }

    override fun keyPressed(event: KeyEvent) {
        if (event.keyCode == java.awt.event.KeyEvent.VK_F11) {
            if (Context.isRegistered(fpsCounter, gui = true)) {
                unregister(fpsCounter, gui = true)
            } else {
                register(fpsCounter, gui = true)
            }
        } else if (event.key == ESC) {
            following = null
        }
        guiComponents().keyPressed(event)
    }

    override fun mouseReleased(event: MouseEvent) {
        guiComponents().mouseReleased(event)
    }

    override fun mousePressed(event: MouseEvent) {
        mouseClickedPosition = event.position()
        guiComponents().mousePressed(event)
        components().mousePressed(event)
    }

    override fun mouseDragged(event: MouseEvent) {
        if (!guiComponents().any { it.inside(event.position()) }) {
            translate((currentTranslation + (Context.realMousePosition() - (currentTranslation + mouseClickedPosition / currentScale))))
        }
        mouseClickedPosition = event.position()
        guiComponents().mouseDragged(event)
    }

    override fun mouseWheel(event: MouseEvent) {
        val oldPosition = event.realPosition()
        if (event.count > 0) {
            currentScale /= 1.1F
        } else {
            currentScale *= 1.1F
        }
        currentTranslation += event.realPosition() - oldPosition

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
