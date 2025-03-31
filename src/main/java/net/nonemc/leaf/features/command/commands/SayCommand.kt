package net.nonemc.leaf.features.command.commands

import net.minecraft.network.play.client.C01PacketChatMessage
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.utils.packet.PacketUtils
import net.nonemc.leaf.utils.misc.StringUtils

class SayCommand : Command("say", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val str = StringUtils.toCompleteString(args, 1)
            PacketUtils.sendPacketNoEvent(C01PacketChatMessage(str.substring(0, str.length.coerceAtMost(100))))
            alert("Message was sent to the chat.")
            return
        }
        chatSyntax("say <message...>")
    }
}