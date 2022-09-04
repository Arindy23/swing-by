package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size

class Button(
    override var position: Position = Position.ZERO,
    override var size: Size = Size.ZERO,
    override var scale: Float = 1F,
    override var name: String = "Test"
) : Component {

}
