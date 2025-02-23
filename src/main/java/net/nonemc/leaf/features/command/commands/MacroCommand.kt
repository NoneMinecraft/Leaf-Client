package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.macro.Macro
import net.nonemc.leaf.utils.misc.StringUtils
import org.lwjgl.input.Keyboard

class MacroCommand : Command("macro", arrayOf("m")) {
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val arg1 = args[1]
            when (arg1.lowercase()) {
                "add" -> {
                    if (args.size > 3) {
                        val key = Keyboard.getKeyIndex(args[2].uppercase())
                        if (key != Keyboard.KEY_NONE) {
                            var comm = StringUtils.toCompleteString(args, 3)
                            if (!comm.startsWith(".")) comm = ".$comm"
                            Leaf.macroManager.macros.add(Macro(key, comm))
                            alert("Bound macro $comm to key ${Keyboard.getKeyName(key)}.")
                        } else {
                            alert("Unknown key to bind macro.")
                        }
                        save()
                    } else {
                        chatSyntax("macro add <key> <macro>")
                    }
                }

                "remove" -> {
                    if (args.size > 2) {
                        if (args[2].startsWith(".")) {
                            Leaf.macroManager.macros.filter { it.command == StringUtils.toCompleteString(args, 2) }
                        } else {
                            val key = Keyboard.getKeyIndex(args[2].uppercase())
                            Leaf.macroManager.macros.filter { it.key == key }
                        }.forEach {
                            Leaf.macroManager.macros.remove(it)
                            alert("Remove macro ${it.command}.")
                        }
                        save()
                    } else {
                        chatSyntax("macro remove <macro/key>")
                    }
                }

                "list" -> {
                    alert("Macros:")
                    Leaf.macroManager.macros.forEach {
                        alert("key=${Keyboard.getKeyName(it.key)}, command=${it.command}")
                    }
                }

                else -> chatSyntax("macro <add/remove/list>")
            }
            return
        }
        chatSyntax("macro <add/remove/list>")
    }

    private fun save() {
        Leaf.configManager.smartSave()
        playEdit()
    }
}