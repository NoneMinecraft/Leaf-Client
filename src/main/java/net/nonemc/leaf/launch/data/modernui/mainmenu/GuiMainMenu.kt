package net.nonemc.leaf.launch.data.modernui.mainmenu

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiYesNoCallback
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        mc.displayGuiScreen(MainMenu())
        drawBackground(0)
    }
}