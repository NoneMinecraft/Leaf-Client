package net.ccbluex.liquidbounce.features.module.modules.rage.rage.control

import net.ccbluex.liquidbounce.utils.mc
private var lastHeldItemCount = 0
fun fired(): Boolean {
    if (mc.thePlayer.inventory.getStackInSlot(0).stackSize != lastHeldItemCount || mc.thePlayer.inventory.getStackInSlot(1).stackSize != lastHeldItemCount) {
        lastHeldItemCount = mc.thePlayer.heldItem.stackSize
        return true
    }else return false
}