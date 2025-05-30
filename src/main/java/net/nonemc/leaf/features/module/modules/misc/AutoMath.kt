﻿//Pit Bot By N0ne
package net.nonemc.leaf.features.module.modules.misc

import net.minecraft.network.play.server.S45PacketTitle
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.libs.packet.PacketText
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "AutoMath", category = ModuleCategory.MISC)
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
                PacketText.chat(result.toString())
            }
        }
    }
}