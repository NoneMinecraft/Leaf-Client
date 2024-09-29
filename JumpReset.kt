/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "JumpReset", category = ModuleCategory.COMBAT)
class JumpReset : Module() {
    var jump = false
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime >= 1&&mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = true
            jump = true
        }else if (jump){
            mc.gameSettings.keyBindJump.pressed = false
            jump = false
    }
    }

    override fun onDisable() {
        jump = false
    }
}
