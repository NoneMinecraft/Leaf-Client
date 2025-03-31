package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.event.EventState
import net.nonemc.leaf.event.MotionEvent
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils

class AACHop350 : SpeedMode("AACHop3.5.0") {
    override fun onMotion(event: MotionEvent) {
        if (event.eventState === EventState.POST && MovementUtils.isMoving() && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava) {
            mc.thePlayer.jumpMovementFactor += 0.00208f
            if (mc.thePlayer.fallDistance <= 1f) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    mc.thePlayer.motionX *= 1.0118
                    mc.thePlayer.motionZ *= 1.0118
                } else {
                    mc.thePlayer.motionY -= 0.0147
                    mc.thePlayer.motionX *= 1.00138
                    mc.thePlayer.motionZ *= 1.00138
                }
            }
        }
    }

    override fun onEnable() {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = mc.thePlayer.motionZ
        }
    }

    override fun onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}