﻿/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.event.MoveEvent
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class AACHop438 : SpeedMode("AACHop4.3.8") {
    override fun onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        mc.timer.timerSpeed = 1.0f

        if (!EntityMoveLib.isMoving() || thePlayer.isInWater || thePlayer.isInLava ||
            thePlayer.isOnLadder || thePlayer.isRiding
        ) return

        if (thePlayer.onGround)
            thePlayer.jump()
        else {
            if (thePlayer.fallDistance <= 0.1)
                mc.timer.timerSpeed = 1.5f
            else if (thePlayer.fallDistance < 1.3)
                mc.timer.timerSpeed = 0.6f
            else
                mc.timer.timerSpeed = 1f
        }
    }

    override fun onMove(event: MoveEvent) {}
}
