﻿package net.nonemc.leaf.features.module.modules.player.nofalls.aac

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class AAC3315Nofall : NoFallMode("AAC3.3.15") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 2) {
            if (!mc.isIntegratedServerRunning) {
                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        Double.NaN,
                        mc.thePlayer.posZ,
                        false
                    )
                )
            }
            mc.thePlayer.fallDistance = -9999f
        }
    }
}