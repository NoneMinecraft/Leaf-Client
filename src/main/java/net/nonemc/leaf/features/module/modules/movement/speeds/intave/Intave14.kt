package net.nonemc.leaf.features.module.modules.movement.speeds.intave

import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.minecraft.network.play.client.C03PacketPlayer

class Intave14 : SpeedMode("Intave14") {
    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && !(event.packet is C03PacketPlayer.C04PacketPlayerPosition || event.packet is C03PacketPlayer.C05PacketPlayerLook || event.packet is C03PacketPlayer.C06PacketPlayerPosLook)) {
            event.cancelEvent()
        }
    }

    override fun onUpdate() {
        if (!mc.thePlayer.onGround){
            mc.timer.timerSpeed = 1.343F
        }else{
            mc.timer.timerSpeed = 0.765F
            mc.thePlayer.motionY = 0.42
        }
    }
}