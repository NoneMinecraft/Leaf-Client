/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.features.command.Command

class PingCommand : Command("ping", emptyArray()) {
    override fun execute(args: Array<String>) {
        alert("§3Your ping is §a${mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime}ms§3.")
    }
}