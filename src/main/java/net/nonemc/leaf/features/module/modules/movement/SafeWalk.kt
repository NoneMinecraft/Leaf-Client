/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.MoveEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue

@ModuleInfo(name = "SafeWalk", category = ModuleCategory.MOVEMENT)
class SafeWalk : Module() {

    private val airSafeValue = BoolValue("AirSafe", false)
    private val onlyVoidValue = BoolValue("OnlyPredictVoid", false)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (onlyVoidValue.get() && !checkVoid()) {
            return
        } else {
            if (airSafeValue.get() || mc.thePlayer.onGround) {
                event.isSafeWalk = true
            }
        }
    }

    private fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld.getCollisionBoxes(
                mc.thePlayer.entityBoundingBox.offset(
                    mc.thePlayer.motionX * 1.4,
                    i.toDouble(),
                    mc.thePlayer.motionZ * 1.4
                )
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }
}
