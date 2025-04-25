package net.nonemc.leaf.ui.mainmenu

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiYesNoCallback

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        mc.displayGuiScreen(MainMenu())
        drawBackground(0)
    }
}