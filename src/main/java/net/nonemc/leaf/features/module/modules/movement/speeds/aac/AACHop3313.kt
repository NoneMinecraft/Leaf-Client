package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.minecraft.block.BlockCarpet
import net.minecraft.util.MathHelper
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.utils.block.BlockUtils.getBlock

class AACHop3313 : SpeedMode("AACHop3.3.13") {
    override fun onUpdate() {
        if (!MovementUtils.isMoving() || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isOnLadder || mc.thePlayer.isRiding || mc.thePlayer.hurtTime > 0) return

        when {
            (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) -> {
                // MotionXYZ
                val yawRad = mc.thePlayer.rotationYaw * 0.017453292f
                mc.thePlayer.motionX -= (MathHelper.sin(yawRad) * 0.202f).toDouble()
                mc.thePlayer.motionZ += (MathHelper.cos(yawRad) * 0.202f).toDouble()
                mc.thePlayer.motionY = 0.405
                MovementUtils.strafe()
            }

            mc.thePlayer.fallDistance < 0.31f -> {
                if (getBlock(mc.thePlayer.position) is BlockCarpet) { // why?
                    return
                }

                // Motion XZ
                mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.moveStrafing == 0f) 0.027f else 0.021f
                mc.thePlayer.motionX *= 1.001
                mc.thePlayer.motionZ *= 1.001

                // Motion Y
                if (!mc.thePlayer.isCollidedHorizontally) mc.thePlayer.motionY -= 0.014999993
            }

            else -> mc.thePlayer.jumpMovementFactor = 0.02f
        }
    }

    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}