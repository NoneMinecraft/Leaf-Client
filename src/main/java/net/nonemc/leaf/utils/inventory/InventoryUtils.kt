package net.nonemc.leaf.utils.inventory

import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.utils.timer.MSTimer

object InventoryUtils : MinecraftInstance(), Listenable {
    val CLICK_TIMER = MSTimer()
    private val INV_TIMER = MSTimer()
    val BLOCK_BLACKLIST = listOf(
        Blocks.enchanting_table,
        Blocks.chest,
        Blocks.ender_chest,
        Blocks.trapped_chest,
        Blocks.anvil,
        Blocks.sand,
        Blocks.web,
        Blocks.torch,
        Blocks.crafting_table,
        Blocks.furnace,
        Blocks.waterlily,
        Blocks.dispenser,
        Blocks.stone_pressure_plate,
        Blocks.wooden_pressure_plate,
        Blocks.red_flower,
        Blocks.flower_pot,
        Blocks.yellow_flower,
        Blocks.noteblock,
        Blocks.dropper,
        Blocks.standing_banner,
        Blocks.wall_banner
    )

    fun isBlockListBlock(itemBlock: ItemBlock): Boolean {
        val block = itemBlock.getBlock()
        return BLOCK_BLACKLIST.contains(block) || !block.isFullCube
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0EPacketClickWindow || packet is C08PacketPlayerBlockPlacement) {
            INV_TIMER.reset()
        }
        if (packet is C08PacketPlayerBlockPlacement) {
            CLICK_TIMER.reset()
        } else if (packet is C0EPacketClickWindow) {
            CLICK_TIMER.reset()
        }
    }

    override fun handleEvents() = true
}
