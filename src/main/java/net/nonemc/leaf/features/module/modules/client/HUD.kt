﻿package net.nonemc.leaf.features.module.modules.client

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.client.button.*
import net.nonemc.leaf.ui.cape.GuiCapeManager.height
import net.nonemc.leaf.ui.hud.designer.GuiHudDesigner
import net.nonemc.leaf.libs.render.ColorUtils
import net.nonemc.leaf.libs.render.EaseUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import java.awt.Color

@ModuleInfo(name = "HUD", category = ModuleCategory.CLIENT, array = false, defaultOn = true)
object HUD : Module() {
    val shadowValue = ListValue("TextShadowMode", arrayOf("LiquidBounce", "Outline", "Default", "Autumn"), "Autumn")
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", false)
    val chatRectValue = BoolValue("ChatRect", true)
    val chatCombineValue = BoolValue("ChatCombine", true)
    val chatAnimValue = BoolValue("ChatAnimation", true)
    private val HealthValue = BoolValue("Health", true)
    private val waterMark = BoolValue("Watermark", true)
    val rainbowStartValue = FloatValue("RainbowStart", 0.55f, 0f, 1f)
    val rainbowStopValue = FloatValue("RainbowStop", 0.85f, 0f, 1f)
    val rainbowSaturationValue = FloatValue("RainbowSaturation", 0.45f, 0f, 1f)
    val rainbowBrightnessValue = FloatValue("RainbowBrightness", 0.85f, 0f, 1f)
    val rainbowSpeedValue = IntegerValue("RainbowSpeed", 1500, 500, 7000)
    val arraylistXAxisAnimSpeedValue = IntegerValue("ArraylistXAxisAnimSpeed", 10, 5, 20)
    val arraylistXAxisAnimTypeValue = EaseUtils.getEnumEasingList("ArraylistXAxisAnimType")
    val arraylistXAxisAnimOrderValue = EaseUtils.getEnumEasingOrderList("ArraylistXAxisHotbarAnimOrder")
    val arraylistYAxisAnimSpeedValue = IntegerValue("ArraylistYAxisAnimSpeed", 10, 5, 20)
    val arraylistYAxisAnimTypeValue = EaseUtils.getEnumEasingList("ArraylistYAxisAnimType")
    val arraylistYAxisAnimOrderValue = EaseUtils.getEnumEasingOrderList("ArraylistYAxisHotbarAnimOrder")
    private val fontEpsilonValue = FloatValue("FontVectorEpsilon", 0.5f, 0f, 1.5f)
    private val buttonValue = ListValue("Button", arrayOf("Better", "Rounded", "FLine", "Rise", "Vanilla"), "Rounded")
    val mainMenuStyle = ListValue("MainMenu", arrayOf("Modern", "Legacy"), "Modern")

    private var lastFontEpsilon = 0f

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner) return
        Leaf.hud.render(false, event.partialTicks)
        if (waterMark.get()) renderWatermark()
        if (HealthValue.get()) mc.fontRendererObj.drawStringWithShadow(
            MathHelper.ceiling_float_int(mc.thePlayer.health).toString(),
            (width / 2 - 4).toFloat(),
            (height / 2 - 13).toFloat(),
            if (mc.thePlayer.health <= 15) Color(255, 0, 0).rgb else Color(0, 255, 0).rgb
        )
        GlStateManager.resetColor()
    }

    private fun renderWatermark() {
        var width = 3
        mc.fontRendererObj.drawStringWithShadow(
            "LEAF ",
            3.0f,
            3.0f,
            ColorUtils.toRGB(0, 255, 0, 255)
        )
        width += mc.fontRendererObj.getStringWidth("LEAF")
        mc.fontRendererObj.drawStringWithShadow(
            "CLIENT",
            width.toFloat(),
            3.0f,
            -1
        )
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        Leaf.hud.update()
        if (mc.currentScreen == null && lastFontEpsilon != fontEpsilonValue.get()) {
            lastFontEpsilon = fontEpsilonValue.get()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        lastFontEpsilon = fontEpsilonValue.get()
    }

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return
        }
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive && event.guiScreen != null && !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner)) {
            mc.entityRenderer.loadShader(ResourceLocation("leaf/blur.json"))
        } else if (mc.entityRenderer.shaderGroup != null && mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("leaf/blur.json")) {
            mc.entityRenderer.stopUseShader()
        }
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        Leaf.hud.handleKey('a', event.key)
    }

    fun getButtonRenderer(button: GuiButton): AbstractButtonRenderer? {
        return when (buttonValue.get().lowercase()) {
            "better" -> BetterButtonRenderer(button)
            "rounded" -> RoundedButtonRenderer(button)
            "fline" -> FLineButtonRenderer(button)
            "rise" -> RiseButtonRenderer(button)
            else -> null
        }
    }
}
