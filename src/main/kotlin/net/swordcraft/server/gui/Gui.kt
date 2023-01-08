package net.swordcraft.server.gui

import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType

interface Gui {

    fun open(player: Player)

    fun onClick(player: Player, slot: Int)

}