package net.nonemc.leaf.features.module.modules.player.nofalls.other

import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

class CubeCraftNofall : NoFallMode("CubeCraft") {
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 2f) {
            mc.thePlayer.onGround = false
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
        }
    }
}