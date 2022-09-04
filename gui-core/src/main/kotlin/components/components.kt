package de.arindy.swingby.gui.core.components

import kotlinx.collections.immutable.ImmutableList
import processing.event.KeyEvent
import processing.event.MouseEvent


fun ImmutableList<Component>.draw() {
    this.forEach { component -> component.draw() }
}

fun ImmutableList<Component>.keyPressed(event: KeyEvent) {
    val focusedComponents = this.filter { component -> component.isFocused() }
    focusedComponents.forEach{ it.onKeyPressed(event)}
}

fun ImmutableList<Component>.mouseReleased(event: MouseEvent) {
    this.forEach { component -> component.mouseReleased(event) }
}

fun ImmutableList<Component>.mousePressed(event: MouseEvent) {
    this.forEach { component -> component.mousePressed(event) }
}

fun ImmutableList<Component>.mouseDragged(event: MouseEvent) {
    this.forEach { component -> component.mouseDragged(event) }
}
