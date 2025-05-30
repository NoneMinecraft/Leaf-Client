﻿package net.nonemc.leaf.features.module.modules.player.phases.aac

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode

class AACv4Phase : PhaseMode("AACv4") {
    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.sendQueue.addToSendQueue(
            C03PacketPlayer.C06PacketPlayerPosLook(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 0.00000001,
                mc.thePlayer.posZ,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                false
            )
        )
        mc.thePlayer.sendQueue.addToSendQueue(
            C03PacketPlayer.C06PacketPlayerPosLook(
                mc.thePlayer.posX,
                mc.thePlayer.posY - 1,
                mc.thePlayer.posZ,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                false
            )
        )
        phase.state = false
    }
}