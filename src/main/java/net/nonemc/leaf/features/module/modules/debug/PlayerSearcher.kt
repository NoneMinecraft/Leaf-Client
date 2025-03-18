package net.nonemc.leaf.features.module.modules.debug

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.TextValue
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S3APacketTabComplete

//Only KKC
@ModuleInfo(name = "PlayerSearcher", category = ModuleCategory.DEBUG)
class PlayerSearcher : Module() {
    private val targetPlayer = TextValue("TargetPlayerName","N0ne")
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