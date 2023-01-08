package net.swordcraft.server.gui.inventory

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.swordcraft.server.Tachyon
import net.swordcraft.server.gui.Gui
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class GuiInventory(
    type: InventoryType,
    title: Component,
    val style: FillerStyle = FillerStyle.SOLID
) : Gui, Inventory(type, title) {

    abstract fun setContents()

    override fun open(player: Player) {
        buildFiller()
        setContents()
        player.openInventory(this)
    }

    abstract fun onClick(player: Player, slot: Int, clickType: ClickType)

    override fun onClick(player: Player, slot: Int) {
        logger.info("Invalid click. Please use onClick(player: Player, slot: Int, clickType: ClickType)")
    }

    protected fun buildFiller() {
        when (style) {
            FillerStyle.NONE -> {}
            FillerStyle.SOLID -> {
                for (i in 0 until size) {
                    setItemStack(i, FILLER_ONE)
                }
            }
            FillerStyle.CHECKERED -> {
                for (i in 0 until size) {
                    setItemStack(i, if (i % 2 == 0) FILLER_ONE else FILLER_TWO)
                }
            }
            FillerStyle.BORDERED -> {
                for (i in 0 until size) {
                    if (i < 9 || i % 9 == 0 || i % 9 == 8 || i > size - 9) {
                        setItemStack(i, FILLER_ONE)
                    }
                }
            }
            FillerStyle.BORDERED_CHECKERED -> {
                for (i in 0 until size) {
                    if (i < 9 || i % 9 == 0 || i % 9 == 8 || i > size - 9) {
                        setItemStack(i, if (i % 2 == 0) FILLER_ONE else FILLER_TWO)
                    }
                }
            }
        }

    }

    companion object  {

        protected val logger: Logger = LoggerFactory.getLogger(GuiInventory::class.java)

        val FILLER_ONE = ItemStack.of(
            Material.fromNamespaceId(
                Tachyon.config.getString("gui.filler-one", "minecraft:black_stained_glass_pane")
            ) ?: Material.BLACK_STAINED_GLASS_PANE
        )

        val FILLER_TWO = ItemStack.of(
            Material.fromNamespaceId(
                Tachyon.config.getString("gui.filler-two", "minecraft:gray_stained_glass_pane")
            ) ?: Material.GRAY_STAINED_GLASS_PANE
        )

    }

}