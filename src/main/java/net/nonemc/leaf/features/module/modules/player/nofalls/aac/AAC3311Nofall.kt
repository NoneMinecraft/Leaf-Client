﻿package net.nonemc.leaf.features.module.modules.player.nofalls.aac

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class AAC3311Nofall : NoFallMode("AAC3.3.11") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 2) {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = mc.thePlayer.motionZ
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 10E-4,
                    mc.thePlayer.posZ,
                    mc.thePlayer.onGround
                )
            )
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
        }
    }
}