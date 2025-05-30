﻿package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.Leaf.displayChatMessage
import net.nonemc.leaf.features.command.Command
import org.lwjgl.input.Keyboard

class BindsCommand : Command("binds", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            if (args[1].equals("clear", true)) {
                for (module in Leaf.moduleManager.modules)
                    module.keyBind = Keyboard.KEY_NONE

                alert("Removed all binds.")
                return
            }
        }

        alert("§c§lBinds")
        Leaf.moduleManager.modules.filter { it.keyBind != Keyboard.KEY_NONE }.forEach {
            displayChatMessage("§6> §c${it.name}: §a§l${Keyboard.getKeyName(it.keyBind)}")
        }
        chatSyntax("binds clear")
    }
}
