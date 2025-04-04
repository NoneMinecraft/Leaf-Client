﻿package net.nonemc.leaf.features.command

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.Leaf.displayAlert
import net.nonemc.leaf.Leaf.displayChatMessage
import net.nonemc.leaf.utils.MinecraftInstance

abstract class Command(val command: String, val alias: Array<String>) : MinecraftInstance() {
    /**
     * Execute commands with provided [args]
     */
    abstract fun execute(args: Array<String>)

    /**
     * Returns a list of command completions based on the provided [args].
     * If a command does not implement [tabComplete] an [EmptyList] is returned by default.
     *
     * @param args an array of command arguments that the entity has passed to the command so far
     * @return a list of matching completions for the command the entity is trying to autocomplete
     * @author NurMarvin
     */
    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }

    /**
     * Print [msg] to chat as alert
     */
    protected fun alert(msg: String) = displayAlert(msg)

    /**
     * Print [msg] to chat as plain text
     */
    protected fun chat(msg: String) = displayChatMessage(msg)

    /**
     * Print [syntax] of command to chat
     */
    protected fun chatSyntax(syntax: String) =
        displayAlert("Syntax: §7${Leaf.commandManager.prefix}$syntax")

    /**
     * Print [syntaxes] of command to chat
     */
    protected fun chatSyntax(syntaxes: Array<String>) {
        displayAlert("Syntax:")

        for (syntax in syntaxes)
            displayChatMessage("§8> §7${Leaf.commandManager.prefix}$command ${syntax.lowercase()}")
    }

    /**
     * Print a syntax error to chat
     */
    protected fun chatSyntaxError() = displayAlert("Syntax error")

    /**
     * Play edit sound
     */
    protected fun playEdit() =
        mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.anvil_use"), 1F))
}