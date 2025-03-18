package net.nonemc.leaf.features.macro

import net.nonemc.leaf.Leaf

class Macro(val key: Int, val command: String) {
    fun exec() {
        Leaf.commandManager.executeCommands(command)
    }
}