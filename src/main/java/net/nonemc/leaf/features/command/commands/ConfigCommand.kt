﻿package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.file.configsDir
import java.io.File
import java.nio.file.Files

class ConfigCommand : Command("config", arrayOf("cfg")) {
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when (args[1].lowercase()) {
                "create" -> {
                    if (args.size > 2) {
                        val file = File(configsDir, "${args[2]}.json")
                        if (!file.exists()) {
                            Leaf.configManager.load(args[2], true)
                            alert("Created config ${args[2]}")
                        } else {
                            alert("Config ${args[2]} already exists")
                        }
                    } else {
                        chatSyntax("${args[1]} <configName>")
                    }
                }

                "load", "forceload" -> {
                    if (args.size > 2) {
                        val file = File(configsDir, "${args[2]}.json")
                        if (file.exists()) {
                            Leaf.configManager.load(args[2], args[1].equals("load", true))
                            alert("Loaded config ${args[2]}")
                        } else {
                            alert("Config ${args[2]} does not exist")
                        }
                    } else {
                        chatSyntax("${args[1]} <configName>")
                    }
                }

                "delete" -> {
                    if (args.size > 2) {
                        val file = File(configsDir, "${args[2]}.json")
                        if (file.exists()) {
                            file.delete()
                            alert("Successfully deleted config ${args[2]}")
                        } else {
                            alert("Config ${args[2]} does not exist")
                        }
                    } else {
                        chatSyntax("${args[1]} <configName>")
                    }
                }

                "list" -> {
                    val list = (configsDir.listFiles() ?: return)
                        .filter { it.isFile }
                        .map {
                            val name = it.name
                            if (name.endsWith(".json")) {
                                name.substring(0, name.length - 5)
                            } else {
                                name
                            }
                        }

                    alert("Configs(${list.size}):")

                    for (file in list) {
                        if (file.equals(Leaf.configManager.nowConfig)) {
                            alert("-> §a§l$file")
                        } else {
                            alert("> $file")
                        }
                    }
                }

                "save" -> {
                    Leaf.configManager.save(true, true)
                    alert("Saved config ${Leaf.configManager.nowConfig}")
                }

                "reload" -> {
                    Leaf.configManager.load(Leaf.configManager.nowConfig, false)
                    alert("Reloaded config ${Leaf.configManager.nowConfig}")
                }

                "rename" -> {
                    if (args.size > 3) {
                        val file = File(configsDir, "${args[2]}.json")
                        val newFile = File(configsDir, "${args[3]}.json")
                        if (file.exists() && !newFile.exists()) {
                            file.renameTo(newFile)
                            alert("Renamed config ${args[2]} to ${args[3]}")
                        } else if (!file.exists()) {
                            alert("Config ${args[2]} does not exist")
                        } else if (newFile.exists()) {
                            alert("Config ${args[3]} already exists")
                        }
                        if (Leaf.configManager.nowConfig.equals(args[2], true)) {
                            Leaf.configManager.load(args[3], false)
                            Leaf.configManager.saveConfigSet()
                        }
                    } else {
                        chatSyntax("${args[1]} <configName> <newName>")
                    }
                }

                "current" -> {
                    alert("Current config is ${Leaf.configManager.nowConfig}")
                }

                "copy" -> {
                    if (args.size > 3) {
                        val file = File(configsDir, "${args[2]}.json")
                        val newFile = File(configsDir, "${args[3]}.json")
                        if (file.exists() && !newFile.exists()) {
                            Files.copy(file.toPath(), newFile.toPath())
                            alert("Copied config ${args[2]} to ${args[3]}")
                        } else if (!file.exists()) {
                            alert("Config ${args[2]} does not exist")
                        } else if (newFile.exists()) {
                            alert("Config ${args[3]} already exists")
                        }
                    } else {
                        chatSyntax("${args[1]} <configName> <newName>")
                    }
                }
            }
        } else {
            chatSyntax(
                arrayOf(
                    "current",
                    "copy <configName> <newName>",
                    "create <configName>",
                    "load <configName>",
                    "forceload <configName>",
                    "delete <configName>",
                    "rename <configName> <newName>",
                    "reload",
                    "list",
                    "save"
                )
            )
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf(
                "current",
                "copy",
                "create",
                "load",
                "forceload",
                "delete",
                "rename",
                "reload",
                "list",
                "save"
            ).filter { it.startsWith(args[0], true) }

            2 -> when (args[0].lowercase()) {
                "delete", "load", "forceload", "rename", "copy" -> {
                    (configsDir.listFiles() ?: return emptyList())
                        .filter { it.isFile }
                        .map {
                            val name = it.name
                            if (name.endsWith(".json")) {
                                name.substring(0, name.length - 5)
                            } else {
                                name
                            }
                        }
                        .filter { it.startsWith(args[1], true) }
                }

                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}
