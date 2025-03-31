package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils

class AACYPort : SpeedMode("AACYPort") {
    override fun onPreMotion() {
        if (MovementUtils.isMoving() && !mc.thePlayer.isSneaking) {
            mc.thePlayer.cameraPitch = 0f
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.3425
                mc.thePlayer.motionX *= 1.5893
                mc.thePlayer.motionZ *= 1.5893
            } else mc.thePlayer.motionY = -0.19
        }
    }
}