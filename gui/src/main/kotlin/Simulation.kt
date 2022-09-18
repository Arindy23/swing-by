package de.arindy.swingby.gui

import de.arindy.swingby.core.calculateSwingByStep
import de.arindy.swingby.core.data.Velocity2D
import de.arindy.swingby.core.distance
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
import de.arindy.swingby.gui.entities.asCoordinates
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

    private val bodies: MutableList<Body> = mutableListOf(
        Body(
            name = { "Jupiter" },
            color = Colors.jupiter,
            diameter = 139822F,
            position = {
                Position(
                    x = 3.569001017514159E8.toFloat(),
                    y = 5.877375047510158E8.toFloat(),
                )
            },
            mass = 1.899E27,
            velocity2D = Velocity2D(
                x = 6.1,
                y = -12.0
            )
        ),
        Body(
            name = { "Cassini" },
            color = Colors.cassini,
            diameter = 0F,
            position = {
                Position(
                    x = 3.554614806656895E8.toFloat(),
                    y = 5.879277724825814E8.toFloat(),
                )
            },
            mass = 4641.0,
            velocity2D = Velocity2D(
                x = -1.43657077142615,
                y = -17.018430251489907
            )
        )
    )

    private var timeFactor = 100000.0
    private var elapsedTimeDays = 0F

    override fun settings() {
        size(1240, 768)
    }

    override fun setup() {
        setupSurface()
        frameRate(1440f)
        background(0x000000)
        textFont(PFont(Font(MONOSPACED, BOLD, 5), true))
        bodies.forEach {
            with(Context) { registerBody(it) }
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
        register(
            Button(
                position = { Position(10F, 160f + (bodyLabels.size + 1) * 25F) },
                name = { "add Body " }
            ).registerAction {
                with(Context) {
                    val body = Body(
                        name = { "new Body" },
                        color = Colors.primary,
                        diameter = 0F,
                        position = { coordinatesInCenter() },
                        mass = 0.0,
                        velocity2D = Velocity2D(
                            x = 0.0,
                            y = 0.0
                        )
                    )
                    val (label, button) = registerBody(body)
                    bodies.add(body)
                    register(label, gui = true)
                    register(button, gui = true)
                }
            }, gui = true
        )
        centerOnScreen()
    }

    private fun Context.registerBody(body: Body): Pair<Label, Button> {
        val verticalPosition = bodyLabels.size * 25F
        val result = Pair(
            Label(
                position = { Position(10F, 160F + verticalPosition) },
                size = Size(width = 100F, 20F),
                name = { body.name() },
                color = { body.color },
                textSize = 16F
            ),
            Toggle(
                position = { Position(120F, 160F + verticalPosition) },
                size = Size(width = 100F, 20F),
                name = { if (body.infoVisible()) "hide info" else "show info" },
                toggle = { body.infoVisible() }
            ).registerAction {
                body.toggleInfo()
            }
        )
        bodyLabels.add(result)
        register(
            body.enableTrail().onClick(
                action = {
                    following = tryFollow(it)
                }
            ),
        )
        return result
    }

    private fun centerOnScreen() {
        following = following?.unfollow()
        var mostAppartBodies: Pair<Body, Body>? = null
        for (i in 1 until bodies.size) {
            bodies.filterIndexed { index, _ -> index != i }.forEach { body ->
                var oldDistance = 0.0
                mostAppartBodies?.let {
                    oldDistance = distance(
                        it.first.lastPosition.asCoordinates(),
                        it.second.lastPosition.asCoordinates()
                    )
                }
                if (oldDistance < distance(bodies[i].lastPosition.asCoordinates(), body.lastPosition.asCoordinates())) {
                    mostAppartBodies = Pair(bodies[i], body)
                }
            }
        }
        currentScale = min(
            resolution().width / (abs(mostAppartBodies!!.first.lastPosition.x - mostAppartBodies!!.second.lastPosition.x) * 2F),
            resolution().height / (abs(mostAppartBodies!!.first.lastPosition.y - mostAppartBodies!!.second.lastPosition.y) * 2F),
        )
        currentTranslation =
            -middle(mostAppartBodies!!.first.lastPosition, mostAppartBodies!!.second.lastPosition) + Position(
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
            val newBodies =
                calculateSwingByStep(this.bodies.associate { it.name() to it.data() }, deltaTime * timeFactor)
            gui.updateElapsedRealTime(incrementElapsedTimeAndReturn())
            bodies.forEach { newBodies[it.name()]?.let { newBody -> it.update(newBody) } }
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

    private fun incrementElapsedTimeAndReturn(): Float {
        elapsedTimeDays += (deltaTime * timeFactor / 3600 / 24).toFloat()
        return elapsedTimeDays
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
