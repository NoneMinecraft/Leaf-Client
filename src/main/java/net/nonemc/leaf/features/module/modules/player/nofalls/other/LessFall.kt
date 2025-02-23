package net.nonemc.leaf.features.module.modules.player.nofalls.other

import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.minecraft.network.play.client.C03PacketPlayer

class LessFall : NoFallMode("LessFall") {
    private var needSpoof = false
    override fun onEnable() {
        needSpoof = false
    }
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance > 3.5) {
            needSpoof = true
            mc.thePlayer.fallDistance = 4.5f
        }
    }
    override fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && needSpoof) {
            event.packet.onGround = true
            needSpoof = false
        }
    }
}
