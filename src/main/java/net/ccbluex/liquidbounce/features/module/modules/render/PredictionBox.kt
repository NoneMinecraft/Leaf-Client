package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "PredictionBox", category = ModuleCategory.COMBAT)
class PredictionBox : Module() {
    private val predictSize = FloatValue("PredictSize", 3F, 0F, 6F)
    private val height = FloatValue("Height", 3F, -1.8F, 0.05F)
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        drawEntityBox(mc.thePlayer, Color.blue, true, false, 2F)
    }
    fun drawEntityBox(entity: Entity, color: Color, outline: Boolean, box: Boolean, outlineWidth: Float) {
        val renderManager = mc.renderManager
        val timer = mc.timer

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.enableGlCap(GL11.GL_BLEND)
        RenderUtils.disableGlCap(GL11.GL_TEXTURE_2D, GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * predictSize.get()
                - renderManager.renderPosX)
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * predictSize.get()
                - renderManager.renderPosY)
        val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * predictSize.get()
                - renderManager.renderPosZ)

        val entityBox = entity.entityBoundingBox
        val axisAlignedBB = AxisAlignedBB(
            entityBox.minX - entity.posX + x - 0.03,
            entityBox.minY - entity.posY + y,
            entityBox.minZ - entity.posZ + z - 0.03,
            entityBox.maxX - entity.posX + x + 0.03,
            entityBox.maxY - entity.posY + y + height.get(),
            entityBox.maxZ - entity.posZ + z + 0.0
        )

        if (outline) {
            GL11.glLineWidth(outlineWidth)
            RenderUtils.enableGlCap(GL11.GL_LINE_SMOOTH)
            RenderUtils.glColor(color.red, color.green, color.blue, if (box) 170 else 255)
            RenderUtils.drawSelectionBoundingBox(axisAlignedBB)
        }

        if (box) {
            RenderUtils.glColor(color.red, color.green, color.blue, if (outline) 26 else 35)
            RenderUtils.drawFilledBox(axisAlignedBB)
        }

        GlStateManager.resetColor()
        GL11.glDepthMask(true)
        RenderUtils.resetCaps()
    }
}