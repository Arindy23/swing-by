package de.arindy.swingby.gui

import de.arindy.swingby.gui.core.Context
import de.arindy.swingby.gui.core.components.Button
import de.arindy.swingby.gui.core.components.Label
import de.arindy.swingby.gui.core.components.TextField
import de.arindy.swingby.gui.core.units.Position

class GUI {

    fun build() {
        with(Context) {
            register(
                Button(
                    position = Position(220F, 10F)
                ).registerAction("") { println("Button pressed") }, gui = true
            )
            register(
                TextField(
                    position = Position(10F, 10F),
                    value = "Test"
                ).register { _, newValue -> println(newValue) }, gui = true
            )
            register(
                Label(
                    name = "TestLabel",
                    position = Position(330F, 10F),
                ), gui = true
            )
        }
    }

}
