package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "WTap", category = ModuleCategory.COMBAT)
class WTap : Module() {
    var isattack = false
    override fun onDisable() {
        isattack = false
    }
    @EventTarget
    fun AttackEvent(event: AttackEvent) {
        isattack = true
    }
    @EventTarget
    fun UpdateEvent(event: UpdateEvent) {
        if (isattack){
            isattack = false
            mc.thePlayer.isSprinting = false
        }else{
            mc.thePlayer.isSprinting
        }
    }
}