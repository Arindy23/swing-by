package de.arindy.swingby.gui

import de.arindy.swingby.gui.core.CONTEXT
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.TextField
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Position

class GUI {

    fun build() {
        with(CONTEXT) {
            register(Button())
            register(
                TextField(
                    position = Position(200F, 200F),
                    color = Color(background = "0x555555", foreground = "0x000000"),
                    value = "Test"
                ), gui = true)
        }
    }

}
