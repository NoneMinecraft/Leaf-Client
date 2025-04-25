package net.nonemc.leaf.features.command

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.Leaf.displayAlert
import net.nonemc.leaf.Leaf.displayChatMessage
import net.nonemc.leaf.libs.base.MinecraftInstance

abstract class Command(val command: String, val alias: Array<String>) : MinecraftInstance() {
    abstract fun execute(args: Array<String>)
    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }
    protected fun alert(msg: String) = displayAlert(msg)
    protected fun chat(msg: String) = displayChatMessage(msg)
    protected fun chatSyntax(syntax: String) =
        displayAlert("Syntax: §7${Leaf.commandManager.prefix}$syntax")
    protected fun chatSyntax(syntaxes: Array<String>) {
        displayAlert("Syntax:")

        for (syntax in syntaxes)
            displayChatMessage("§8> §7${Leaf.commandManager.prefix}$command ${syntax.lowercase()}")
    }
    protected fun chatSyntaxError() = displayAlert("Syntax error")
    protected fun playEdit() =
        mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.anvil_use"), 1F))
}