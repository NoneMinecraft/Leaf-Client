package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AAC4BHop : SpeedMode("AAC4BHop") {
    private var legitHop = false

    override fun onEnable() {
        legitHop = true
    }

    override fun onDisable() {
        mc.thePlayer.speedInAir = 0.02f
    }

    override fun onTick() {
        if (EntityMoveLib.isMoving()) {
            if (legitHop) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    mc.thePlayer.onGround = false
                    legitHop = false
                }
                return
            }
            if (mc.thePlayer.onGround) {
                mc.thePlayer.onGround = false
                EntityMoveLib.strafe(0.375f)
                mc.thePlayer.jump()
                mc.thePlayer.motionY = 0.41
            } else mc.thePlayer.speedInAir = 0.0211f
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            legitHop = true
        }
    }
}