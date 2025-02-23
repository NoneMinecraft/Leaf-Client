package net.nonemc.leaf.features.module.modules.movement.longjumps.ncp

import net.nonemc.leaf.event.JumpEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.movement.longjumps.LongJumpMode
import net.nonemc.leaf.utils.MovementUtils
import net.nonemc.leaf.value.FloatValue

class NCPLongjump : LongJumpMode("NCP") {
    private val ncpBoostValue = FloatValue("${valuePrefix}Boost", 4.25f, 1f, 10f)
    private var canBoost = false
    override fun onEnable() {
        canBoost = true
    }
    override fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
        MovementUtils.strafe(MovementUtils.getSpeed() * if (canBoost) ncpBoostValue.get() else 1f)
        if(canBoost) canBoost = false
    }

    override fun onJump(event: JumpEvent) {
        canBoost = true
    }
}