package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.Minecraft
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "NightMode", category = ModuleCategory.RENDER)
class NightMode : Module() {

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        // 获取玩家头部位置
        val player = Minecraft.getMinecraft().thePlayer
        val startPos = player.getPositionEyes(event.partialTicks)

        // 获取玩家的视角角度
        val yaw = Math.toRadians(player.rotationYaw.toDouble())
        val pitch = Math.toRadians(player.rotationPitch.toDouble())

        // 计算线的终点
        val distance = 50.0 // 线的长度
        val endPos = Vec3(
            startPos.xCoord + distance * (-cos(pitch) * sin(yaw)),
            startPos.yCoord + distance * sin(pitch),
            startPos.zCoord + distance * (cos(pitch) * cos(yaw))
        )

        // 启用 OpenGL 状态
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(2.0f)
        GL11.glColor3f(1.0f, 0.0f, 0.0f) // 红色

        // 开始渲染线
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(startPos.xCoord, startPos.yCoord, startPos.zCoord)
        GL11.glVertex3d(endPos.xCoord, endPos.yCoord, endPos.zCoord)
        GL11.glEnd()

        // 恢复 OpenGL 状态
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glPopMatrix()

    }}