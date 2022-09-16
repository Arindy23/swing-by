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
import java.util.*

class Simulation : PApplet() {

    init {
        register(this)
    }

    private val fpsCounter = FPSCounter()
    private var mouseClickedPosition: Position = Position.ZERO
    private var following: Optional<Body> = Optional.empty()
    private var animate = false
    private var centerOnScreen = false
    private lateinit var gui: GUI

    private val jupiter = Body(
        name = { "Jupiter" },
        color = Colors.jupiter,
        diameter = 139822F,
        position = Position(
            x = 3.1E8.toFloat(),
            y = 6.8E8.toFloat(),
        ),
        infoPosition = Position(10F, 190F),
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
        position = Position(
            x = 2.8E8.toFloat(),
            y = 6.5E8.toFloat(),
        ),
        infoPosition = Position(10F, 380F),
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

    private val followingLabel = Label(
        name = { "" },
        position = Position.ZERO
    )

    private val unfollow = Button(
        name = { "Unfollow" },
        position = Position.ZERO,
        size = Size(100F, 25F)
    ).registerAction { following = Optional.empty() }

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
        register(
            body.enableTrail().onClick(
                action = {
                    following = tryFollow(it)
                }
            ),
        )
    }

    private fun centerOnScreen() {
        following = Optional.empty()
        currentScale = min(
            resolution().width / (abs(cassini.lastPosition.x - jupiter.lastPosition.x) * 2F),
            resolution().height / (abs(cassini.lastPosition.y - jupiter.lastPosition.y) * 2F),
        )
        currentTranslation = -middle(jupiter.lastPosition, cassini.lastPosition) + Position(
            (resolution().width / currentScale) / 2,
            (resolution().height) / currentScale / 2
        )
    }

    private fun tryFollow(it: Body) =
        if ((following.isEmpty || following.get() != it)) {
            if (it.diameter * currentScale > resolution().height / 2) {
                currentScale = resolution().height / (it.diameter * 2F)
            }
            Optional.of(it)
        } else {
            Optional.empty()
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
        } else if (following.isPresent) {
            currentTranslation = -following.get().lastPosition + Position(
                (resolution().width / currentScale) / 2,
                (resolution().height) / currentScale / 2
            )
        }
        inMatrix {
            scaleWithContext(currentScale)
            translate(currentTranslation)
            components().draw()
        }
        guiComponents().draw()
        drawFollow()
        updateDeltaTime(System.currentTimeMillis())
    }

    private fun drawFollow() {
        if (!centerOnScreen && following.isPresent) {
            followingLabel.name = { "Following: ${if (following.isPresent) following.get().name() else ""}" }
            followingLabel.position = Position((resolution().width - followingLabel.size.width) / 2, 10F)
            unfollow.position = Position((resolution().width - 100F) / 2, 40F)
            register(
                followingLabel,
                gui = true
            )
            register(
                unfollow,
                gui = true
            )
        } else {
            unregister(followingLabel, gui = true)
            unregister(unfollow, gui = true)
        }
    }

    override fun keyPressed(event: KeyEvent) {
        if (event.keyCode == java.awt.event.KeyEvent.VK_F11) {
            if (Context.isRegistered(fpsCounter, gui = true)) {
                unregister(fpsCounter, gui = true)
            } else {
                register(fpsCounter, gui = true)
            }
        } else if (event.key == ESC) {
            following = Optional.empty()
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
