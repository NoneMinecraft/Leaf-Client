﻿package net.nonemc.leaf.features.command.commands

import joptsimple.internal.Strings
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.Leaf.displayChatMessage
import net.nonemc.leaf.features.command.Command

class HelpCommand : Command("help", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        var page = 1

        if (args.size > 1) {
            try {
                page = args[1].toInt()
            } catch (e: NumberFormatException) {
                chatSyntaxError()
            }
        }

        if (page <= 0) {
            alert("The number you have entered is too low, it must be over 0")
            return
        }

        val maxPageDouble = Leaf.commandManager.commands.size / 8.0
        val maxPage = if (maxPageDouble > maxPageDouble.toInt()) {
            maxPageDouble.toInt() + 1
        } else {
            maxPageDouble.toInt()
        }

        if (page > maxPage) {
            alert("The number you have entered is too big, it must be under $maxPage.")
            return
        }

        alert("§c§lHelp")
        displayChatMessage("§7> Page: §8$page / $maxPage")

        val commands = Leaf.commandManager.commands.map { it.value }.distinct().sortedBy { it.command }

        var i = 8 * (page - 1)
        while (i < 8 * page && i < commands.size) {
            val command = commands[i]

            displayChatMessage(
                "§6> §7${Leaf.commandManager.prefix}${command.command}${
                    if (command.alias.isEmpty()) "" else " §7(§8" + Strings.join(
                        command.alias,
                        "§7, §8"
                    ) + "§7)"
                }"
            )
            i++
        }

        displayChatMessage("§a------------\n§7> §c${Leaf.commandManager.prefix}help §8<§7§lpage§8>")
    }
}
