package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction

@ModuleInfo(name = "PacketSpoof", category = ModuleCategory.COMBAT)
class PacketSpoof : Module() {
    private val aac5 = BoolValue("AAC5Combat",false)
    private val intaveTimer = BoolValue("IntaveTimer",false)
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C0FPacketConfirmTransaction && aac5.get()){
            if (event.packet.uid != 2147483647.toShort()){
                event.cancelEvent()
                mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(2147483647,event.packet.getUid(),false))
            }
        }
        if (event.packet is C0FPacketConfirmTransaction && intaveTimer.get()){
            event.cancelEvent()
        }
        if (event.packet is C03PacketPlayer && !(event.packet is C03PacketPlayer.C04PacketPlayerPosition || event.packet is C03PacketPlayer.C05PacketPlayerLook || event.packet is C03PacketPlayer.C06PacketPlayerPosLook) && intaveTimer.get()) {
            event.cancelEvent()
        }

    }
}