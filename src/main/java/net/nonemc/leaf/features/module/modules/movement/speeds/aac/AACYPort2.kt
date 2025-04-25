package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AACYPort2 : SpeedMode("AACYPort2") {
    override fun onPreMotion() {
        if (EntityMoveLib.isMoving()) {
            mc.thePlayer.cameraPitch = 0f
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
                mc.thePlayer.motionY = 0.3851
                mc.thePlayer.motionX *= 1.01
                mc.thePlayer.motionZ *= 1.01
            } else mc.thePlayer.motionY = -0.21
        }
    }
}