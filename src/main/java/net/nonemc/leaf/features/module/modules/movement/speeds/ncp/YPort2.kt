/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.nonemc.leaf.features.module.modules.movement.speeds.ncp

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils

class YPort2 : SpeedMode("YPort2") {
    override fun onPreMotion() {
        if (mc.thePlayer.isOnLadder || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !MovementUtils.isMoving()) return

        if (mc.thePlayer.onGround) mc.thePlayer.jump() else mc.thePlayer.motionY = -1.0

        MovementUtils.strafe()
    }
}