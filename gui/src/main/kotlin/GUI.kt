package de.arindy.swingby.gui

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.components.Coordinates
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.components.TextField
import de.arindy.swingby.gui.core.components.Toggle
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PApplet
import processing.core.PConstants.LEFT
import java.awt.event.KeyEvent.VK_C
import java.awt.event.KeyEvent.VK_SPACE

class GUI {

    private var elapsedRealTimeLabel = Label(
        name = { "0" },
        position = { Position(175F, 10F) },
        size = Size(100F, 25F),
        horizontalAlign = PApplet.LEFT,
        textSize = 12F
    )

    fun build(
        animate: Map<String, () -> Unit> = HashMap(),
        centerOnScreen: Map<String, () -> Unit> = HashMap(),
        timeFactor: Map<String, (String, String) -> Unit> = HashMap(),
    ): GUI {
        with(Context) {
            register(
                Label(
                    name = { "Elapsed RealTime in d:" },
                    position = { Position(10F, 10F) },
                    size = Size(170F, 25F),
                    horizontalAlign = LEFT,
                    textSize = 12F
                ),
                gui = true
            )
            register(
                elapsedRealTimeLabel,
                gui = true
            )
            register(
                Label(
                    position = { Position(10F, 45F) },
                    size = Size(85F, 25F),
                    name = { "TimeFactor:" },
                    horizontalAlign = LEFT,
                    textSize = 12F
                ),
                gui = true
            )
            register(
                TextField(
                    position = { Position(100F, 45F) },
                    size = Size(100F, 25F),
                    value = { "100000" },
                    name = { "TimeFactor" },
                ).register(timeFactor),
                gui = true
            )
            register(
                Toggle(
                    position = { Position(10F, 80F) },
                    name = { "Animate" },
                    shortcutKey = VK_SPACE,
                ).registerActions(animate),
                gui = true
            )
            register(
                Toggle(
                    position = { Position(10F, 115F) },
                    name = { "Center on Screen" },
                    shortcutKey = VK_C
                ).registerActions(centerOnScreen),
                gui = true
            )

            register(
                Coordinates(),
                gui = true
            )
        }
        return this
    }

    fun updateElapsedRealTime(elapsedTimeDays: Float) {
        elapsedRealTimeLabel.name = { "$elapsedTimeDays" }
    }

}
