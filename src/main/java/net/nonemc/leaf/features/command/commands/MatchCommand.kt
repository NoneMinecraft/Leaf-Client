package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command

class MatchCommand : Command("match", arrayOf("match")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 3) {
            val module = Leaf.moduleManager.getModule(args[0])
            val module2 = Leaf.moduleManager.getModule(args[1])

            if (module == null) {
                alert("Module '${args[0]}' not found.")
                return
            }

            if (module2 == null) {
                alert("Module '${args[1]}' not found.")
                return
            }

            if (module == module2) {
                alert("Must match ${args[0]} to ANOTHER module.")
                return
            }

            module.state = module2.state

            alert("${if (module.state) "Enabled" else "Disabled"} module §8${module.name}§3 to match module §8${module2.name}§3.")
            return
        }

        chatSyntax("match <module> <module>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> Leaf.moduleManager.modules
                .map { it.name }
                .filter { it.startsWith(moduleName, true) }
                .toList()

            2 -> Leaf.moduleManager.modules
                .map { it.name }
                .filter { it.startsWith(args[1], true) }
                .toList()

            else -> emptyList()
        }
    }
}
