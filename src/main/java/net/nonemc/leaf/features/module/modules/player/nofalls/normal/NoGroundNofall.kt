package net.nonemc.leaf.features.module.modules.player.nofalls.normal

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class NoGroundNofall : NoFallMode("NoGround") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) event.packet.onGround = false
    }
}