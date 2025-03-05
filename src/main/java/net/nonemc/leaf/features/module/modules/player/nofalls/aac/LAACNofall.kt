package net.nonemc.leaf.features.module.modules.player.nofalls.aac

import net.nonemc.leaf.event.JumpEvent
import net.nonemc.leaf.event.MoveEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class LAACNofall : NoFallMode("LAAC") {
    private var jumped = false
    override fun onEnable() {
        jumped = false
    }

    override fun onNoFall(event: UpdateEvent) {
        if (!jumped && mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInWeb) {
            mc.thePlayer.motionY = -6.0
        }
    }

    override fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.onGround) {
            jumped = false
        }

        if (mc.thePlayer.motionY > 0) {
            jumped = true
        }
    }

    override fun onMove(event: MoveEvent) {
        if (!jumped && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInWeb && mc.thePlayer.motionY < 0.0) {
            event.x = 0.0
            event.z = 0.0
        }
    }

    override fun onJump(event: JumpEvent) {
        jumped = true
    }
}