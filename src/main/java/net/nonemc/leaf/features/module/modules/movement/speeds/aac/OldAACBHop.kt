﻿package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils

class OldAACBHop : SpeedMode("OldAACBHop") {
    override fun onPreMotion() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                MovementUtils.strafe(0.56f)
                mc.thePlayer.motionY = 0.41999998688697815
            } else {
                MovementUtils.strafe(MovementUtils.getSpeed() * if (mc.thePlayer.fallDistance > 0.4f) 1.0f else 1.01f)
            }
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}