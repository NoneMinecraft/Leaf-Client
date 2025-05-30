﻿package net.nonemc.leaf.features.module.modules.player.phases.aac

import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.MathHelper
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.block.BlockLib
import net.nonemc.leaf.libs.timer.TickTimer
import kotlin.math.cos
import kotlin.math.sin

class AAC350Phase : PhaseMode("AAC3.5.0") {
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

        if (!tickTimer.hasTimePassed(2) || !mc.thePlayer.isCollidedHorizontally || !(!isInsideBlock || mc.thePlayer.isSneaking)) return
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val oldX = mc.thePlayer.posX
        val oldZ = mc.thePlayer.posZ
        val x = -sin(yaw)
        val z = cos(yaw)
        mc.thePlayer.setPosition(oldX + x, mc.thePlayer.posY, oldZ + z)
        tickTimer.reset()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            val yaw = EntityMoveLib.direction.toFloat()
            packet.x = packet.x - MathHelper.sin(yaw) * 0.00000001
            packet.z = packet.z + MathHelper.cos(yaw) * 0.00000001
        }
    }
}