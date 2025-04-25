package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class OldAACBHop : SpeedMode("OldAACBHop") {
    override fun onPreMotion() {
        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.onGround) {
                EntityMoveLib.strafe(0.56f)
                mc.thePlayer.motionY = 0.41999998688697815
            } else {
                EntityMoveLib.strafe(EntityMoveLib.getSpeed() * if (mc.thePlayer.fallDistance > 0.4f) 1.0f else 1.01f)
            }
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}