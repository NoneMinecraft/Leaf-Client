/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer
import net.ccbluex.liquidbounce.utils.extensions.skin
import net.ccbluex.liquidbounce.utils.render.GLUtils.drawImage
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.audio.SoundHandler
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.random.Random

@ModuleInfo(name = "Sound", category = ModuleCategory.COMBAT)
class Sound : Module() {
    var kills = 0
    private val text = TextValue("Text","win")
    private val teamT = TextValue("TeamT","T")
    private val teamCT = TextValue("TeamCT","CT")
    private val start = TextValue("Start","start")
    private val r = IntegerValue("r",0,0,255)
    private val g = IntegerValue("g",0,0,255)
    private val b = IntegerValue("b",0,0,255)
    private val r2 = IntegerValue("r2",0,0,255)
    private val g2 = IntegerValue("g2",0,0,255)
    private val b2 = IntegerValue("b2",0,0,255)
    private val a = IntegerValue("a",0,0,255)
    private val a2 = IntegerValue("a2",0,0,255)
    private val x = IntegerValue("x",100,0,255)
    private val x2 = IntegerValue("x2",100,0,255)
    private val y =  IntegerValue("y",58,0,255)
    private val y2 = IntegerValue("y2",50,0,255)
    private val width1 = IntegerValue("width1",200,0,255)
    private val height = IntegerValue("height",21,0,255)
    private val height2 = IntegerValue("height2",8,0,255)
    private val textX = IntegerValue("textX",121,0,200)
    private val textY =IntegerValue("textY",50,0,200)
    private val MVPX = IntegerValue("MVPX",228,0,400)
    private val MVPY = IntegerValue("MVPY",228,0,400)
    private val imgT = ResourceLocation("leaf/T.png")
    private val imgCT = ResourceLocation("leaf/CT.png")
    private val imgsilver1 = ResourceLocation("leaf/silver1.png")
    private val imgsilver2 = ResourceLocation("leaf/silver2.png")
    private val imgsilver3 = ResourceLocation("leaf/silver3.png")
    private val imgsilver4 = ResourceLocation("leaf/silver4.png")
    private val imgsilver5 = ResourceLocation("leaf/silver5.png")
    private val imgX = IntegerValue("imgX",228,0,400)
    private val imgY = IntegerValue("imgY",60,0,400)
    private val imgWidth = IntegerValue("imgWidth",13,0,400)
    private val imgHeight = IntegerValue("imgHeight",13,0,400)
    private val silverX = IntegerValue("silverX",228,0,400)
    private val silverY = IntegerValue("silverY",60,0,400)
    private val silverWidth = IntegerValue("silverWidth",13,0,400)
    private val silverHeight = IntegerValue("silverHeight",13,0,400)
    private val soundTextX = IntegerValue("soundTextX",155,0,200)
    private val soundTextY =IntegerValue("soundTextY",48,0,200)
    var sound = ""
    var ctw = false
    var tw = false
    var tick = 0
    var alpha = 0
    override fun onDisable() {
        tw = false
        ctw = false
        tick = 0
        alpha = 0
        kills = 0
        sound = ""
    }




    @EventTarget
    fun onRender(event: Render2DEvent) {
        if (alpha>=a.get())alpha = a.get()
        if (ctw) {
            if (alpha <= 0)alpha=0
            if (tick > 1700){
                alpha -= 2
            }
            if (tick<1800) {
                tick++
                alpha += 2
                drawBlackPanel2(x2.get().toDouble(), y2.get().toDouble(), width1.get().toDouble(), height2.get().toDouble())
                drawBlackPanel(x.get().toDouble(), y.get().toDouble(), width1.get().toDouble(), height.get().toDouble())
                drawText("Counter-Terrorists Win", textX.get(), textY.get(), -1)
                drawText2("KillStreak: You killed $kills players in the round", MVPX.get(), MVPY.get(), -1)
                drawText2("Now Playing: $sound", soundTextX.get(), soundTextY.get(), -1)
                renderImage(imgCT, imgX.get(), imgY.get(), imgWidth.get(), imgHeight.get())
                when(kills) {
                    1 -> {
                        renderImage(imgsilver1, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    2 -> {
                        renderImage(imgsilver2, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    3 -> {
                        renderImage(imgsilver3, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    4 -> {
                        renderImage(imgsilver4, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    5 ->{
                        renderImage(imgsilver5, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                }

            }else{
                ctw = false
                tick = 0
                alpha = 0
            }
        }
        if (tw) {
            if (alpha <= 0)alpha=0
            if (tick > 1700){
                alpha -= 2
            }

            if (tick<1800) {
                alpha += 2
                tick++
                drawBlackPanel2(x2.get().toDouble(), y2.get().toDouble(), width1.get().toDouble(), height2.get().toDouble())
                drawBlackPanel(x.get().toDouble(), y.get().toDouble(), width1.get().toDouble(), height.get().toDouble())
                drawText("Terrorists Win", textX.get(), textY.get(), -1)
                drawText2("KillStreak: You killed $kills players in the round", MVPX.get(), MVPY.get(), -1)
                drawText2("Now Playing: $sound", soundTextX.get(), soundTextY.get(), -1)
                renderImage(imgT, imgX.get(), imgY.get(), imgWidth.get(), imgHeight.get())
                when(kills) {
                    1 -> {

                        renderImage(imgsilver1, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    2 -> {
                        renderImage(imgsilver2, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    3 -> {
                        renderImage(imgsilver3, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    4 -> {
                        renderImage(imgsilver4, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                    5 ->{
                        renderImage(imgsilver5, silverX.get(), silverY.get(), silverWidth.get(), silverHeight.get())
                    }
                }
            }else{
                tw = false
                tick = 0
                alpha = 0
            }
        }
        }





    @EventTarget
    fun onPacket(event: PacketEvent) {
        val playerArmor = mc.thePlayer.inventory.armorInventory[3]
        val myItemArmor = playerArmor.item as ItemArmor
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            val regex = Regex(mc.thePlayer.name+" - (\\d+) kills")

            val matchResult = regex.find(message)

            if (matchResult != null) {
                 kills = matchResult.groupValues[1].toInt()

            } else {

            }
            if (message.contains(text.get()+teamCT.get())&&
                (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON || myItemArmor.getColor(playerArmor) == 0x0000FF)) {
              ctw = true
                when (Random.nextInt(1, 10)) {
                    1 -> {
                        LiquidBounce.tipSoundManager.winSound.asyncPlay()
                        sound = "EZ4ENCE" }
                    2 -> {
                        LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                        sound = "dashstar" }
                    3 -> {
                        LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                        sound= "The Good Youth" }
                    4 -> {
                        LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                        sound= "inhuman" }
                    5 -> {
                        LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                        sound= "Heading for the Source" }
                    6 -> {
                        LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                        sound= "The Lowlife Pack" }
                    7 -> {
                        LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                        sound= "Under Bright Lights" }
                    8 -> {
                        LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                        sound = "ULTIMATE" }
                    9 -> {
                        LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                        sound = "I Am"
                    }
                }

            }
            if (message.contains(text.get()+teamT.get())&&
                (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN || myItemArmor.getColor(playerArmor) == 0xFF0000)) {
                tw = true
                when (Random.nextInt(1, 10)) {
                    1 -> {
                        LiquidBounce.tipSoundManager.winSound.asyncPlay()
                        sound = "EZ4ENCE" }
                    2 -> {
                        LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                        sound = "dashstar" }
                    3 -> {
                        LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                        sound= "The Good Youth" }
                    4 -> {
                        LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                        sound= "inhuman" }
                    5 -> {
                        LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                        sound= "Heading for the Source" }
                    6 -> {
                        LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                        sound= "The Lowlife Pack" }
                    7 -> {
                        LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                        sound= "Under Bright Lights" }
                    8 -> {
                        LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                        sound = "ULTIMATE" }
                    9 -> {
                        LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                        sound = "I Am"
                    }
                }

            }
            if (message.contains(start.get())){
                when (Random.nextInt(1, 10)) {
                    1 -> {
                        LiquidBounce.tipSoundManager.startSound.asyncPlay()
                    }
                    2 -> {
                        LiquidBounce.tipSoundManager.startSound2.asyncPlay()
                    }
                    3 -> {
                        LiquidBounce.tipSoundManager.startSound3.asyncPlay()
                    }
                    4 -> {
                        LiquidBounce.tipSoundManager.startSound4.asyncPlay()
                    }
                    5 -> {
                        LiquidBounce.tipSoundManager.startSound5.asyncPlay()
                    }
                    6 -> {
                        LiquidBounce.tipSoundManager.startSound6.asyncPlay()
                    }
                    7 -> {
                        LiquidBounce.tipSoundManager.startSound7.asyncPlay()
                    }
                    8 -> {
                        LiquidBounce.tipSoundManager.startSound8.asyncPlay()
                    }
                    9 -> {
                        LiquidBounce.tipSoundManager.startSound9.asyncPlay()
                    }
                }

            }
        }
    }
    fun renderPlayerSkinHead(player: EntityPlayer, x: Int, y: Int, width: Int, height: Int) {
        // 获取玩家的皮肤资源
        val skin: ResourceLocation = player.skin

        // 获取Minecraft实例
        val mc: Minecraft = Minecraft.getMinecraft()
        val textureManager: TextureManager = mc.textureManager

        // 绑定皮肤纹理
        textureManager.bindTexture(skin)

        // 设置OpenGL状态
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        // 控制渲染大小和位置
        GlStateManager.translate(x.toFloat(), y.toFloat(), 0f)
        GlStateManager.scale(width / 64.0f, height / 64.0f, 1.0f)

        // 渲染玩家的头部区域（8x8像素），头部在皮肤图的 (8, 8) 到 (16, 16)
        mc.ingameGUI.drawTexturedModalRect(0, 0, 8, 8, 8, 8)

        // 恢复OpenGL状态
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }
    private fun drawText(text: String, x: Int, y: Int, color: Int) {
        val fontRenderer: FontRenderer =  Fonts.font28
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        fontRenderer.drawString(text, x, y, color)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }
    private fun drawText2(text: String, x: Int, y: Int, color: Int) {
        val fontRenderer: FontRenderer =  Fonts.SFUI35
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        fontRenderer.drawString(text, x, y, color)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    private fun renderImage(resourceLocation: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        mc.textureManager.bindTexture(resourceLocation)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
        GL11.glDisable(GL11.GL_BLEND)
    }


    private fun drawBlackPanel(x: Double, y: Double, width: Double, height: Double) {

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r.get().toByte(), g.get().toByte(), b.get().toByte(), alpha.toByte())

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
    private fun drawBlackPanel2(x: Double, y: Double, width: Double, height: Double) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r2.get().toByte(), g2.get().toByte(), b2.get().toByte(), alpha.toByte())

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
}