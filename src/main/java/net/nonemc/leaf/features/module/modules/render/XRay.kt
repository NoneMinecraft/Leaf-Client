﻿package net.nonemc.leaf.features.module.modules.render

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.file.saveConfig
import net.nonemc.leaf.file.xrayConfig

@ModuleInfo(
    name = "XRay",
    category = ModuleCategory.RENDER,
    autoDisable = EnumAutoDisableType.RESPAWN,
    moduleCommand = false
)
class XRay : Module() {
    val xrayBlocks = mutableListOf<Block>(
        Blocks.bed,
        Blocks.coal_ore,
        Blocks.iron_ore,
        Blocks.gold_ore,
        Blocks.redstone_ore,
        Blocks.lapis_ore,
        Blocks.diamond_ore,
        Blocks.emerald_ore,
        Blocks.quartz_ore,
        Blocks.coal_block,
        Blocks.iron_block,
        Blocks.gold_block,
        Blocks.diamond_block,
        Blocks.emerald_block,
        Blocks.redstone_block,
        Blocks.lapis_block,
        Blocks.mob_spawner,
        Blocks.end_portal_frame,
        Blocks.command_block
    )

    init {
        Leaf.commandManager.registerCommand(object : Command("xray", emptyArray()) {

            override fun execute(args: Array<String>) {
                if (args.size > 1) {
                    if (args[1].equals("add", ignoreCase = true)) {
                        if (args.size > 2) {
                            try {
                                val block = try {
                                    Block.getBlockById(args[2].toInt())
                                } catch (exception: NumberFormatException) {
                                    val tmpBlock = Block.getBlockFromName(args[2])

                                    if (Block.getIdFromBlock(tmpBlock) <= 0 || tmpBlock == null) {
                                        alert("§7Block §8${args[2]}§7 does not exist!")
                                        return
                                    }

                                    tmpBlock
                                }

                                if (xrayBlocks.contains(block)) {
                                    alert("This block is already on the list.")
                                    return
                                }

                                xrayBlocks.add(block)
                              saveConfig(xrayConfig)
                                alert("§7Added block §8${block.localizedName}§7.")
                                playEdit()
                            } catch (exception: NumberFormatException) {
                                chatSyntaxError()
                            }

                            return
                        }

                        chatSyntax("xray add <block_id>")
                        return
                    }

                    if (args[1].equals("remove", ignoreCase = true)) {
                        if (args.size > 2) {
                            try {
                                var block: Block

                                try {
                                    block = Block.getBlockById(args[2].toInt())
                                } catch (exception: NumberFormatException) {
                                    block = Block.getBlockFromName(args[2])

                                    if (Block.getIdFromBlock(block) <= 0) {
                                        alert("§7Block §8${args[2]}§7 does not exist!")
                                        return
                                    }
                                }

                                if (!xrayBlocks.contains(block)) {
                                    alert("This block is not on the list.")
                                    return
                                }

                                xrayBlocks.remove(block)
                                saveConfig(xrayConfig)
                                alert("§7Removed block §8${block.localizedName}§7.")
                                playEdit()
                            } catch (exception: NumberFormatException) {
                                chatSyntaxError()
                            }

                            return
                        }
                        chatSyntax("xray remove <block_id>")
                        return
                    }

                    if (args[1].equals("list", ignoreCase = true)) {
                        alert("§8Xray blocks:")
                        xrayBlocks.forEach { alert("§8${it.localizedName} §7-§c ${Block.getIdFromBlock(it)}") }
                        return
                    }
                }

                chatSyntax("xray <add, remove, list>")
            }
        })
    }

    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }
}
