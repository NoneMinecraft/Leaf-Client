package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.MainLib.tell
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.S02PacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "AutoTell", category = ModuleCategory.PLAYER)
class AutoTell : Module() {
    private val messageValue = TextValue("message","message")

        @EventTarget
        fun onPacket(event: PacketEvent) {
            if (event.packet is S02PacketChat) {
                val chatPacket = event.packet as S02PacketChat
                val message = chatPacket.chatComponent.unformattedText
                val duelPattern = "\\[Duels\\] (\\S+)"
                val defeatedPattern = "defeated (\\S+)"

                val duelMatcher = Pattern.compile(duelPattern).matcher(message)
                val defeatedMatcher = Pattern.compile(defeatedPattern).matcher(message)

                var player: String? = null
                var target: String? = null

                if (duelMatcher.find()) {
                    player = duelMatcher.group(1)
                }

                if (defeatedMatcher.find()) {
                    target = defeatedMatcher.group(1)
                }


                println("Player: $player")
                println("Target: $target")


                if (player != null && player == mc.thePlayer.name) {
                    target?.let { tell(it,messageValue.get()) }
                    ChatPrint("tell..")
                }
            }
        }
    }
