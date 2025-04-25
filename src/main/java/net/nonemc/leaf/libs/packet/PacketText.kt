package net.nonemc.leaf.libs.packet

import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ChatComponentText
import net.nonemc.leaf.libs.base.mc

object PacketText {
    fun chat(message: String) {
        net.nonemc.leaf.libs.base.mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(message))
    }
    fun tell(player: String, message: String) {
        net.nonemc.leaf.libs.base.mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage("/tell $player $message"))
    }
    fun chatPrint(message: String) {
        net.nonemc.leaf.libs.base.mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }
}
