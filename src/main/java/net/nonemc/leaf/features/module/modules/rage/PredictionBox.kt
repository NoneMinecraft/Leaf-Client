package net.nonemc.leaf.features.module.modules.rage

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.render.RenderUtils
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "PredictionBox", category = ModuleCategory.Rage)
class PredictionBox : Module() {
    private val predictSize = FloatValue("PredictSize", 3F, 0F, 6F)
    private val height = FloatValue("Height", 3F, -1.8F, 0.05F)
    private val mode = ListValue("Mode", arrayOf("ClientSide","ServerSide"),"ServerSide")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val thicknessValue = FloatValue("Thickness", 2F, 1F, 5F)
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        predictionBox(mc.thePlayer, Color(redValue.get(),greenValue.get(),blueValue.get(),alphaValue.get()), true, false, thicknessValue.get())
    }
    fun predictionBox(entity: Entity, color: Color, outline: Boolean, box: Boolean, outlineWidth: Float) {
        val renderManager = mc.renderManager
        val timer = mc.timer

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.enableGlCap(GL11.GL_BLEND)
        RenderUtils.disableGlCap(GL11.GL_TEXTURE_2D, GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        val x = if(mode.get() == "ClientSide")(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * predictSize.get()
                - renderManager.renderPosX) else entity.serverPosX.toDouble() / 32
        val y = if(mode.get() == "ClientSide")(entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * predictSize.get()
                - renderManager.renderPosY) else entity.serverPosY.toDouble() / 32
        val z = if(mode.get() == "ClientSide")(entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * predictSize.get()
                - renderManager.renderPosZ) else entity.serverPosZ.toDouble() / 32

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