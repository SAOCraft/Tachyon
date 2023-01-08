package net.swordcraft.server.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration
import net.swordcraft.server.Tachyon

fun String?.color(): Component {
    return Tachyon.miniMessage.deserialize(this ?: "<red>NULL</red>").decoration(TextDecoration.ITALIC, false)
}

fun Component?.uncolor(): String {
    if (this == null) return "NULL"
    if (this !is TextComponent) return this.toString()
    return this.content()
}

fun List<String>.color(): List<Component> {
    return this.map { it.color() }
}

fun List<Component>.uncolor(): List<String> {
    return this.map { it.uncolor() }
}