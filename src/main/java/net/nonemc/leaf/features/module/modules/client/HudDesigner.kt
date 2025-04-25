package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.ui.hud.designer.GuiHudDesigner

@ModuleInfo(name = "HudDesigner", category = ModuleCategory.CLIENT, canEnable = false)
class HudDesigner : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(GuiHudDesigner())
    }
}