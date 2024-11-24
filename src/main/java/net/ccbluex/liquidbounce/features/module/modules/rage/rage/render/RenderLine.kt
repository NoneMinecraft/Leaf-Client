package net.ccbluex.liquidbounce.features.module.modules.rage.rage.render
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import java.awt.Color

 fun render3DLine(entity: EntityPlayer, color: Color, yOffset:Double) {
    val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * MinecraftInstance.mc.timer.renderPartialTicks -
            MinecraftInstance.mc.renderManager.renderPosX)
    val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * MinecraftInstance.mc.timer.renderPartialTicks -
            MinecraftInstance.mc.renderManager.renderPosY)
    val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * MinecraftInstance.mc.timer.renderPartialTicks -
            MinecraftInstance.mc.renderManager.renderPosZ)
    val eyeVector = Vec3(0.0, 0.0, 1.0)
        .rotatePitch((-Math.toRadians(MinecraftInstance.mc.thePlayer.rotationPitch.toDouble())).toFloat())
        .rotateYaw((-Math.toRadians(MinecraftInstance.mc.thePlayer.rotationYaw.toDouble())).toFloat())

    RenderUtils.glColor(color, 255)

    GL11.glBegin(GL11.GL_LINE_STRIP)

    GL11.glVertex3d(eyeVector.xCoord, MinecraftInstance.mc.thePlayer.getEyeHeight().toDouble() + eyeVector.yCoord, eyeVector.zCoord)

    GL11.glVertex3d(x, y+ yOffset, z)

    GL11.glVertex3d(x, y + entity.height + yOffset, z)

    GL11.glEnd()
}