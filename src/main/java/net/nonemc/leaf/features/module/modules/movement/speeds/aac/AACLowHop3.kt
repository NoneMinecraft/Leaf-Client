package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib
import kotlin.math.cos
import kotlin.math.sin

class AACLowHop3 : SpeedMode("AACLowHop3") {
    private var firstJump = false
    private var waitForGround = false

    override fun onEnable() {
        firstJump = true
    }

    override fun onPreMotion() {
        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.hurtTime <= 0) {
                if (mc.thePlayer.onGround) {
                    waitForGround = false
                    if (!firstJump) firstJump = true
                    mc.thePlayer.jump()
                    mc.thePlayer.motionY = 0.41
                } else {
                    if (waitForGround) return
                    if (mc.thePlayer.isCollidedHorizontally) return
                    firstJump = false
                    mc.thePlayer.motionY -= 0.0149
                }
                if (!mc.thePlayer.isCollidedHorizontally) EntityMoveLib.forward(if (firstJump) 0.0016 else 0.001799)
            } else {
                firstJump = true
                waitForGround = true
            }
        } else {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = 0.0
        }

        val speed = EntityMoveLib.getSpeed().toDouble()
        mc.thePlayer.motionX = -(sin(EntityMoveLib.direction) * speed)
        mc.thePlayer.motionZ = cos(EntityMoveLib.direction) * speed
    }
}