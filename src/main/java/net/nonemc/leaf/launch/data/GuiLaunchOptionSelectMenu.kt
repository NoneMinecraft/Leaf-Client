﻿package net.nonemc.leaf.launch.data

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.launch.EnumLaunchFilter
import java.awt.Color

class GuiLaunchOptionSelectMenu : GuiScreen() {
    override fun initGui() {

        Leaf.launchFilters.addAll(
            when (0) {
                0 -> arrayListOf(EnumLaunchFilter.MODERN_UI)
                else -> emptyList()
            }
        )

        Leaf.startClient()

        if (mc.currentScreen is GuiLaunchOptionSelectMenu)
            mc.displayGuiScreen(Leaf.mainMenu)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, pTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, pTicks)
    }

    override fun actionPerformed(button: GuiButton) {
    }

    override fun keyTyped(p_keyTyped_1_: Char, p_keyTyped_2_: Int) {}
}