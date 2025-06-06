﻿package net.nonemc.leaf.ui.hud.element.elements

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.ui.hud.element.Border
import net.nonemc.leaf.ui.hud.element.Element
import net.nonemc.leaf.ui.hud.element.ElementInfo
import net.nonemc.leaf.ui.hud.element.Side
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.libs.render.ColorUtils
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.libs.render.shadowRenderUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.FontValue
import net.nonemc.leaf.value.IntegerValue
import org.lwjgl.input.Keyboard
import java.awt.Color

@ElementInfo(name = "TabGUI", blur = true)
class TabGUI(x: Double = 5.0, y: Double = 25.0) : Element(x = x, y = y) {

    private val redValue = IntegerValue("Rectangle Red", 111, 0, 255)
    private val greenValue = IntegerValue("Rectangle Green", 111, 0, 255)
    private val blueValue = IntegerValue("Rectangle Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Rectangle Alpha", 255, 0, 255)
    private val rectangleRainbow = BoolValue("Rectangle Rainbow", false)
    private val backgroundRedValue = IntegerValue("Background Red", 0, 0, 255)
    private val backgroundGreenValue = IntegerValue("Background Green", 0, 0, 255)
    private val backgroundBlueValue = IntegerValue("Background Blue", 0, 0, 255)
    private val backgroundAlphaValue = IntegerValue("Background Alpha", 150, 0, 255)
    private val borderValue = BoolValue("Border", false)
    private val borderStrength = FloatValue("Border Strength", 2F, 1F, 5F)
    private val borderRedValue = IntegerValue("Border Red", 0, 0, 255)
    private val borderGreenValue = IntegerValue("Border Green", 0, 0, 255)
    private val borderBlueValue = IntegerValue("Border Blue", 0, 0, 255)
    private val borderAlphaValue = IntegerValue("Border Alpha", 10, 0, 255)
    private val borderRainbow = BoolValue("Border Rainbow", false)
    private val arrowsValue = BoolValue("Arrows", false)
    private val fontValue = FontValue("Font", Fonts.font35)
    private val textShadow = BoolValue("TextShadow", true)
    private val textFade = BoolValue("TextFade", false)
    private val textPositionY = FloatValue("TextPosition-Y", 2F, 0F, 5F)
    private val width = FloatValue("Width", 60F, 55F, 100F)
    private val tabHeight = FloatValue("TabHeight", 12F, 10F, 15F)
    private val upperCaseValue = BoolValue("UpperCase", false)
    private val tabs = mutableListOf<Tab>()

    private var categoryMenu = true
    private var selectedCategory = 0
    private var selectedModule = 0

    private var tabY = 0F
    private var itemY = 0F

    init {
        for (category in ModuleCategory.values()) {
            val tab = Tab(category.displayName)

            Leaf.moduleManager.modules
                .filter { module: Module -> category == module.category }
                .forEach { e: Module -> tab.modules.add(e) }

            tabs.add(tab)
        }
    }

    override fun drawElement(partialTicks: Float): Border {
        updateAnimation()

        val fontRenderer = fontValue.get()

        // Color
        val color = if (!rectangleRainbow.get()) {
            Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get())
        } else {
            ColorUtils.rainbowWithAlpha(alphaValue.get())
        }

        val backgroundColor = Color(
            backgroundRedValue.get(), backgroundGreenValue.get(), backgroundBlueValue.get(),
            backgroundAlphaValue.get()
        )

        val borderColor = if (!borderRainbow.get()) {
            Color(borderRedValue.get(), borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get())
        } else {
            ColorUtils.rainbowWithAlpha(borderAlphaValue.get())
        }

        // Draw
        val guiHeight = tabs.size * tabHeight.get()

        if (borderValue.get()) {
            RenderUtils.drawBorderedRect(
                1F,
                0F,
                width.get(),
                guiHeight,
                borderStrength.get(),
                borderColor.rgb,
                backgroundColor.rgb
            )
        } else {
            RenderUtils.drawRect(1F, 0F, width.get(), guiHeight, backgroundColor.rgb)
        }
        // RenderUtils.drawGradientSideways(1.0, (1 + tabY - 1).toDouble(), width.get().toDouble(), (tabY + tabHeight.get()).toDouble(), color.rgb,Color(color.red, color.green,color.blue,50).rgb)
        GlStateManager.resetColor()

        shadowRenderUtils.drawShadowWithCustomAlpha(1f, 0f, width.get(), guiHeight, 240f)


        var y = 1F
        tabs.forEachIndexed { index, tab ->
            var tabName = tab.tabName
            if (upperCaseValue.get()) {
                tabName = tabName.uppercase()
            }

            val textX = if (side.horizontal == Side.Horizontal.RIGHT) {
                width.get() - fontRenderer.getStringWidth(tabName) - tab.textFade - 3
            } else {
                tab.textFade + 5
            }
            val textY = y + textPositionY.get()

            val textColor = if (selectedCategory == index) 0xffffff else Color(210, 210, 210).rgb

            fontRenderer.drawString(tabName, textX, textY, textColor, textShadow.get())

            if (arrowsValue.get()) {
                if (side.horizontal == Side.Horizontal.RIGHT) {
                    fontRenderer.drawString(
                        if (!categoryMenu && selectedCategory == index) ">" else "<", 3F, y + 2F,
                        0xffffff, textShadow.get()
                    )
                } else {
                    fontRenderer.drawString(
                        if (!categoryMenu && selectedCategory == index) "<" else ">",
                        width.get() - 8F, y + 2F, 0xffffff, textShadow.get()
                    )
                }
            }

            if (index == selectedCategory && !categoryMenu) {
                val tabX = if (side.horizontal == Side.Horizontal.RIGHT) {
                    1F - tab.menuWidth
                } else {
                    width.get() + 5
                }

                tab.drawTab(
                    tabX,
                    y,
                    color.rgb,
                    backgroundColor.rgb,
                    borderColor.rgb,
                    borderStrength.get(),
                    upperCaseValue.get(),
                    fontRenderer
                )
            }
            y += tabHeight.get()
        }

        return Border(1F, 0F, width.get(), guiHeight)
    }

    override fun handleKey(c: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_UP -> parseAction(Action.UP)
            Keyboard.KEY_DOWN -> parseAction(Action.DOWN)
            Keyboard.KEY_RIGHT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.LEFT else Action.RIGHT)
            Keyboard.KEY_LEFT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.RIGHT else Action.LEFT)
            Keyboard.KEY_RETURN -> parseAction(Action.TOGGLE)
        }
    }

    private fun updateAnimation() {
        val delta = RenderUtils.deltaTime

        val xPos = tabHeight.get() * selectedCategory
        if (tabY.toInt() != xPos.toInt()) {
            if (xPos > tabY) {
                tabY += 0.1F * delta
            } else {
                tabY -= 0.1F * delta
            }
        } else {
            tabY = xPos
        }
        val xPos2 = tabHeight.get() * selectedModule

        if (itemY.toInt() != xPos2.toInt()) {
            if (xPos2 > itemY) {
                itemY += 0.1F * delta
            } else {
                itemY -= 0.1F * delta
            }
        } else {
            itemY = xPos2
        }

        if (categoryMenu) {
            itemY = 0F
        }

        if (textFade.get()) {
            tabs.forEachIndexed { index, tab ->
                if (index == selectedCategory) {
                    if (tab.textFade < 4) {
                        tab.textFade += 0.05F * delta
                    }

                    if (tab.textFade > 4) {
                        tab.textFade = 4F
                    }
                } else {
                    if (tab.textFade > 0) {
                        tab.textFade -= 0.05F * delta
                    }

                    if (tab.textFade < 0) {
                        tab.textFade = 0F
                    }
                }
            }
        } else {
            for (tab in tabs) {
                if (tab.textFade > 0) {
                    tab.textFade -= 0.05F * delta
                }

                if (tab.textFade < 0) {
                    tab.textFade = 0F
                }
            }
        }
    }

    private fun parseAction(action: Action) {
        when (action) {
            Action.UP -> if (categoryMenu) {
                --selectedCategory
                if (selectedCategory < 0) {
                    selectedCategory = tabs.size - 1
                    tabY = tabHeight.get() * selectedCategory.toFloat()
                }
            } else {
                --selectedModule
                if (selectedModule < 0) {
                    selectedModule = tabs[selectedCategory].modules.size - 1
                    itemY = tabHeight.get() * selectedModule.toFloat()
                }
            }

            Action.DOWN -> if (categoryMenu) {
                ++selectedCategory
                if (selectedCategory > tabs.size - 1) {
                    selectedCategory = 0
                    tabY = tabHeight.get() * selectedCategory.toFloat()
                }
            } else {
                ++selectedModule
                if (selectedModule > tabs[selectedCategory].modules.size - 1) {
                    selectedModule = 0
                    itemY = tabHeight.get() * selectedModule.toFloat()
                }
            }

            Action.LEFT -> if (!categoryMenu) categoryMenu = true

            Action.RIGHT -> if (categoryMenu) {
                categoryMenu = false
                selectedModule = 0
            }

            Action.TOGGLE -> if (!categoryMenu) {
                val sel = selectedModule
                tabs[selectedCategory].modules[sel].toggle()
            }
        }
    }

    /**
     * TabGUI Tab
     */
    private inner class Tab(val tabName: String) {

        val modules = mutableListOf<Module>()
        var menuWidth = 0
        var textFade = 0F

        fun drawTab(
            x: Float,
            y: Float,
            color: Int,
            backgroundColor: Int,
            borderColor: Int,
            borderStrength: Float,
            upperCase: Boolean,
            fontRenderer: FontRenderer,
        ) {
            var maxWidth = 0

            for (module in modules)
                if (fontRenderer.getStringWidth(if (upperCase) module.name.uppercase() else module.name) + 4 > maxWidth) {
                    maxWidth =
                        (fontRenderer.getStringWidth(if (upperCase) module.name.uppercase() else module.name) + 7F).toInt()
                }

            menuWidth = maxWidth

            val menuHeight = modules.size * tabHeight.get()

            if (borderValue.get()) {
                RenderUtils.drawBorderedRect(
                    x - 1F,
                    y - 1F,
                    x + menuWidth - 2F,
                    y + menuHeight - 1F,
                    borderStrength,
                    borderColor,
                    backgroundColor
                )
            } else {
                RenderUtils.drawRect(x - 1F, y - 1F, x + menuWidth - 2F, y + menuHeight - 1F, backgroundColor)
            }

            RenderUtils.drawRect(
                x - 1.toFloat(),
                y + itemY - 1,
                x + menuWidth - 2F,
                y + itemY + tabHeight.get() - 1,
                color
            )
            GlStateManager.resetColor()

            modules.forEachIndexed { index, module ->
                val moduleColor = if (module.state) 0xffffff else Color(205, 205, 205).rgb

                fontRenderer.drawString(
                    if (upperCase) module.name.uppercase() else module.name, x + 2F,
                    y + tabHeight.get() * index + textPositionY.get(), moduleColor, textShadow.get()
                )
            }
        }
    }

    /**
     * TabGUI Action
     */
    enum class Action { UP, DOWN, LEFT, RIGHT, TOGGLE }
}
