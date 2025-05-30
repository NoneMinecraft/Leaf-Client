﻿package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.ui.hud.element.elements.Notification
import net.nonemc.leaf.ui.hud.element.elements.NotifyType
import org.lwjgl.input.Keyboard

class BindCommand : Command("bind", emptyArray()) {
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            // Get module by name
            val module = Leaf.moduleManager.getModule(args[1])

            if (module == null) {
                alert("Module §a§l" + args[1] + "§3 not found.")
                return
            }

            if (args.size > 2) {
                // Find key by name and change
                val key = Keyboard.getKeyIndex(args[2].uppercase())
                module.keyBind = key

                // Response to user
                alert("Bound module §a§l${module.name}§3 to key §a§l${Keyboard.getKeyName(key)}§3.")
                Leaf.hud.addNotification(
                    Notification("KeyBind", "Bound ${module.name} to ${Keyboard.getKeyName(key)}.", NotifyType.INFO)
                )
                playEdit()
            } else {
                Leaf.moduleManager.pendingBindModule = module
                alert("Press any key to bind module ${module.name}")
            }
            return
        }

        chatSyntax(arrayOf("<module> <key>", "<module> none"))
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> Leaf.moduleManager.modules
                .map { it.name }
                .filter { it.startsWith(moduleName, true) }
                .toList()

            else -> emptyList()
        }
    }
}
