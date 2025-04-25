package net.nonemc.leaf.features.command

import net.nonemc.leaf.Leaf.displayChatMessage
import net.nonemc.leaf.libs.clazz.ClassReflect

class CommandManager {
    val commands = HashMap<String, Command>()
    var latestAutoComplete: Array<String> = emptyArray()

    var prefix = '.'
    fun registerCommands() {
        ClassReflect.resolvePackage("${this.javaClass.`package`.name}.commands", Command::class.java)
            .forEach(this::registerCommand)
    }
    fun executeCommands(input: String) {
        val args = input.split(" ").toTypedArray()
        val command = commands[args[0].substring(1).lowercase()]

        if (command != null) {
            command.execute(args)
        } else {
            displayChatMessage("§cCommand not found. Type ${prefix}help to view all commands.")
        }
    }
    fun autoComplete(input: String): Boolean {
        this.latestAutoComplete = this.getCompletions(input) ?: emptyArray()
        return input.startsWith(this.prefix) && this.latestAutoComplete.isNotEmpty()
    }

    private fun getCompletions(input: String): Array<String>? {
        if (input.isNotEmpty() && input.toCharArray()[0] == this.prefix) {
            val args = input.split(" ")

            return if (args.size > 1) {
                val command = getCommand(args[0].substring(1))
                val tabCompletions = command?.tabComplete(args.drop(1).toTypedArray())

                tabCompletions?.toTypedArray()
            } else {
                commands.map { ".${it.key}" }.filter { it.lowercase().startsWith(args[0].lowercase()) }.toTypedArray()
            }
        }
        return null
    }
    private fun getCommand(name: String): Command? {
        return commands[name.lowercase()]
    }
    fun registerCommand(command: Command) {
        commands[command.command.lowercase()] = command
        command.alias.forEach {
            commands[it.lowercase()] = command
        }
    }
    private fun registerCommand(commandClass: Class<out Command>) {
        try {
            registerCommand(commandClass.newInstance())
        } catch (e: Throwable) {
            println("Failed to load command: ${commandClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }
    fun unregisterCommand(command: Command) {
        commands.toList().forEach {
            if (it.second == command) {
                commands.remove(it.first)
            }
        }
    }
}
