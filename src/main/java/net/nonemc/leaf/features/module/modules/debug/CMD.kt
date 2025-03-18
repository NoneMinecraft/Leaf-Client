package net.nonemc.leaf.features.module.modules.debug

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.minecraft.client.Minecraft
import net.nonemc.leaf.features.module.modules.debug.gui.CommandGUI

@ModuleInfo(name = "CMD", category = ModuleCategory.DEBUG)
class CMD : Module() {
    override fun onEnable() {
        Minecraft.getMinecraft().displayGuiScreen(CommandGUI(this))
    }
    override fun onDisable() {
        Minecraft.getMinecraft().displayGuiScreen(null)
    }
}