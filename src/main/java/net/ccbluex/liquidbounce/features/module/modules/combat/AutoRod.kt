package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

@ModuleInfo(name = "AutoRod", category = ModuleCategory.COMBAT)
object AutoRod : Module() {
    private var current = 0
    private var a = false
    override fun onDisable() {
        a = false
    }
    private
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.gameSettings.keyBindUseItem.pressed){
            if (findItem(Items.fishing_rod) != -1){
                if (!a) {
                    current = mc.thePlayer.inventory.currentItem
                    a = true
                }
                mc.thePlayer.inventory.currentItem = findItem(Items.fishing_rod)
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }else a = false
        }else{
            mc.thePlayer.inventory.currentItem = current
            a = false
        }
    }
    fun findItem(itemToFind: Item): Int {
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
}