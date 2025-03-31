/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "AirJump", category = ModuleCategory.MOVEMENT)
class AirJump : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.onGround = true
    }
}
