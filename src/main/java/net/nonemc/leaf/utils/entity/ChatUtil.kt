package net.nonemc.leaf.utils.entity

import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ChatComponentText
import net.nonemc.leaf.utils.mc

object ChatUtil {
    fun Chat(message: String) {
        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(message))
    }

    fun tell(player: String, message: String) {
        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage("/tell $player $message"))
    }

    fun ChatPrint(message: String) {
        mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }

}
