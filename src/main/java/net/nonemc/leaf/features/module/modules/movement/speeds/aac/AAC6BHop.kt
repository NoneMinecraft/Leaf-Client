﻿package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AAC6BHop : SpeedMode("AAC6BHop") {
    private var legitJump = false
    override fun onUpdate() {
        mc.timer.timerSpeed = 1f

        if (mc.thePlayer.isInWater) return

        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (legitJump) {
                    mc.thePlayer.motionY = 0.4
                    EntityMoveLib.strafe(0.15f)
                    mc.thePlayer.onGround = false
                    legitJump = false
                    return
                }
                mc.thePlayer.motionY = 0.41
                EntityMoveLib.strafe(0.47458485f)
            }

            if (mc.thePlayer.motionY < 0 && mc.thePlayer.motionY > -0.2) mc.timer.timerSpeed =
                (1.2 + mc.thePlayer.motionY).toFloat()

            mc.thePlayer.speedInAir = 0.022151f
        } else {
            legitJump = true
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onEnable() {
        legitJump = true
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer.speedInAir = 0.02f
    }
}