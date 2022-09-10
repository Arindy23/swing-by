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
import de.arindy.swingby.gui.core.Context.updateDeltaTime
import de.arindy.swingby.gui.core.components.FPSCounter
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.components.TextField
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
import de.arindy.swingby.gui.entities.Body
import processing.core.PApplet
import processing.core.PFont
import processing.event.KeyEvent
import processing.event.MouseEvent
import java.awt.Font
import java.awt.Font.BOLD
import java.awt.Font.MONOSPACED
import java.text.DecimalFormat
import java.util.*

class Simulation : PApplet() {

    init {
        register(this)
    }

    private val fpsCounter = FPSCounter()
    private var mouseClickedPosition: Position = Position.ZERO
    private var following: Optional<Body> = Optional.empty()
    private var animate = false
    private val jupiter = Body(
        name = "Jupiter",
        color = Colors.jupiter,
        diameter = 139822F,
        position = Position(
            x = 3.1E8.toFloat(),
            y = 6.8E8.toFloat(),
        ),
        mass = 1.899E27,
        velocity2D = Velocity2D(
            y = -12.0,
            x = 6.1
        )
    )
    private val cassini = Body(
        name = "cassini",
        color = Colors.cassini,
        diameter = 0F,
        position = Position(
            x = 2.8E8.toFloat(),
            y = 6.5E8.toFloat(),
        ),
        mass = 4641.0,

        velocity2D = Velocity2D(
            y = -8.9,
            x = 9.5
        )
    )


    private val timeFactor = TextField(
        position = Position(10F, 10F),
        value = "100000",
        name = "TimeFactor"
    )
    private var elapsedTime = 0F
    private var elapsedTimeDays = 0F
    private var minDistance = -1F

    private var elapsedTimeLabel = Label(
        name = "",
        position = Position(0F, 50F),
        horizontalAlign = LEFT
    )

    private var elapsedRealTimeLabel = Label(
        name = "",
        position = Position(0F, 70F),
        horizontalAlign = LEFT
    )

    private var cassiniSpeedLabel = Label(
        name = "",
        position = Position(0F, 90F),
        horizontalAlign = LEFT
    )

    private var jupiterSpeedLabel = Label(
        name = "",
        position = Position(0F, 110F),
        horizontalAlign = LEFT
    )

    private var minDistanceLabel = Label(
        name = "",
        position = Position(0F, 130F),
        horizontalAlign = LEFT
    )

    override fun settings() {
        size(1240, 768)
    }

    override fun setup() {
        setupSurface()
        frameRate(1440f)
        background(0x000000)
        with(Context) {
            register(
                cassini.enableTrail().onClick(
                    action = {
                        following = tryFollow(it)
                    },
                )
            )
            register(
                jupiter.enableTrail().onClick(
                    action = {
                        following = tryFollow(it)
                    }
                )
            )
            register(
                elapsedTimeLabel,
                gui = true
            )
            register(
                elapsedRealTimeLabel,
                gui = true
            )
            register(
                cassiniSpeedLabel,
                gui = true
            )
            register(
                jupiterSpeedLabel,
                gui = true
            )
            register(
                minDistanceLabel,
                gui = true
            )
            register(
                timeFactor,
                gui = true
            )
        }
        textFont(PFont(Font(MONOSPACED, BOLD, 5), true))
        GUI().build(
            buttonActions = mapOf(Pair("Animate") {
                animate = !animate
            })
        )
        currentScale = 0.0001F
        currentTranslation = -jupiter.position + Position(
            (Context.resolution().width / currentScale) / 2,
            (Context.resolution().height) / currentScale / 2
        )
    }

    private fun Context.tryFollow(it: Body) =
        if (!insideGuiComponent() && (following.isEmpty || following.get() != it)) {
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
                deltaTime * timeFactor.doubleValue()
            )
            val decimalFormat = DecimalFormat("#.0#")
            elapsedTime += deltaTime
            elapsedTimeDays += (deltaTime * timeFactor.doubleValue() / 3600 / 24).toFloat()
            elapsedTimeLabel.name = "Time in s: ${decimalFormat.format(elapsedTime)}"
            elapsedRealTimeLabel.name = "RealTime in d: $elapsedTimeDays"
            cassiniSpeedLabel.name = "Cassini Speed in km/s: ${decimalFormat.format(cassini.data().velocity)}"
            jupiterSpeedLabel.name = "Jupiter Speed in km/s: ${decimalFormat.format(jupiter.data().velocity)}"
            minDistanceLabel.name = "Min Distance in km: $minDistance"
            jupiter.update(body)
            cassini.update(other)
            minDistance =
                if (minDistance < 0 || minDistance > cassini.data().distanceToNextBody) cassini.data().distanceToNextBody.toFloat() else minDistance
        }
        if (following.isPresent) {
            currentTranslation = -following.get().lastPosition + Position(
                (Context.resolution().width / currentScale) / 2,
                (Context.resolution().height) / currentScale / 2
            )
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
            if (Context.isRegistered(fpsCounter)) {
                Context.unregister(fpsCounter, gui = true)
            } else {
                register(fpsCounter, gui = true)
            }
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
        translate((currentTranslation + (Context.realMousePosition() - (currentTranslation + mouseClickedPosition / currentScale))))
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
