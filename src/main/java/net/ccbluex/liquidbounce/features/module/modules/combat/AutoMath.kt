package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "AutoMath", category = ModuleCategory.COMBAT)
class AutoMath : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S45PacketTitle) {
            val titlePacket = event.packet
            val message = titlePacket.message.unformattedText
                val regex = Regex("""\s*(\d+)\s*([+-])\s*(\d+)\s*""")
                val matchResult = regex.matchEntire(message)

                if (matchResult != null) {
                    val (num1, operator, num2) = matchResult.destructured
                    val result = when (operator) {
                        "+" -> num1.toInt() + num2.toInt()
                        "-" -> num1.toInt() - num2.toInt()
                        else -> error("null")
                    }
                    MainLib.Chat(result.toString())
                }
            }
        }
}