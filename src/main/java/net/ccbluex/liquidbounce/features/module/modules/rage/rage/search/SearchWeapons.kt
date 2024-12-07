package net.ccbluex.liquidbounce.features.module.modules.rage.rage.search

import net.ccbluex.liquidbounce.features.module.modules.rage.rage.WeaponType
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.mc

fun hasWeapon(type: WeaponType): Boolean {
    val player = mc.thePlayer ?: return false
    val playerInventory = player.inventory ?: return false
    for (i in 0 until playerInventory.sizeInventory) {
        val stack = playerInventory.getStackInSlot(i) ?: continue
        if (stack.item in type.items) return true
    }
    return false
}