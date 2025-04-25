package net.nonemc.leaf.features.module.modules.rage.rage.control

import net.nonemc.leaf.libs.base.mc

private var lastHeldItemCount = 0
fun fired(): Boolean {
    if (net.nonemc.leaf.libs.base.mc.thePlayer.inventory.getStackInSlot(0).stackSize != lastHeldItemCount || net.nonemc.leaf.libs.base.mc.thePlayer.inventory.getStackInSlot(
            1
        ).stackSize != lastHeldItemCount
    ) {
        lastHeldItemCount = net.nonemc.leaf.libs.base.mc.thePlayer.heldItem.stackSize
        return true
    } else return false
}