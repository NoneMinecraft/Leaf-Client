package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib
import kotlin.math.cos
import kotlin.math.sin

class AAC7BHop : SpeedMode("AAC7BHop") {
    override fun onUpdate() {
        if (!EntityMoveLib.isMoving() || mc.thePlayer.ridingEntity != null || mc.thePlayer.hurtTime > 0) return

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.405
            mc.thePlayer.motionX *= 1.004
            mc.thePlayer.motionZ *= 1.004
            return
        }

        val speed = EntityMoveLib.getSpeed() * 1.0072
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        mc.thePlayer.motionX = -sin(yaw) * speed
        mc.thePlayer.motionZ = cos(yaw) * speed
    }
}