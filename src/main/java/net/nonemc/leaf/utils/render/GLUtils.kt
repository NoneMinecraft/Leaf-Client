package net.nonemc.leaf.utils.render

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.utils.mc
import net.nonemc.leaf.utils.render.shader.shaders.BlurShader
import net.nonemc.leaf.utils.render.shader.shaders.SRoundRectShader
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20
import java.awt.Color

object GLUtils {
    @JvmField
    var deltaTime = 0

    @JvmStatic
    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glColor(color)

        glBegin(GL_QUADS)

        glVertex2f(x2, y)
        glVertex2f(x, y)
        glVertex2f(x, y2)
        glVertex2f(x2, y2)

        glEnd()

        glDisable(GL_BLEND)
        glEnable(GL_TEXTURE_2D)
    }

    private fun drawBorder(x: Float, y: Float, x2: Float, y2: Float, width: Float, color: Int) {
        glEnable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(width)
        glColor(color)

        glBegin(GL_LINE_LOOP)

        glVertex2f(x2, y)
        glVertex2f(x, y)
        glVertex2f(x, y2)
        glVertex2f(x2, y2)

        glEnd()

        glDisable(GL_BLEND)
        glEnable(GL_TEXTURE_2D)
        glDisable(GL_LINE_SMOOTH)
    }

    @JvmStatic
    fun drawBorderedRect(x: Float, y: Float, x2: Float, y2: Float, width: Float, color: Int, color2: Int = color) {
        drawRect(x, y, x2, y2, color2)
        drawBorder(x, y, x2, y2, width, color)
    }

    @Suppress("unused")
    fun drawSeparateRoundedRect(
        x: Float,
        y: Float,
        x2: Float,
        y2: Float,
        tr: Float,
        br: Float,
        tl: Float,
        bl: Float,
        color: Color,
    ) {
        require(tr in 0f..1f && br in 0f..1f && tl in 0f..1f && br in 0f..1f) { "Rectangle radii should be between 0 and 1" }

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        SRoundRectShader.startShader()

        GL20.glUniform4f(
            SRoundRectShader.getUniform("color"),
            color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f
        )
        GL20.glUniform2f(SRoundRectShader.getUniform("size"), x2 - x, y2 - y)
        GL20.glUniform4f(SRoundRectShader.getUniform("radius"), tr, br, tl, bl)

        drawQuads(x, y, x2, y2)

        SRoundRectShader.stopShader()

        glDisable(GL_BLEND)
    }

    @JvmStatic
    fun drawQuads(x: Float, y: Float, x2: Float, y2: Float) {
        glBegin(GL_QUADS)

        glTexCoord2f(0f, 0f)
        glVertex2f(x, y)
        glTexCoord2f(0f, 1f)
        glVertex2f(x, y2)
        glTexCoord2f(1f, 1f)
        glVertex2f(x2, y2)
        glTexCoord2f(1f, 0f)
        glVertex2f(x2, y)

        glEnd()
    }

    @JvmStatic
    fun drawImage(image: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)

        mc.textureManager.bindTexture(image)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())

        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
    }

    fun interpolate(old: Double, current: Double): Double {
        return old + (current - old) * mc.timer.renderPartialTicks.toDouble()
    }

    fun glColor(red: Float, green: Int, blue: Int, alpha: Int = 255) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    @JvmStatic
    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f

        GlStateManager.color(red, green, blue, alpha)
    }

    private fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f

        GlStateManager.color(red, green, blue, alpha)
    }
}