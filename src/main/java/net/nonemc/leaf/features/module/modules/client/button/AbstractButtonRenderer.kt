﻿package net.nonemc.leaf.features.module.modules.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.nonemc.leaf.font.FontLoaders
import java.awt.Color

abstract class AbstractButtonRenderer(protected val button: GuiButton) {
    abstract fun render(mouseX: Int, mouseY: Int, mc: Minecraft)

    open fun drawButtonText(mc: Minecraft) {
        FontLoaders.F18.DisplayFonts(
            button.displayString,
            button.xPosition + button.width / 2f - FontLoaders.F18.DisplayFontWidths(
                FontLoaders.F18,
                button.displayString
            ) / 2f,
            button.yPosition + button.height / 2f - FontLoaders.F18.height / 2f,
            if (button.enabled) Color.WHITE.rgb else Color.GRAY.rgb,
            FontLoaders.F18
        )
    }
}