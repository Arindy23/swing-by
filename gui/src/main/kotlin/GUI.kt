package de.arindy.swingby.gui

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.Coordinates
import de.arindy.swingby.gui.core.units.Position

class GUI {

    fun build(
        buttonActions: Map<String, () -> Unit> = HashMap()
    ) {
        with(Context) {
            register(
                Button(
                    position = Position(220F, 10F),
                    name = "Animate"
                ).registerAction("Debug") { println("Button Animate pressed") }
                    .registerActions(buttonActions),
                gui = true
            )
            register(
                Coordinates(),
                gui = true
            )
        }
    }

}
