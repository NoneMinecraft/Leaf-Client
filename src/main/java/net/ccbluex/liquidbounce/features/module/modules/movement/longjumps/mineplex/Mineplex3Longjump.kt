package net.ccbluex.liquidbounce.features.module.modules.movement.longjumps.mineplex

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.longjumps.LongJumpMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class Mineplex3Longjump : LongJumpMode("Mineplex3") {
    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.fallDistance != 0.0f) {
            mc.thePlayer.motionY += 0.037
        }
    }
    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.jumpMovementFactor = 0.09f
        mc.thePlayer.motionY += 0.0132099999999999999999999999999
        mc.thePlayer.jumpMovementFactor = 0.08f
        MovementUtils.strafe()
    }
}