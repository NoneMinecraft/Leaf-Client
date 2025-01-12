package net.ccbluex.liquidbounce.features.module.modules.rage.rage.render

import net.ccbluex.liquidbounce.features.module.modules.rage.CounterStrike
import net.minecraft.client.gui.FontRenderer
import org.lwjgl.opengl.GL11
import java.awt.Color

fun drawText(fontRenderer: FontRenderer, text: String, x: Int, y: Int, r: Int, g: Int, b: Int) {
    GL11.glPushMatrix()
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    val color = (r shl 16) or (g shl 8) or b
    fontRenderer.drawString(text, x, y, color)

    GL11.glDisable(GL11.GL_BLEND)
    GL11.glPopMatrix()
}

private fun drawProgressRing(centerX: Int, centerY: Int, radius: Float, progress: Float) {
    val segments = 4000
    val anglePerSegment = (2 * Math.PI / segments).toFloat()

    GL11.glPushMatrix()
    GL11.glEnable(GL11.GL_LINE_SMOOTH)
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glColor4f(1f, 1f, 1f, 1f)
    GL11.glLineWidth(3f)
    GL11.glBegin(GL11.GL_LINE_STRIP)
    for (i in 0..(segments * progress).toInt()) {
        val angle = i * anglePerSegment
        val x = centerX + radius * Math.cos(angle.toDouble()).toFloat()
        val y = centerY + radius * Math.sin(angle.toDouble()).toFloat()
        GL11.glVertex2f(x, y)
    }
    GL11.glEnd()

    GL11.glDisable(GL11.GL_LINE_SMOOTH)
    GL11.glDisable(GL11.GL_BLEND)

    GL11.glEnable(GL11.GL_TEXTURE_2D)

    GL11.glPopMatrix()
}
 fun drawPanel(x: Double, y: Double, width: Double, height: Double, color: Color) {
    GL11.glPushMatrix()
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    GL11.glColor4ub(color.red.toByte(), color.green.toByte(), color.blue.toByte(), color.alpha.toByte())

    GL11.glBegin(GL11.GL_QUADS)
    GL11.glVertex2d(x, y)
    GL11.glVertex2d(x, y + height)
    GL11.glVertex2d(x + width, y + height)
    GL11.glVertex2d(x + width, y)
    GL11.glEnd()

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glDisable(GL11.GL_BLEND)
    GL11.glPopMatrix()
}
