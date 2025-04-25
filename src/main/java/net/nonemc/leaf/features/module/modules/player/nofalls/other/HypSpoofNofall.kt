package net.nonemc.leaf.features.module.modules.player.nofalls.other

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.nonemc.leaf.libs.packet.PacketLib

class HypSpoofNofall : NoFallMode("HypSpoof") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) PacketLib.sendPacketNoEvent(
            C03PacketPlayer.C04PacketPlayerPosition(
                event.packet.x,
                event.packet.y,
                event.packet.z,
                true
            )
        )
    }
}