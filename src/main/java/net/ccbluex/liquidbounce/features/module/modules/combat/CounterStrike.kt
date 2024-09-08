/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.drawText
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.random.Random

@ModuleInfo(name = "CounterStrike", category = ModuleCategory.COMBAT)
class CounterStrike : Module() {
    var kills = 0
    private val text = TextValue("Text","win")
    private val teamT = TextValue("TeamT","T")
    private val teamCT = TextValue("TeamCT","CT")
    private val start = TextValue("Start","start")
    private val exchangeTeams = TextValue("ExchangeTeams","ExchangeTeams")
    private val resetTitle = TextValue("ResetTitle","ResetTitle")
    private val musicMode = ListValue("MusicMode", arrayOf("Random","Custom"),"Random")
    private val musicNumber = IntegerValue("MusicNumber",1,1,9).displayable{musicMode.get() == "Custom"}
    private val ticks = IntegerValue("Tick",1000,100,2000)
    private val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
    private val screenWidth = scaledResolution.scaledWidth
    private val screenHeight = scaledResolution.scaledHeight
    private val referenceWidth = screenWidth.toDouble()
    private val referenceHeight = screenHeight.toDouble()
    private val vx = ((226.0 / referenceWidth) * screenWidth).toInt()
    private val vy = ((1.0 / referenceHeight) * screenHeight).toInt()
    private val vx2 = ((226.0 / referenceWidth) * screenWidth).toInt()
    private val vy2 = ((12.0 / referenceHeight) * screenHeight).toInt()
    private val vx3 = ((238.0 / referenceWidth) * screenWidth).toInt()
    private val vy3 = ((12.0 / referenceHeight) * screenHeight).toInt()
    private val tx = ((230.0 / referenceWidth) * screenWidth).toInt()
    private val ty = ((5.0 / referenceHeight) * screenHeight).toInt()
    private val tx2 = ((229.0 / referenceWidth) * screenWidth).toInt()
    private val ty2 = ((17.0 / referenceHeight) * screenHeight).toInt()
    private val tx3 = ((242.0 / referenceWidth) * screenWidth).toInt()
    private val ty3 = ((17.0 / referenceHeight) * screenHeight).toInt()
    private val x = ((130.0 / referenceWidth) * screenWidth).toInt()
    private val x2 = ((130.0 / referenceWidth) * screenWidth).toInt()
    private val y = ((16.0 / referenceHeight) * screenHeight).toInt()
    private val y2 = ((24.0 / referenceHeight) * screenHeight).toInt()
    private val textX = ((202.0 / referenceWidth) * screenWidth).toInt()
    private val textY = ((19.0 / referenceHeight) * screenHeight).toInt()
    private val MVPX = ((155.0 / referenceWidth) * screenWidth).toInt()
    private val MVPY = ((28.0 / referenceHeight) * screenHeight).toInt()
    private val imgX = ((310.0 / referenceWidth) * screenWidth).toInt()
    private val imgY = ((25.0 / referenceHeight) * screenHeight).toInt()
    private val silverX = ((132.0 / referenceWidth) * screenWidth).toInt()
    private val silverY = ((37.0 / referenceHeight) * screenHeight).toInt()
    private val soundTextX = ((155.0 / referenceWidth) * screenWidth).toInt()
    private val soundTextY = ((39.0 / referenceHeight) * screenHeight).toInt()
    private val vw = 22
    private val vh = 10
    private val vw2 = 10
    private val vh2 = 15
    private val vw3 = 10
    private val vh3 = 15
    private val r = 25
    private val g = 24
    private val b = 28
    private val r2 = 8
    private val g2 = 8
    private val b2 = 0
    private val a = 212
    private var twinValue = 0
    private var ctwinValue = 0
    private var tmpT = 0
    private var tmpCT = 0
    private val width1 = 200
    private val height = 8
    private val height2 = 20
    private val imgWidth = 17
    private val imgHeight =17
   private val silverWidth = 9
    private val silverHeight = 5
    private var seconds = 0
    private var s =0
    private var sound = ""
    private var ctw = false
    private var tw = false
    private var tick = 0
    private var alpha = 0
    private var time = 0
    private var timerSecounds = 0
    private var timerMinute = 0
    private var timerTick = 0
    private var isStart = false
    private val imgT = ResourceLocation("leaf/T.png")
    private val imgCT = ResourceLocation("leaf/CT.png")
    private val imgsilver1 = ResourceLocation("leaf/silver1.png")
    private val imgsilver2 = ResourceLocation("leaf/silver2.png")
    private val imgsilver3 = ResourceLocation("leaf/silver3.png")
    private val imgsilver4 = ResourceLocation("leaf/silver4.png")
    private val imgsilver5 = ResourceLocation("leaf/silver5.png")
    override fun onDisable() {
        s= 0
        timerTick = 0
        timerMinute = 0
        timerSecounds = 0
        tw = false
        ctw = false
        tick = 0
        alpha = 0
        kills = 0
        sound = ""
        ctwinValue = 0
        twinValue = 0
        time = 0
        tmpT = 0
        tmpCT = 0
        seconds = 0
        isStart = false
    }




    @EventTarget
    fun onRender(event: Render2DEvent) {

        drawBlackPanel4(vx.toDouble(),vy.toDouble(),vw.toDouble(),vh.toDouble())
        drawBlackPanel3(vx2.toDouble(),vy2.toDouble(),vw2.toDouble(),vh2.toDouble())
        drawBlackPanel3(vx3.toDouble(),vy3.toDouble(),vw3.toDouble(),vh3.toDouble())
        drawText3("$timerMinute:$timerSecounds", tx, ty, 255,255,255)
        drawText3(ctwinValue.toString(), tx2, ty2, 191,215,234)
        drawText3(twinValue.toString(), tx3, ty3, 228,224,175)

        if (alpha>=a)alpha = a
        if (ctw) {
            if (alpha <= 0)alpha=0
            if (tick<ticks.get()) {
                tick++
                alpha += 2
                drawBlackPanel2(x2.toDouble(), y2.toDouble(), width1.toDouble(), height2.toDouble())
                drawBlackPanel(x.toDouble(), y.toDouble(), width1.toDouble(), height.toDouble())

                renderImage(imgCT, imgX, imgY, imgWidth, imgHeight)
                drawText4("Counter-Terrorists Win", textX, textY, 191,215,234)
                drawText5("KillStreak: You killed $kills players in the round", MVPX, MVPY, 191,215,234)
                drawText5("Now Playing: $sound", soundTextX, soundTextY, 191,215,234)
                when(kills) {
                    1 -> {
                        renderImage(imgsilver1, silverX, silverY, silverWidth, silverHeight)
                    }
                    2 -> {
                        renderImage(imgsilver2, silverX, silverY, silverWidth, silverHeight)
                    }
                    3 -> {
                        renderImage(imgsilver3, silverX, silverY, silverWidth, silverHeight)
                    }
                    4 -> {
                        renderImage(imgsilver4, silverX, silverY, silverWidth, silverHeight)
                    }
                    5 ->{
                        renderImage(imgsilver5, silverX, silverY, silverWidth, silverHeight)
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
            if (tick<ticks.get()) {
                alpha += 2
                tick++
                drawBlackPanel2(x2.toDouble(), y2.toDouble(), width1.toDouble(), height2.toDouble())
                drawBlackPanel(x.toDouble(), y.toDouble(), width1.toDouble(), height.toDouble())
                renderImage(imgT, imgX, imgY, imgWidth, imgHeight)
                drawText4("Terrorists Win", textX, textY, 191,215,234)
                drawText5("KillStreak: You killed $kills players in the round", MVPX, MVPY, 191,215,234)
                drawText5("Now Playing: $sound", soundTextX, soundTextY, 191,215,234)
                when(kills) {
                    1 -> {
                        renderImage(imgsilver1, silverX, silverY, silverWidth, silverHeight)
                    }
                    2 -> {
                        renderImage(imgsilver2, silverX, silverY, silverWidth, silverHeight)
                    }
                    3 -> {
                        renderImage(imgsilver3, silverX, silverY, silverWidth, silverHeight)
                    }
                    4 -> {
                        renderImage(imgsilver4, silverX, silverY, silverWidth, silverHeight)
                    }
                    5 ->{
                        renderImage(imgsilver5, silverX, silverY, silverWidth, silverHeight)
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
    fun Update (event:UpdateEvent) {
        if (isStart){
            if (timerTick < 20)timerTick++
            if (timerTick == 20){
                timerTick = 0
                timerSecounds --
            }
            if (timerMinute == 0 && timerSecounds == 0){
                timerMinute = 0
                timerSecounds = 0
                isStart = false
            }
            if (timerSecounds == 0){
                timerSecounds = 60
                timerMinute --
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val playerArmor = mc.thePlayer.inventory.armorInventory[3]
        val myItemArmor = playerArmor.item as ItemArmor
        val packet = event.packet


        if (event.packet is S45PacketTitle) {
            val titlePacket = event.packet as S45PacketTitle
            val message = titlePacket.message.unformattedText

            if (message.contains(exchangeTeams.get(), ignoreCase = true)) {
                tmpT = twinValue
                tmpCT = ctwinValue
                twinValue = tmpCT
                ctwinValue = tmpT
                tmpT = 0
                tmpCT = 0
            }
            if (message.contains(start.get(), ignoreCase = true)) {
                isStart = true
                timerSecounds = 59
                timerMinute = 1
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

        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            val regex = Regex(mc.thePlayer.name+" - (\\d+) kills")

            val matchResult = regex.find(message)

            if (matchResult != null) {
                 kills = matchResult.groupValues[1].toInt()

            } else {

            }
            if (message.contains(resetTitle.get(), ignoreCase = true)) {
                ctwinValue = 0
                twinValue =0
            }
            if (message.contains(text.get()+teamCT.get())){
                ctwinValue ++
            }
            if (message.contains(text.get()+teamT.get())){
                twinValue ++
            }
            if (message.contains(text.get()+teamCT.get())&&
                (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON || myItemArmor.getColor(playerArmor) == 0x0000FF)) {
              ctw = true
                isStart = false
                if (musicMode.get() == "Random") {
                    when (Random.nextInt(1, 10)) {
                        1 -> {
                            LiquidBounce.tipSoundManager.winSound.asyncPlay()
                            sound = "EZ4ENCE"
                        }

                        2 -> {
                            LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                            sound = "dashstar"
                        }

                        3 -> {
                            LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                            sound = "The Good Youth"
                        }

                        4 -> {
                            LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                            sound = "inhuman"
                        }

                        5 -> {
                            LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                            sound = "Heading for the Source"
                        }

                        6 -> {
                            LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                            sound = "The Lowlife Pack"
                        }

                        7 -> {
                            LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                            sound = "Under Bright Lights"
                        }

                        8 -> {
                            LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                            sound = "ULTIMATE"
                        }

                        9 -> {
                            LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                            sound = "I Am"
                        }
                    }
                }else{
                    when (musicNumber.get()) {
                        1 -> {
                            LiquidBounce.tipSoundManager.winSound.asyncPlay()
                            sound = "EZ4ENCE"
                        }

                        2 -> {
                            LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                            sound = "dashstar"
                        }

                        3 -> {
                            LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                            sound = "The Good Youth"
                        }

                        4 -> {
                            LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                            sound = "inhuman"
                        }

                        5 -> {
                            LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                            sound = "Heading for the Source"
                        }

                        6 -> {
                            LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                            sound = "The Lowlife Pack"
                        }

                        7 -> {
                            LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                            sound = "Under Bright Lights"
                        }

                        8 -> {
                            LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                            sound = "ULTIMATE"
                        }

                        9 -> {
                            LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                            sound = "I Am"
                        }
                    }

                }

            }
            if (message.contains(text.get()+teamT.get())&&
                (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN || myItemArmor.getColor(playerArmor) == 0xFF0000)) {
                tw = true
                isStart = false
                if (musicMode.get() == "Random") {
                    when (Random.nextInt(1, 10)) {
                        1 -> {
                            LiquidBounce.tipSoundManager.winSound.asyncPlay()
                            sound = "EZ4ENCE"

                        }

                        2 -> {
                            LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                            sound = "dashstar"
                        }

                        3 -> {
                            LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                            sound = "The Good Youth"
                        }

                        4 -> {
                            LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                            sound = "inhuman"
                        }

                        5 -> {
                            LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                            sound = "Heading for the Source"
                        }

                        6 -> {
                            LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                            sound = "The Lowlife Pack"
                        }

                        7 -> {
                            LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                            sound = "Under Bright Lights"
                        }

                        8 -> {
                            LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                            sound = "ULTIMATE"
                        }

                        9 -> {
                            LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                            sound = "I Am"
                        }
                    }
                }else{
                    when (musicNumber.get()) {
                        1 -> {
                            LiquidBounce.tipSoundManager.winSound.asyncPlay()
                            sound = "EZ4ENCE"
                        }

                        2 -> {
                            LiquidBounce.tipSoundManager.winSound2.asyncPlay()
                            sound = "dashstar"
                        }

                        3 -> {
                            LiquidBounce.tipSoundManager.winSound3.asyncPlay()
                            sound = "The Good Youth"
                        }

                        4 -> {
                            LiquidBounce.tipSoundManager.winSound4.asyncPlay()
                            sound = "inhuman"
                        }

                        5 -> {
                            LiquidBounce.tipSoundManager.winSound5.asyncPlay()
                            sound = "Heading for the Source"
                        }

                        6 -> {
                            LiquidBounce.tipSoundManager.winSound6.asyncPlay()
                            sound = "The Lowlife Pack"
                        }

                        7 -> {
                            LiquidBounce.tipSoundManager.winSound7.asyncPlay()
                            sound = "Under Bright Lights"
                        }

                        8 -> {
                            LiquidBounce.tipSoundManager.winSound8.asyncPlay()
                            sound = "ULTIMATE"
                        }

                        9 -> {
                            LiquidBounce.tipSoundManager.winSound9.asyncPlay()
                            sound = "I Am"
                        }
                    }

                }

            }

        }
    }
    private fun drawText3(text: String, x: Int, y: Int, r: Int, g: Int, b: Int) {
        val fontRenderer: FontRenderer = Fonts.font28
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val color = (r shl 16) or (g shl 8) or b
        fontRenderer.drawString(text, x, y, color)

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    private fun drawText4(text: String, x: Int, y: Int, rv: Int, gv: Int, bv: Int) {
        val fontRenderer: FontRenderer = Fonts.font28
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val color = (rv shl 16) or (gv shl 8) or bv
        fontRenderer.drawString(text, x, y, color)

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }
    private fun drawText5(text: String, x: Int, y: Int, rv: Int, gv: Int, bv: Int) {
        val fontRenderer: FontRenderer = Fonts.SFUI35
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val color = (rv shl 16) or (gv shl 8) or bv
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

    private fun drawBlackPanel3(x: Double, y: Double, width: Double, height: Double) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)


        val steps = 100
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


    private fun drawBlackPanel4(x: Double, y: Double, width: Double, height: Double) {

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r.toByte(), g.toByte(), b.toByte(), 156.toByte())

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
    private fun drawRoundedPanel(x: Double, y: Double, width: Double, height: Double, radius: Double) {
        val segments = 20 // Number of segments for the arc

        // Draw the rectangle with rounded corners
        drawRectWithRoundedCorners(x, y, width, height, radius, segments)
    }

    private fun drawRectWithRoundedCorners(x: Double, y: Double, width: Double, height: Double, radius: Double, segments: Int) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r.toByte(), g.toByte(), b.toByte(), 156.toByte())

        // Draw the center rectangle
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x + radius, y)
        GL11.glVertex2d(x + width - radius, y)
        GL11.glVertex2d(x + width - radius, y + height)
        GL11.glVertex2d(x + radius, y + height)
        GL11.glEnd()

        // Draw the top left corner arc
        drawArc(x + radius, y + radius, radius, 180.0, 270.0, segments)

        // Draw the top right corner arc
        drawArc(x + width - radius, y + radius, radius, 270.0, 360.0, segments)

        // Draw the bottom left corner arc
        drawArc(x + radius, y + height - radius, radius, 90.0, 180.0, segments)

        // Draw the bottom right corner arc
        drawArc(x + width - radius, y + height - radius, radius, 0.0, 90.0, segments)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    private fun drawArc(centerX: Double, centerY: Double, radius: Double, startAngle: Double, endAngle: Double, segments: Int) {
        val angleStep = (endAngle - startAngle) / segments

        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glVertex2d(centerX, centerY)

        for (i in 0..segments) {
            val angle = Math.toRadians(startAngle + angleStep * i)
            val x = centerX + radius * Math.cos(angle)
            val y = centerY + radius * Math.sin(angle)
            GL11.glVertex2d(x, y)
        }
        GL11.glEnd()
    }

    private fun drawBlackPanel(x: Double, y: Double, width: Double, height: Double) {

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
    private fun drawBlackPanel2(x: Double, y: Double, width: Double, height: Double) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4ub(r2.toByte(), g2.toByte(), b2.toByte(), alpha.toByte())

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