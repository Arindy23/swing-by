package de.arindy.swingby.gui.core.components

import de.arindy.swingby.gui.core.Context.applet
import de.arindy.swingby.gui.core.Context.inMatrix
import de.arindy.swingby.gui.core.Context.secondElapsed
import de.arindy.swingby.gui.core.fill
import de.arindy.swingby.gui.core.position
import de.arindy.swingby.gui.core.rect
import de.arindy.swingby.gui.core.stroke
import de.arindy.swingby.gui.core.units.Color
import de.arindy.swingby.gui.core.units.Colors
import de.arindy.swingby.gui.core.units.Position
import de.arindy.swingby.gui.core.units.Size
import processing.core.PConstants
import processing.core.PConstants.BACKSPACE
import processing.core.PConstants.DELETE
import processing.core.PConstants.LEFT
import processing.event.KeyEvent
import processing.event.MouseEvent
import java.awt.event.KeyEvent.VK_A
import java.awt.event.KeyEvent.VK_END
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_HOME
import java.awt.event.KeyEvent.VK_LEFT
import java.awt.event.KeyEvent.VK_RIGHT
import java.util.*

class TextField(
    override var position: () -> Position,
    override var size: Size = Size(200F, 25F),
    override var name: () -> String,
    private var value: () -> String = { "" },
    private val color: Color = Colors.primary,
    validCharacters: String = ".\\wöÖäÄüÜß-",
    private val textSize: Float = 16F,
    private val changeOnEnter: Boolean = false
) : Component {
    private var newValue: () -> String = value

    private val validChars = Regex("[$validCharacters]")
    private val valueReceivers: HashMap<String, ((String, String) -> Unit)> = HashMap()

    private var focused = false
    private var index = value().length
    private var marked = IntRange.EMPTY

    fun register(actions: Map<String, (oldValue: String, newValue: String) -> Unit>): TextField {
        actions.forEach { register(it.key, it.value) }
        return this
    }

    fun register(
        name: String = UUID.randomUUID().toString(),
        block: (oldValue: String, newValue: String) -> Unit
    ): TextField {
        valueReceivers[name] = block
        return this
    }

    fun unregister(name: String): TextField {
        valueReceivers.remove(name)
        return this
    }

    override fun draw() {
        with(applet) {
            inMatrix {
                stroke(color.foreground)
                fill(color.background)
                rect(position(), size)
            }
            inMatrix {
                textSize(textSize)
                textAlign(LEFT, PConstants.CENTER)
                var x = position().x + 2F
                val message = valueWithCursor()
                for (i in message.indices) {
                    if (marked.contains(i) && i != index) {
                        fill(color.selected)
                    } else {
                        fill(color.foreground)
                    }
                    text(message[i], x, position().y - 4F + size.height / 2)
                    x += textWidth(message[i])
                }
                if (value() != newValue()) {
                    fill("0xFF0000")
                    text("*", position().x + size.width - 7F, position().y - 4F + size.height / 2)
                }
            }
        }
    }

    private fun valueWithCursor(): String {
        return if (isFocused() && secondElapsed(0.5F)) {
            newValue().insert('|', index())
        } else if (isFocused()) {
            newValue().insert(' ', index())
        } else {
            newValue()
        }
    }

    override fun mouseReleased(event: MouseEvent) {
        if (!focused) {
            index = newValue().length
        }
        focused = inside(event.position())
    }

    override fun onKeyPressed(event: KeyEvent) {
        if (event.isControlDown && event.keyCode == VK_A) {
            selectAll()
        } else if (event.key == BACKSPACE && newValue().isNotEmpty() && index != 0) {
            onBackspace()
        } else if (event.key == DELETE && newValue().isNotEmpty() && index < newValue().length) {
            onDelete()
        } else if (event.keyCode == VK_HOME) {
            onHome(event)
        } else if (event.keyCode == VK_END) {
            onEnd(event)
        } else if (event.keyCode == VK_LEFT && index > 0) {
            onLeft(event)
        } else if (event.keyCode == VK_RIGHT && index < newValue().length) {
            onRight(event)
        } else if (event.key.toString().matches(validChars)) {
            insertChar(event)
        } else if (event.keyCode == VK_ENTER && changeOnEnter) {
            valueReceivers.forEach { it.value(value(), newValue()) }
            newValue = value
            index = index()
        }
    }

    private fun insertChar(event: KeyEvent) {
        val tempValue = if (!marked.isEmpty()) {
            removeMarked()
        } else {
            newValue()
        }
        if (textFitsField(tempValue)) {
            changeValue(tempValue.insert(event.key, index()))
            index++
        }
    }

    private fun textFitsField(tempValue: String): Boolean {
        applet.textSize(textSize)
        return applet.textWidth(tempValue) + textSize <= size.width
    }

    private fun onRight(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            if (marked.isEmpty()) {
                IntRange(index(), index() + 1)
            } else {
                if (index() == marked.first && marked.first != marked.last) {
                    IntRange(index() + 1, marked.last)
                } else {
                    IntRange(marked.first, index() + 1)
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
                IntRange(index() - 1, index())
            } else {
                if (index() == marked.last && marked.first != marked.last) {
                    IntRange(marked.first, index() - 1)
                } else {
                    IntRange(index() - 1, marked.last)
                }
            }
        } else {
            IntRange.EMPTY
        }
        index--
    }

    private fun onEnd(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            IntRange(index(), newValue().length)
        } else {
            IntRange.EMPTY
        }
        index = newValue().length
    }

    private fun onHome(event: KeyEvent) {
        marked = if (event.isShiftDown) {
            IntRange(0, index())
        } else {
            IntRange.EMPTY
        }
        index = 0
    }

    private fun onDelete() {
        val newValue = if (marked.isEmpty()) {
            newValue().removeRange(index(), index() + 1)
        } else {
            removeMarked()
        }
        changeValue(newValue)
    }

    private fun selectAll() {
        marked = IntRange(0, newValue().length)
        index = 0
    }

    private fun onBackspace() {
        val newValue = if (marked.isEmpty()) {
            index--
            newValue().removeRange(index(), index() + 1)
        } else {
            removeMarked()
        }
        changeValue(newValue)
    }

    private fun removeMarked(): String {
        return newValue().removeRange(marked.first, marked.last)
    }

    private fun changeValue(newValue: String) {
        marked = IntRange.EMPTY
        if (!changeOnEnter) {
            valueReceivers.forEach { it.value(value(), newValue) }
            this.newValue = { newValue }
            this.value = { newValue }
        } else {
            this.newValue = { newValue }
        }
        index = index()
    }

    private fun index(): Int {
        return if (index < newValue().length) index else newValue().length
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
