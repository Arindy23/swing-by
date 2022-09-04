package de.arindy.swingby.gui.core.units

class Size(val width: Float, val height: Float) {
    companion object {
        val ZERO: Size = Size(0F, 0F)
    }

    override fun toString(): String {
        return "[width: $width, height: $height]"
    }
}
