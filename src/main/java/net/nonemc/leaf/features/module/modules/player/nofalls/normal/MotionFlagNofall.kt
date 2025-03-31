package net.nonemc.leaf.features.module.modules.player.nofalls.normal

import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.nonemc.leaf.value.FloatValue

class MotionFlagNofall : NoFallMode("MotionFlag") {
    private val flySpeedValue = FloatValue("${valuePrefix}MotionSpeed", -0.01f, -5f, 5f)
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3) {
            mc.thePlayer.motionY = flySpeedValue.get().toDouble()
        }
    }
}