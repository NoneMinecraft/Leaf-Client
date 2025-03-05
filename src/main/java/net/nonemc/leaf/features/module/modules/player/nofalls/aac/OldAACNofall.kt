package net.nonemc.leaf.features.module.modules.player.nofalls.aac

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class OldAACNofall : NoFallMode("OldAAC") {
    private var oldaacState = 0
    override fun onEnable() {
        oldaacState = 0
    }

    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 2f) {
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            oldaacState = 2
        } else if (oldaacState == 2 && mc.thePlayer.fallDistance < 2) {
            mc.thePlayer.motionY = 0.1
            oldaacState = 3
            return
        }
        when (oldaacState) {
            3 -> {
                mc.thePlayer.motionY = 0.1
                oldaacState = 4
            }

            4 -> {
                mc.thePlayer.motionY = 0.1
                oldaacState = 5
            }

            5 -> {
                mc.thePlayer.motionY = 0.1
                oldaacState = 1
            }
        }
    }
}