﻿package net.nonemc.leaf.features.module.modules.player.nofalls.vulcan

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class VulcanNofall : NoFallMode("Vulcan") {
    private var vulCanNoFall = false
    private var vulCantNoFall = false
    private var nextSpoof = false
    private var doSpoof = false
    override fun onEnable() {
        vulCanNoFall = false
        vulCantNoFall = false
        nextSpoof = false
        doSpoof = false
    }

    override fun onNoFall(event: UpdateEvent) {
        if (!vulCanNoFall && mc.thePlayer.fallDistance > 3.25) {
            vulCanNoFall = true
        }
        if (vulCanNoFall && mc.thePlayer.onGround && vulCantNoFall) {
            vulCantNoFall = false
        }
        if (vulCantNoFall) return
        if (nextSpoof) {
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.fallDistance = -0.1f
            EntityMoveLib.strafe(0.3f)
            nextSpoof = false
        }
        if (mc.thePlayer.fallDistance > 3.5625f) {
            mc.thePlayer.fallDistance = 0.0f
            doSpoof = true
            nextSpoof = true
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && doSpoof) {
            event.packet.onGround = true
            doSpoof = false
            event.packet.y = Math.round(mc.thePlayer.posY * 2).toDouble() / 2
            mc.thePlayer.setPosition(mc.thePlayer.posX, event.packet.y, mc.thePlayer.posZ)
        }
    }
}