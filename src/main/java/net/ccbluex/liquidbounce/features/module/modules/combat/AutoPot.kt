package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.FindItems
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemPotion

@ModuleInfo(name = "AutoPot", category = ModuleCategory.COMBAT)
class AutoPot : Module() {
    private val health = FloatValue("Health",15F,0F,20F)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.health < health.get()){
        }

    }
}