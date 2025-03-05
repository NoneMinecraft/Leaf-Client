package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.ui.cape.GuiCapeManager

@ModuleInfo(name = "CapeManager", category = ModuleCategory.CLIENT, canEnable = false)
class CapeManager : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(GuiCapeManager)
    }
}