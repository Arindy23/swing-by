package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.CONTEXT.applet
import de.arindy.swingby.gui.core.CONTEXT.inMatrix
import de.arindy.swingby.gui.core.CONTEXT.secondElapsed
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.listener.ValueReciever
import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants
import processing.core.PConstants.BACKSPACE
import processing.core.PConstants.DELETE
import processing.core.PConstants.LEFT
import processing.event.KeyEvent
import processing.event.MouseEvent
import java.awt.event.KeyEvent.VK_END
import java.awt.event.KeyEvent.VK_HOME
import java.awt.event.KeyEvent.VK_LEFT
import java.awt.event.KeyEvent.VK_RIGHT

class TextField(
    override var position: Position,
    override var size: Size = Size(200F, 25F),
    override var scale: Float = 1F,
    override var name: String = "TestTextField",
    private var value: String = "",
    private val color: Color = Color(),
) : Component {
    private val validChars = Regex("[\\wöÖäÄüÜß-]")
    private val valueRecievers: HashSet<ValueReciever> = HashSet()

    private var focused = false
    private var index = value.length
    private var marked = IntRange.EMPTY

    fun register(reciever: ValueReciever): TextField {
        valueRecievers.add(reciever)
        return this
    }

    fun unregister(reciever: ValueReciever): TextField {
        valueRecievers.remove(reciever)
        return this
    }

    override fun draw() {
        with(applet) {
            inMatrix {
                scale(scale)
                stroke(color.foreground)
                fill(color.background)
                rect(size, position)
            }
            inMatrix {
                textSize(16F)
                textAlign(LEFT, PConstants.CENTER)
                var x = position.x + 2F
                val message = valueWithCursor()
                for (i in message.indices) {
                    if (marked.contains(i) && i != index) {
                        fill(color.selected)
                    } else {
                        fill(color.foreground)
                    }
                    text(message[i], x, position.y - 4F + size.height / 2)
                    x += textWidth(message[i])
                }
            }
        }
    }

    private fun valueWithCursor(): String {
        return if (isFocused() && secondElapsed(0.5F)) {
            value.insert('|', index)
        } else {
            value.insert(' ', index)
        }
    }

    override fun mouseReleased(event: MouseEvent) {
        focused = inside(event.position())
    }

    override fun onKeyPressed(event: KeyEvent) {
        if (event.key == BACKSPACE && value.isNotEmpty()) {
            onBackspace()
        } else if (event.key == DELETE && value.isNotEmpty() && index < value.length) {
            onDelete()
        } else if (event.keyCode == VK_HOME) {
            onHome(event)
        } else if (event.keyCode == VK_END) {
            onEnd(event)
        } else if (event.keyCode == VK_LEFT && index > 0) {
            onLeft(event)
        } else if (event.keyCode == VK_RIGHT && index < value.length) {
            onRight(event)
        } else if (event.key.toString().matches(validChars)) {
            validChar(event)
        }
    }

    private fun validChar(event: KeyEvent) {
        val tempValue = if (!marked.isEmpty()) {
            removeMarked()
        } else {
            value
        }
        if (textFitsField(tempValue)) {
            changeValue(tempValue.insert(event.key, index))
            index++
        }
    }

    private fun textFitsField(tempValue: String) = applet.textWidth(tempValue) + 16F <= size.width

    private fun onRight(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            if (marked.isEmpty()) {
                IntRange(index, index + 1)
            } else {
                if (index == marked.first && marked.first != marked.last) {
                    IntRange(index + 1, marked.last)
                } else {
                    IntRange(marked.first, index + 1)
                }
            }
        } else {
            IntRange.EMPTY
        }
        index++
    }

    private fun onLeft(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            if (marked.isEmpty()) {
                IntRange(index - 1, index)
            } else {
                if (index == marked.last && marked.first != marked.last) {
                    IntRange(marked.first, index - 1)
                } else {
                    IntRange(index - 1, marked.last)
                }
            }
        } else {
            IntRange.EMPTY
        }
        index--
    }

    private fun onEnd(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            IntRange(index, value.length)
        } else {
            IntRange.EMPTY
        }
        index = value.length
    }

    private fun onHome(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            IntRange(0, index)
        } else {
            IntRange.EMPTY
        }
        index = 0
    }

    private fun onDelete() {
        val newValue = if (marked.isEmpty()) {
            value.removeRange(index, index + 1)
        } else {
            removeMarked()
        }
        changeValue(newValue)
    }

    private fun onBackspace() {
        val newValue = if (marked.isEmpty()) {
            index--
            value.removeRange(index, index + 1)
        } else {
            removeMarked()
        }
        changeValue(newValue)
    }

    private fun removeMarked(): String {
        return value.removeRange(marked.first, marked.last)
    }

    private fun changeValue(newValue: String) {
        marked = IntRange.EMPTY
        valueRecievers.forEach { it.onValueChanged(value, newValue) }
        value = newValue
    }

    override fun isFocused(): Boolean {
        return focused
    }

    override fun toString(): String {
        return asString()
    }
}

private fun String.insert(key: Char, index: Int): String {
    return substring(0, index) + key + substring(index)
}
