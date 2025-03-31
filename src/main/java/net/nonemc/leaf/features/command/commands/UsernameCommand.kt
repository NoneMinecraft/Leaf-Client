package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.features.command.Command

class UsernameCommand : Command("username", arrayOf("name")) {
    override fun execute(args: Array<String>) {
        alert("Username: " + mc.thePlayer.name)
    }
}