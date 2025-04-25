package net.nonemc.leaf.features.module.modules.player.phases.vanilla

import net.nonemc.leaf.event.MoveEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.timer.TickTimer
import kotlin.math.cos
import kotlin.math.sin

class VanillaPhase : PhaseMode("Vanilla") {
    private var mineplexClip = false
    private val ticktimer = TickTimer()
    override fun onEnable() {
        mineplexClip = false
        ticktimer.reset()
    }

    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isCollidedHorizontally) mineplexClip = true
        if (!mineplexClip) return
        ticktimer.update()
        event.x = 0.0
        event.z = 0.0
        if (ticktimer.hasTimePassed(3)) {
            ticktimer.reset()
            mineplexClip = false
        } else if (ticktimer.hasTimePassed(1)) {
            val offset = if (ticktimer.hasTimePassed(2)) 1.6 else 0.06
            val direction = EntityMoveLib.direction
            mc.thePlayer.setPosition(
                mc.thePlayer.posX + -sin(direction) * offset,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + cos(direction) * offset
            )
        }
    }
}