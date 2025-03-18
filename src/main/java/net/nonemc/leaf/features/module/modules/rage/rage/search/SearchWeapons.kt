package net.nonemc.leaf.features.module.modules.rage.rage.search

import net.nonemc.leaf.features.module.modules.rage.rage.WeaponType
import net.nonemc.leaf.utils.mc
import net.minecraft.client.Minecraft
import net.minecraft.item.Item

fun hasWeapon(type: WeaponType): Boolean {
    val player = mc.thePlayer ?: return false
    val playerInventory = player.inventory ?: return false
    for (i in 0 until playerInventory.sizeInventory) {
        val stack = playerInventory.getStackInSlot(i) ?: continue
        if (stack.item in type.items) return true
    }
    return false
} fun findItem(itemToFind: Item): Int {
    val mc = Minecraft.getMinecraft()
    val player = mc.thePlayer ?: return -1
    val inventory = player.inventory ?: return -1

    for (i in 0 until inventory.mainInventory.size) {
        val stack = inventory.getStackInSlot(i) ?: continue
        if (stack.item == itemToFind) {
            return i
        }
    }
    return -1
}