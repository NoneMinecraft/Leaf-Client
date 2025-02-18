package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S3APacketTabComplete

//Only KKC
@ModuleInfo(name = "PlayerSearcher", category = ModuleCategory.COMBAT)
class PlayerSearcher : Module() {
    private val targetPlayer = TextValue("TargetPlayerName","Xinslone")
    override fun onEnable() {
        if (mc.thePlayer == null) return
        mc.netHandler.addToSendQueue(C14PacketTabComplete("/report "))
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S3APacketTabComplete){
            val messages = packet.func_149630_c()
            if (messages.contains(targetPlayer.get())){
                ChatPrint("§0[§cPlayerSearcher§0] §7TargetPlayer:[${targetPlayer.get()}] is online.")
            }else{
                ChatPrint("§0[§cPlayerSearcher§0] §7TargetPlayer:[${targetPlayer.get()}] is offline.")
            }
        }
    }
}