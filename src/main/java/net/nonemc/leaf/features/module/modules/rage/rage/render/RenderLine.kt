package net.nonemc.leaf.features.module.modules.rage.rage.render

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import net.nonemc.leaf.libs.base.mc
import net.nonemc.leaf.libs.render.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

fun render3DLine(entity: EntityPlayer, color: Color, a: Int, yOffset: Double) {
    val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * net.nonemc.leaf.libs.base.mc.timer.renderPartialTicks -
            net.nonemc.leaf.libs.base.mc.renderManager.renderPosX)
    val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * net.nonemc.leaf.libs.base.mc.timer.renderPartialTicks -
            net.nonemc.leaf.libs.base.mc.renderManager.renderPosY)
    val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * net.nonemc.leaf.libs.base.mc.timer.renderPartialTicks -
            net.nonemc.leaf.libs.base.mc.renderManager.renderPosZ)
    val eyeVector = Vec3(0.0, 0.0, 1.0)
        .rotatePitch((-Math.toRadians(net.nonemc.leaf.libs.base.mc.thePlayer.rotationPitch.toDouble())).toFloat())
        .rotateYaw((-Math.toRadians(net.nonemc.leaf.libs.base.mc.thePlayer.rotationYaw.toDouble())).toFloat())

    RenderUtils.glColor(color, a)

    GL11.glBegin(GL11.GL_LINE_STRIP)

    GL11.glVertex3d(eyeVector.xCoord, net.nonemc.leaf.libs.base.mc.thePlayer.getEyeHeight().toDouble() + eyeVector.yCoord, eyeVector.zCoord)

    GL11.glVertex3d(x, y + yOffset, z)

    GL11.glVertex3d(x, y + entity.height + yOffset, z)

    GL11.glEnd()
}
