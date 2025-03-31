package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command

class ToggleCommand : Command("toggle", arrayOf("t")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val module = Leaf.moduleManager.getModule(args[1])

            if (module == null) {
                alert("Module '${args[1]}' not found.")
                return
            }

            if (args.size > 2) {
                val newState = args[2].lowercase()

                if (newState == "on" || newState == "off") {
                    module.state = newState == "on"
                } else {
                    chatSyntax("toggle <module> [on/off]")
                    return
                }
            } else {
                module.toggle()
            }

            alert("${if (module.state) "Enabled" else "Disabled"} module §8${module.name}§3.")
            return
        }

        chatSyntax("toggle <module> [on/off]")
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
