﻿package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.minecraft.util.MathHelper
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AACBHop : SpeedMode("AACBHop") {
    override fun onPreMotion() {
        if (mc.thePlayer.isInWater) return

        if (EntityMoveLib.isMoving()) {
            mc.timer.timerSpeed = 1.08f
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.399
                val f = mc.thePlayer.rotationYaw * 0.017453292f
                mc.thePlayer.motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
                mc.thePlayer.motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
                mc.timer.timerSpeed = 2f
            } else {
                mc.thePlayer.motionY *= 0.97
                mc.thePlayer.motionX *= 1.008
                mc.thePlayer.motionZ *= 1.008
            }
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            mc.timer.timerSpeed = 1f
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}