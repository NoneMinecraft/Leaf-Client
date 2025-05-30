﻿package net.nonemc.leaf.features.module.modules.player.phases.vanilla

import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.block.BlockLib
import net.nonemc.leaf.libs.timer.TickTimer
import kotlin.math.cos
import kotlin.math.sin

class SkipPhase : PhaseMode("Skip") {
    private val tickTimer = TickTimer()
    override fun onEnable() {
        tickTimer.reset()
    }

    override fun onUpdate(event: UpdateEvent) {
        val isInsideBlock =
            BlockLib.collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
        if (isInsideBlock) {
            mc.thePlayer.noClip = true
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.onGround = true
        }
        tickTimer.update()

        if (!mc.thePlayer.onGround || !tickTimer.hasTimePassed(2) || !mc.thePlayer.isCollidedHorizontally || !(!isInsideBlock || mc.thePlayer.isSneaking)) return
        val direction = EntityMoveLib.direction
        val posX = -sin(direction) * 0.3
        val posZ = cos(direction) * 0.3
        var i = 0
        while (i < 3) {
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.06,
                    mc.thePlayer.posZ,
                    true
                )
            )
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX + posX * i,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ + posZ * i,
                    true
                )
            )
            ++i
        }
        mc.thePlayer.entityBoundingBox = mc.thePlayer.entityBoundingBox.offset(posX, 0.0, posZ)
        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX + posX, mc.thePlayer.posY, mc.thePlayer.posZ + posZ)
        tickTimer.reset()
    }
}