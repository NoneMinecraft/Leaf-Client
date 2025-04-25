package net.nonemc.leaf.features.module.modules.movement.speeds.ncp

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class NCPLatest : SpeedMode("NCPLatest") {


    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
        super.onDisable()
    }

    override fun onUpdate() {

        mc.thePlayer.jumpMovementFactor = 0.02725f

        if (mc.thePlayer.ticksExisted % 20 <= 9) {
            mc.timer.timerSpeed = 1.05f
        } else {
            mc.timer.timerSpeed = 0.98f
        }

        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
            }
            EntityMoveLib.strafe(EntityMoveLib.getSpeed() * 1.0035f)
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}
