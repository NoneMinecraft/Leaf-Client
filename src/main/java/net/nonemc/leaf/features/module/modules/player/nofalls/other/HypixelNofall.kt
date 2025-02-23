package net.nonemc.leaf.features.module.modules.player.nofalls.other

import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

class HypixelNofall : NoFallMode("Hypixel") {
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && mc.thePlayer != null && mc.thePlayer.fallDistance > 1.5) {
            event.packet.onGround = mc.thePlayer.ticksExisted % 2 == 0
        }
    }
}