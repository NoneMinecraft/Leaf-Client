package net.nonemc.leaf.features.module.modules.player.nofalls.packet

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode

class Packet2Nofall : NoFallMode("Packet2") {
    private var packet1Count = 0
    private var packetModify = false
    override fun onEnable() {
        packet1Count = 0
        packetModify = false
    }

    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance.toInt() / 2 > packet1Count) {
            packet1Count = mc.thePlayer.fallDistance.toInt() / 2
            packetModify = true
        }
        if (mc.thePlayer.onGround) {
            packet1Count = 0
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) {
            if (packetModify) {
                event.packet.onGround = true
                packetModify = false
            }
        }
    }
}