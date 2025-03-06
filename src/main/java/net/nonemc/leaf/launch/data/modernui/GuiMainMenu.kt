package net.nonemc.leaf.launch.data.modernui

import net.minecraft.client.gui.*
import net.nonemc.leaf.features.module.modules.client.HUD
import net.nonemc.leaf.launch.data.modernui.mainmenu.*

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (HUD.mainMenuStyle.equals("Five")) {
            mc.displayGuiScreen(ModernGuiMainMenu())
        } else {
            mc.displayGuiScreen(ClassicGuiMainMenu())
        }
        drawBackground(0)
    }

}