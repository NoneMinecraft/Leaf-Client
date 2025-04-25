package net.nonemc.leaf.features.module.modules.movement.speeds.other

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class GroundStrafeHop : SpeedMode("GroundStrafeHop") {
    override fun onPreMotion() {
        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
                EntityMoveLib.strafe()
            }
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}