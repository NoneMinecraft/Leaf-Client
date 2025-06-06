﻿package net.nonemc.leaf.libs.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.features.module.modules.client.HudShadows
import org.lwjgl.opengl.GL11.*

object shadowRenderUtils {
    @JvmStatic
    var deltaTime = 0

    @JvmStatic
    fun drawShadowWithCustomAlpha(x: Float, y: Float, width: Float, height: Float, alpha: Float) {
        if (HudShadows.UiShadowValue.equals("TextureShadows")) {
            drawTexturedRectWithCustomAlpha(x - 9, y - 9, 9f, 9f, "paneltopleft", alpha)
            drawTexturedRectWithCustomAlpha(x - 9, y + height, 9f, 9f, "panelbottomleft", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y + height, 9f, 9f, "panelbottomright", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y - 9, 9f, 9f, "paneltopright", alpha)
            drawTexturedRectWithCustomAlpha(x - 9, y, 9f, height, "panelleft", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y, 9f, height, "panelright", alpha)
            drawTexturedRectWithCustomAlpha(x, y - 9, width, 9f, "paneltop", alpha)
            drawTexturedRectWithCustomAlpha(x, y + height, width, 9f, "panelbottom", alpha)
        } else if (HudShadows.UiShadowValue.equals("TextureGlow")) {
            drawTexturedRectWithCustomAlpha(x - 9, y - 9, 9f, 9f, "glowpaneltopleft", alpha)
            drawTexturedRectWithCustomAlpha(x - 9, y + height, 9f, 9f, "glowpanelbottomleft", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y + height, 9f, 9f, "glowpanelbottomright", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y - 9, 9f, 9f, "glowpaneltopright", alpha)
            drawTexturedRectWithCustomAlpha(x - 9, y, 9f, height, "glowpanelleft", alpha)
            drawTexturedRectWithCustomAlpha(x + width, y, 9f, height, "glowpanelright", alpha)
            drawTexturedRectWithCustomAlpha(x, y - 9, width, 9f, "glowpaneltop", alpha)
            drawTexturedRectWithCustomAlpha(x, y + height, width, 9f, "glowpanelbottom", alpha)
        } else if (HudShadows.UiShadowValue.equals("None")) {
            return; }
    }

    @JvmStatic
    fun drawTexturedRectWithCustomAlpha(x: Float, y: Float, width: Float, height: Float, image: String, alpha: Float) {
        glPushMatrix()
        val enableBlend = glIsEnabled(GL_BLEND)
        val disableAlpha = !glIsEnabled(GL_ALPHA_TEST)
        if (!enableBlend) glEnable(GL_BLEND)
        if (!disableAlpha) glDisable(GL_ALPHA_TEST)
        GlStateManager.color(1f, 1f, 1f, alpha)
        Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("leaf/ui/shadow/$image.png"))
        drawModalRectWithCustomSizedTexture(
            x,
            y,
            0F,
            0F,
            width,
            height,
            width,
            height
        )
        if (!enableBlend) glDisable(GL_BLEND)
        if (!disableAlpha) glEnable(GL_ALPHA_TEST)
        GlStateManager.resetColor()
        glPopMatrix()
    }

    @JvmStatic
    fun drawModalRectWithCustomSizedTexture(
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        width: Float,
        height: Float,
        textureWidth: Float,
        textureHeight: Float,
    ) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex(
            (u * f).toDouble(),
            ((v + height) * f1).toDouble()
        ).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(
            ((u + width) * f).toDouble(),
            ((v + height) * f1).toDouble()
        ).endVertex()
        worldrenderer.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(
            ((u + width) * f).toDouble(),
            (v * f1).toDouble()
        ).endVertex()
        worldrenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }
}


