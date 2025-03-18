
package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command

class PrefixCommand : Command("prefix", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("prefix <character>")
            return
        }

        val prefix = args[1]

        if (prefix.length > 1) {
            alert("§cPrefix can only be one character long!")
            return
        }

        Leaf.commandManager.prefix = prefix.single()
        Leaf.fileManager.saveConfig(Leaf.fileManager.specialConfig)

        alert("Successfully changed command prefix to '§8$prefix§3'")
    }
}