package net.nonemc.leaf.features.module.modules.render

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import org.lwjgl.opengl.GL11

@ModuleInfo(name = "Projector", category = ModuleCategory.RENDER)
class Projector : Module() {

    private val xOffset = FloatValue("XOffset", 0.4f, -1f, 1f)
    private val yOffset = FloatValue("YOffset", -0.5f, -1f, 1f)
    private val zOffset = FloatValue("ZOffset", 0.4f, -1f, 1f)
    private val radius = IntegerValue("Radius", 5, 1, 20)

    private data class BlockData(val pos: BlockPos, val state: IBlockState)

    private val block = mutableListOf<BlockData>()

    override fun onEnable() {
        block.clear()
        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return

        val currentX = MathHelper.floor_double(player.posX)
        val currentY = MathHelper.floor_double(player.posY)
        val currentZ = MathHelper.floor_double(player.posZ)

        for (x in (currentX - radius.get())..(currentX + radius.get())) {
            for (y in currentY..(currentY + radius.get().coerceAtMost(255))) {
                for (z in (currentZ - radius.get())..(currentZ + radius.get())) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    if (state.block !== Blocks.air) {
                        block.add(BlockData(pos, state))
                    }
                }
            }
        }
    }

    override fun onDisable() {
        block.clear()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (block.isEmpty()) return

        val renderManager = mc.renderManager
        val blockRenderer = mc.blockRendererDispatcher
        val worldRenderer = Tessellator.getInstance().worldRenderer

        GlStateManager.pushMatrix()
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1f, 1f, 1f, 0.4f)
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.translate(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ)

        try {
            for (block in block) {
                val pos = block.pos
                val state = block.state
                val model = blockRenderer.getModelFromBlockState(state, mc.theWorld, pos) ?: continue
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
                worldRenderer.setTranslation(
                    pos.x - renderManager.renderPosX + xOffset.get(),
                    pos.y - renderManager.renderPosY + yOffset.get(),
                    pos.z - renderManager.renderPosZ + zOffset.get()
                )

                blockRenderer.blockModelRenderer.renderModel(mc.theWorld, model, state, pos, worldRenderer, true)
                Tessellator.getInstance().draw()
                worldRenderer.setTranslation(0.0, 0.0, 0.0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            GlStateManager.depthMask(true)
            GlStateManager.enableDepth()
            GlStateManager.enableLighting()
            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.popMatrix()
        }
    }
}