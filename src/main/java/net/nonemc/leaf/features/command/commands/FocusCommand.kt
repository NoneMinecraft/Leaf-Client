/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command

class FocusCommand : Command("focus", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 3) {
            val focused = args[1]
            val target = args[2]
            val entity = mc.theWorld.playerEntities.filter { it.name.equals(target, true) && !it.equals(mc.thePlayer) }.also {
                if (it.isEmpty()) {
                    alert("§6Couldn't find anyone named §a${target.lowercase()}§6 in the world.")
                    return
                }
            }[0]

            when (focused.lowercase()) {
                "add" -> {
                    Leaf.combatManager.focusedPlayerList.add(entity)
                    alert("Successfully added §a${target.lowercase()}§3 into the focus list.")
                    return
                }
                "remove" -> {
                    if (Leaf.combatManager.focusedPlayerList.contains(entity)) {
                        Leaf.combatManager.focusedPlayerList.remove(entity)
                        alert("Successfully removed §a${target.lowercase()}§3 from the focus list.")
                        return
                    } else {
                        alert("§6Couldn't find anyone named §a${target.lowercase()}§6 in the focus list.")
                        return
                    }
                }
            }
        } else if (args.size == 2 && args[1].equals("clear", true)) {
            Leaf.combatManager.focusedPlayerList.clear()
            alert("Successfully cleared the focus list.")
            return
        }

        chatSyntax("focus <clear/add/remove> <target name>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val pref = args[0]

        return when (args.size) {
            1 -> listOf("clear", "add", "remove")
            2 -> if (args[0].equals("add", true) || args[0].equals("remove", true)) {
                    mc.theWorld.playerEntities
                        .filter { it.name.startsWith(pref, true) }
                        .map { it.name }
                        .toList()
            } else emptyList()
            else -> emptyList()
        }
    }
}
