﻿package net.nonemc.leaf.features.module.modules.player.nofalls.vulcan

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class VulcanNofall2 : NoFallMode("Vulcan2") {
    private var doSpoof = false

    override fun onEnable() {
        doSpoof = false
    }

    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 2.0) {
            mc.timer.timerSpeed = 0.9f
        }
        if (mc.thePlayer.onGround) {
            mc.timer.timerSpeed = 1f
        }

        if (mc.thePlayer.fallDistance > 2.8) {
            doSpoof = true
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.fallDistance = 0f
            mc.thePlayer.motionY = mc.thePlayer.motionY + mc.thePlayer.motionY / 10.0

        }

    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && doSpoof) {
            event.packet.onGround = true
            doSpoof = false
        }
    }
}
