﻿package net.nonemc.leaf.features.module.modules.player.phases.spartan

import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.block.BlockLib
import net.nonemc.leaf.libs.timer.TickTimer
import kotlin.math.cos
import kotlin.math.sin

class SpartanPhase : PhaseMode("Spartan") {
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
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(0.5, 0.0, 0.5, true))
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 0.2,
                mc.thePlayer.posZ,
                true
            )
        )
        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(0.5, 0.0, 0.5, true))
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX + 0.5,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + 0.5,
                true
            )
        )
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val x = -sin(yaw) * 0.04
        val z = cos(yaw) * 0.04
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z)
        tickTimer.reset()
    }
}