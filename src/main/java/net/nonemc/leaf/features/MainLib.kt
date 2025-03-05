/*
Leaf Hack Client
Code By None
* */
package net.nonemc.leaf.features

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.nonemc.leaf.utils.mc
import org.lwjgl.opengl.GL11
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

var Pitch = 0.0F

object MainLib {
    fun renderLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glLineWidth(2.0f)

        // 设置颜色
        GL11.glColor4f(red, green, blue, alpha)

        // 绘制线条
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2f(startX, startY)
        GL11.glVertex2f(endX, endY)
        GL11.glEnd()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    fun FindItems(items: Items): Int {
        for (i in 1 until mc.thePlayer.inventory.mainInventory.size) {
            val stack = mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == items) {
                return i
            }
        }
        return -1
    }

    private fun drawGradientPanel(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        r: Int,
        g: Int,
        b: Int,
        step: Int
    ) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)


        val steps = step
        val stepHeight = height / steps
        for (i in 0 until steps) {
            val alpha = (255 * (1.0 - i.toDouble() / steps)).toInt().toByte()
            GL11.glColor4ub(r.toByte(), g.toByte(), b.toByte(), alpha)
            val currentY = y + i * stepHeight
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glVertex2d(x, currentY)
            GL11.glVertex2d(x, currentY + stepHeight)
            GL11.glVertex2d(x + width, currentY + stepHeight)
            GL11.glVertex2d(x + width, currentY)
            GL11.glEnd()
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D)
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

    //drawText("text",1,1,1,1,1,Fonts.font28)
    fun drawText(text: String, x: Int, y: Int, r: Int, g: Int, b: Int, font: FontRenderer) {
        val fontRenderer: FontRenderer = font
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val color = (r shl 16) or (g shl 8) or b
        fontRenderer.drawString(text, x, y, color)

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    fun relativePositioningX(value: Double): Double {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val screenWidth = scaledResolution.scaledWidth
        val referenceWidth = screenWidth.toDouble()
        return ((value / referenceWidth) * screenWidth)
    }

    fun relativePositioningY(value: Double): Double {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val screenHeight = scaledResolution.scaledHeight
        val referenceHeight = screenHeight.toDouble()
        return ((value / referenceHeight) * screenHeight)
    }

    var timerTick = 0
    fun updateTimer(isStart: Boolean, seconds: Int, minutes: Int): Pair<Int, Int> {

        var timerSeconds = seconds
        var timerMinutes = minutes

        if (isStart) {
            if (timerTick < 20) timerTick++
            if (timerTick == 20) {
                timerTick = 0
                timerSeconds--
            }
            if (timerMinutes == 0 && timerSeconds == 0) {
                timerMinutes = 0
                timerSeconds = 0
                return Pair(timerSeconds, timerMinutes)
            }
            if (timerSeconds == 0) {
                timerSeconds = 60
                timerMinutes--
            }
        }

        return Pair(timerSeconds, timerMinutes)
    }

    fun block() {
        val playerPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
        val radius = 1
        val nonAirBlocks: MutableList<BlockPos> = ArrayList()
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val blockPos = playerPos.add(x, y, z)
                    if (mc.theWorld.getBlockState(blockPos).block !== Blocks.air) {
                        nonAirBlocks.add(blockPos)
                    }
                }
            }
        }
        var closestDistance = Double.MAX_VALUE
        var closestBlock: BlockPos? = null
        for (blockPos in nonAirBlocks) {
            val distanceSq = mc.thePlayer.getDistanceSq(
                blockPos.x.toDouble(),
                blockPos.y.toDouble(),
                blockPos.z.toDouble()
            )
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq
                closestBlock = blockPos
            }

            if (closestBlock != null) {
                mc.thePlayer.rotationYaw = Math.toDegrees(
                    atan2(
                        closestBlock.z - mc.thePlayer.posZ,
                        closestBlock.x - mc.thePlayer.posX

                    )
                ).toFloat() - 90


                mc.thePlayer.rotationPitch = Math.toDegrees(

                    -atan(
                        (closestBlock.y - mc.thePlayer.posY) / sqrt(
                            (closestBlock.x - mc.thePlayer.posX).pow(2.0) + (closestBlock.z - mc.thePlayer.posZ).pow(
                                2.0

                            )
                        )
                    )

                ).toFloat()
            }
        }
    }


    fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld.getCollisionBoxes(
                mc.thePlayer.entityBoundingBox.offset(
                    mc.thePlayer.motionX * 1.4,
                    i.toDouble(),
                    mc.thePlayer.motionZ * 1.4
                )
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }

    fun RightClick(pressed: Boolean) {
        mc.gameSettings.keyBindUseItem.pressed = pressed
    }

    fun LeftClick(pressed: Boolean) {
        mc.gameSettings.keyBindAttack.pressed = pressed
    }

    fun Jump(pressed: Boolean) {
        mc.gameSettings.keyBindJump.pressed = pressed
    }

    fun Sneak(pressed: Boolean) {
        mc.gameSettings.keyBindSneak.pressed = pressed
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
