package net.nonemc.leaf.features.module.modules.movement.speeds.other

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class StrafeHop : SpeedMode("StrafeHop") {
    override fun onPreMotion() {
        if (EntityMoveLib.isMoving()) {
            EntityMoveLib.strafe()
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
            }
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}
