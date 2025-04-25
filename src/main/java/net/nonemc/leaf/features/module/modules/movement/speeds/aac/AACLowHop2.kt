package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AACLowHop2 : SpeedMode("AACLowHop2") {
    private var legitJump = false

    override fun onEnable() {
        legitJump = true
        mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onPreMotion() {
        mc.timer.timerSpeed = 1f

        if (mc.thePlayer.isInWater) return

        if (EntityMoveLib.isMoving()) {
            mc.timer.timerSpeed = 1.09f
            if (mc.thePlayer.onGround) {
                if (legitJump) {
                    mc.thePlayer.jump()
                    legitJump = false
                    return
                }
                mc.thePlayer.motionY = 0.343
                EntityMoveLib.strafe(0.534f)
            }
        } else {
            legitJump = true
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}