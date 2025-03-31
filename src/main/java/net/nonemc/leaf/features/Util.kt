package net.nonemc.leaf.features

import net.minecraft.client.gui.FontRenderer
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ChatComponentText
import net.nonemc.leaf.utils.mc
import org.lwjgl.opengl.GL11

object Util {
    fun drawText(fontRenderer: FontRenderer, text: String, x: Int, y: Int, r: Int, g: Int, b: Int) {
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val color = (r shl 16) or (g shl 8) or b
        fontRenderer.drawString(text, x, y, color)

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }
    fun drawPanel(x: Double, y: Double, width: Double, height: Double, r: Int, g: Int, b: Int, alpha: Int) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r.toByte(), g.toByte(), b.toByte(), alpha.toByte())

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

    fun Chat(message: String) {
        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(message))
    }

    fun tell(player: String, message: String) {
        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage("/tell $player $message"))
    }

    fun ChatPrint(message: String) {
        mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
    }

}
