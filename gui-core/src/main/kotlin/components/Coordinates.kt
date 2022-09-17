package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.Context.currentTranslation
import de.arindy.swingby.gui.core.Context.resolution
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size

class Coordinates : Label({ Position.ZERO }, name = { "Coordinates" }) {

    override fun draw() {
        name = {
            "Coordinates: ${
                currentTranslation + Position(
                    (resolution().width / Context.currentScale) / 2,
                    (resolution().height) / Context.currentScale / 2
                )
            }"
        }
        position =
            { Position(resolution().width / 2 - Context.applet.textWidth(name()) / 2, resolution().height - 35F) }
        size = Size(Context.applet.textWidth(name()), size.height)
        super.draw()
    }
}
