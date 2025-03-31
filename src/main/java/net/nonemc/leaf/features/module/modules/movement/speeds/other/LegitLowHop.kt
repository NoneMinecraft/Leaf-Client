package net.nonemc.leaf.features.module.modules.movement.speeds.other

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils

class LegitLowHop : SpeedMode("LegitLowHop") {
    override fun onPreMotion() {
        if (mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.36
            }
            mc.thePlayer.isSprinting = true
        }
    }
}
