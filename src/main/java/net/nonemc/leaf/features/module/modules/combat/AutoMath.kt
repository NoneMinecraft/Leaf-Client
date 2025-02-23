//Pit Bot By N0ne
package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.MainLib
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
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