/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.rage

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.MainLib
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.rage.rage.render.drawPanel
import net.nonemc.leaf.features.module.modules.rage.rage.render.drawText
import net.nonemc.leaf.features.module.modules.rage.rage.search.findItem
import net.nonemc.leaf.ui.font.Fonts
import net.nonemc.leaf.utils.render.RenderUtils
import net.nonemc.leaf.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.System.currentTimeMillis

@ModuleInfo(name = "CounterStrike", category = ModuleCategory.Rage)
class CounterStrike : Module() {
    private val text = TextValue("RenderTrigger-Victory","Victory")
    private val dieText = TextValue("RenderTrigger-Die","Died")
    private val teamT = TextValue("RenderTrigger-TeamT","T")
    private val teamCT = TextValue("RenderTrigger-TeamCT","CT")
    private val c4A = TextValue("RenderTrigger-C4A","The bomb was placed in A")
    private val c4B = TextValue("RenderTrigger-C4B","The bomb was placed in B")
    private val start = TextValue("RenderTrigger-Start","start")
    private val exchangeTeams = TextValue("RenderTrigger-ExchangeTeams","Swap")
    private val resetTitle = TextValue("RenderTrigger-GameOver","GameOver")
    private val musicMode = ListValue("MvpMusicMode", arrayOf("Random","Custom"),"Random")
    private val musicNumber = IntegerValue("MvpMusicNumber",1,1,9).displayable{musicMode.get() == "Custom"}
    private val ticks = IntegerValue("Mvp-RenderTick",1000,100,2000)
    private val Xoffset = FloatValue("InventoryOffsetX", 0f, -200f, 200f)
    private val YOffset = FloatValue("InventoryOffsetY", 0f, -200f, 200f)
    private val Xoffset2 = FloatValue("ScoreboardOffsetX", 0f, -200f, 200f)
    private val Yoffset2 = FloatValue("ScoreboardOffsetY", 0f, -200f, 200f)
    private val Xoffset3 = FloatValue("BombPanelOffsetX", 0f, -200f, 200f)
    private val Yoffset3 = FloatValue("BombPanelOffsetY", 0f, -200f, 200f)
    private val colorRedValue = IntegerValue("Cross-Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Cross-Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Cross-Blue", 255, 0, 255)
    private val colorAlphaValue = IntegerValue("Cross-Alpha", 255, 0, 255)
    private val widthValue = FloatValue("Cross-Width", 0.5f, 0.25f, 10f)
    private val sizeValue = FloatValue("Cross-Length", 7f, 0.25f, 15f)
    private val crossSpeed = FloatValue("Cross-Speed", 0.1f, 0.01f, 1f)
    private val gapValue = FloatValue("Cross-Gap", 5f, 0.25f, 15f)
    private val stepCross = IntegerValue("Cross-Step", 10, 1, 40)
    private val dynamicValue = BoolValue("Cross-Dynamic", true)
    private var kills = 0
    private var startTime = 0L
    private var countdownSeconds = 45
    private val propx = 468.0
    private val propy = 240.0
    private val propx2 = 443.0
    private val propy2 = 207.0
    private val propx4 = 453.0
    private val propy4 = 234.0
    private val propx5 = 450.0
    private val propy5 = 226.0
    private val xxx =-25
    private val yyy =-53
    private val vxx = -37
    private val vyy = -62
    private val vxx2 =-12
    private val vyy2 =-57
    private val vxx3 =-12
    private val vyy3 =-50
    private val vx = 226.0
    private val vy = 1.0
    private val vx2 = 226.0
    private val vy2 = 12.0
    private val vx3 = 238.0
    private val vy3 = 12.0
    private val tx = 230.0
    private val ty = 5.0
    private val tx2 = 229.0
    private val ty2 = 17.0
    private val tx3 = 242.0
    private val ty3 = 17.0
    private val x = 130.0
    private val x2 = 130.0
    private val y = 16.0
    private val y2 = 24.0
    private val textX = 202.0
    private val textY = 19.0
    private val MVPX = 155.0
    private val MVPY = 28.0
    private val imgX = 310.0
    private val imgY = 25.0
    private val silverX = 132.0
    private val silverY = 37.0
    private val soundTextX = 155.0
    private val soundTextY = 39.0


    private val propw = 8F
    private val proph = 11F
    private val propw2 = 30F
    private val proph2 = 11F
    private val propw3 = 25F
    private val proph3 = 7F
    private val propw4 = 20F
    private val proph4 = 11F
    private val xp = 3F
    private val radiuss = 6.56F
    private val vhh =18
    private val vww = 70
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
    private var soundName = ""
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
    private val prop1 = ResourceLocation("leaf/prop1.png")
    private val prop2 = ResourceLocation("leaf/prop2.png")
    private val prop6 = ResourceLocation("leaf/prop6.png")
    private val prop3 = ResourceLocation("leaf/prop3.png")
    private val prop4 = ResourceLocation("leaf/prop4.png")
    private val prop7 = ResourceLocation("leaf/prop7.png")
    private val m4 = ResourceLocation("leaf/M4.png")
    private val ak = ResourceLocation("leaf/AK.png")
    private val p2000 = ResourceLocation("leaf/p2000.png")
    private val DesertEagle = ResourceLocation("leaf/DesertEagle.png")
    private val sword = ResourceLocation("leaf/sword.png")
    private val m42 = ResourceLocation("leaf/M42.png")
    private val ak2 = ResourceLocation("leaf/AK2.png")
    private val p20002 = ResourceLocation("leaf/p20002.png")
    private val DesertEagle2 = ResourceLocation("leaf/DesertEagle2.png")
    private val sword2 = ResourceLocation("leaf/sword2.png")
    private var isRenderA = false
    private var isRenderB = false
    var Select1 = false
    var Select2 = false
    var Select3 = false
    var can = false
    var all = 0
    var die = 0
    var kd = 0.0
    override fun onDisable() {
        Select3 = false
        Select2 = false
        Select1 = false
        can = false
        kd = 0.0
        die = 0
        all = 0
        isRenderA = false
        isRenderB = false
        s= 0
        timerTick = 0
        timerMinute = 0
        timerSecounds = 0
        tw = false
        ctw = false
        tick = 0
        alpha = 0
        kills = 0
        soundName = ""
        ctwinValue = 0
        twinValue = 0
        time = 0
        tmpT = 0
        tmpCT = 0
        seconds = 0
        isStart = false
        startTime = 0L
        countdownSeconds = 40
    }
    override fun onEnable() {
        startTime = currentTimeMillis()
    }
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!can) {
            kd = all.toDouble() / die.toDouble()
            can = true
        }
        if (alpha >= a) alpha = a
        val screenWidth = event.scaledResolution.scaledWidth / 2
        val screenHeight = event.scaledResolution.scaledHeight / 2
        renderCross(screenWidth, screenHeight,stepCross.get())
        renderScoreboard()
        renderC4()
        renderCTWin()
        renderTWin()
        renderHotbar()
    }

    @EventTarget
    fun onUpdate(event:UpdateEvent) {
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
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            if (message.contains(text.get()+teamCT.get())){
                ctwinValue ++
            }
            if (message.contains(text.get()+teamT.get())){
                twinValue ++
            }
        }
        val playerArmor = mc.thePlayer?.inventory?.armorInventory?.get(3) ?:return
        val myItemArmor = playerArmor.item as? ItemArmor

        if (event.packet is S45PacketTitle) {
            val titlePacket = event.packet as S45PacketTitle
            val message = titlePacket.message?.unformattedText ?: ""
            if (message.contains(dieText.get(), ignoreCase = true)){
                die ++
            }
            if (message.contains(c4A.get(), ignoreCase = true)&&!isRenderA) {
                startTime = currentTimeMillis()
                isRenderA = true
            }
            if (message.contains(c4B.get(), ignoreCase = true)&&!isRenderB) {
                startTime = currentTimeMillis()
                isRenderB = true
            }
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
                can = false
            }
        }

        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            val regex = Regex(mc.thePlayer.name + " - (\\d+) kills")
            val matchResult = regex.find(message)

            if (matchResult != null) {
                kills = matchResult.groupValues[1].toInt()
            }

            if (message.contains(resetTitle.get(), ignoreCase = true)) {
                ctwinValue = 0
                twinValue = 0
            }
            val isTeamCT = message.contains(text.get() + teamCT.get())
            val isTeamT = message.contains(text.get() + teamT.get())
            val armorColorCT = myItemArmor?.getColor(playerArmor) == 0x0000FF
            val armorColorT = myItemArmor?.getColor(playerArmor) == 0xFF0000
            val isIronArmor = myItemArmor?.armorMaterial == ItemArmor.ArmorMaterial.IRON
            val isChainArmor = myItemArmor?.armorMaterial == ItemArmor.ArmorMaterial.CHAIN
            if (isTeamCT && (isIronArmor || armorColorCT)) {playWinSound(musicMode.get(), musicNumber.get())
                ctw = true
            } else if (isTeamT && (isChainArmor || armorColorT)){ playWinSound(musicMode.get(), musicNumber.get())
                tw = true
            }
        }
    }
    fun findM4(): Int = findItem(Items.iron_hoe)
    fun findAK(): Int = findItem(Items.stone_hoe)
    fun p2000(): Int = findItem(Items.wooden_pickaxe)
    fun findDesertEagle(): Int = findItem(Items.golden_pickaxe)
    fun findSword(): Int {
        val axeIndex = findItem(Items.iron_axe)
        if (axeIndex != -1) return axeIndex
        return findItem(Items.stone_axe)
    }
    fun findProp(): Int = findItem(Items.blaze_powder)
    fun findProp2(): Int = findItem(Items.carrot)
    fun findProp3(): Int = findItem(Items.potato)
    var length = 0.0F

    private fun renderCross(screenWidth: Int, screenHeight: Int, step : Int) {
        val player = mc.thePlayer
        if (!player.isSneaking){
            length = 0.0F
        }else{
            if (length < sizeValue.get()){
                length += crossSpeed.get()
            }
        }
        val movementSpeed = Math.sqrt((player.motionX * player.motionX + player.motionZ * player.motionZ).toDouble()).toFloat()
        val gap = if (dynamicValue.get()) gapValue.get() + movementSpeed * 5 else gapValue.get()
        val red = colorRedValue.get() / 255f
        val green = colorGreenValue.get() / 255f
        val blue = colorBlueValue.get() / 255f
        val alphaBase = colorAlphaValue.get() / 255f

        val steps = step
        val size = length

        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        for (i in 0 until steps) {
            val alpha = (alphaBase * (1.0 - i.toDouble() / steps)).toFloat()
            GL11.glColor4f(red, green, blue, alpha)

            GL11.glLineWidth(widthValue.get())
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap - (i * size / steps)).toFloat())
            GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap - ((i + 1) * size / steps)).toFloat())
            GL11.glEnd()

            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap + (i * size / steps)).toFloat())
            GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap + ((i + 1) * size / steps)).toFloat())
            GL11.glEnd()
        }
        for (i in 0 until steps) {
            val alpha = (alphaBase * (1.0 - i.toDouble() / steps)).toFloat()
            GL11.glColor4f(red, green, blue, alpha)
            GL11.glLineWidth(widthValue.get())
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex2f((screenWidth - gap - (i * size / steps)).toFloat(), screenHeight.toFloat())
            GL11.glVertex2f((screenWidth - gap - ((i + 1) * size / steps)).toFloat(), screenHeight.toFloat())
            GL11.glEnd()
            GL11.glBegin(GL11.GL_LINES)
            GL11.glVertex2f((screenWidth + gap + (i * size / steps)).toFloat(), screenHeight.toFloat())
            GL11.glVertex2f((screenWidth + gap + ((i + 1) * size / steps)).toFloat(), screenHeight.toFloat())
            GL11.glEnd()
        }

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }
    fun renderScoreboard(){
        drawBlackPanel4(vx.toDouble() + Xoffset2.get(), vy.toDouble() + Yoffset2.get(), vw.toDouble(), vh.toDouble())
        drawBlackPanel3(vx2+ Xoffset2.get(), vy2 + Yoffset2.get().toDouble(), vw2.toDouble(), vh2.toDouble())
        drawBlackPanel3(vx3 + Xoffset2.get(), vy3 + Yoffset2.get().toDouble(), vw3.toDouble(), vh3.toDouble())
        drawText1("$timerMinute:$timerSecounds", tx.toInt() + Xoffset2.get().toInt(), ty.toInt() + Yoffset2.get().toInt(), 255, 255, 255)
        drawText1(ctwinValue.toString(), tx2.toInt() + Xoffset2.get().toInt(), ty2.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
        drawText1(twinValue.toString(), tx3.toInt() + Xoffset2.get().toInt(), ty3.toInt() + Yoffset2.get().toInt(), 228, 224, 175)
    }
    fun renderC4(){
        if (isRenderA) {
            val scaledResolution = ScaledResolution(mc)
            val centerX = scaledResolution.scaledWidth / 2
            val centerY = scaledResolution.scaledHeight / 2

            val elapsedTime = (currentTimeMillis() - startTime) / 1000
            var remainingTime = countdownSeconds - elapsedTime

            if (remainingTime < 0) {
                remainingTime = 0
                isRenderA = false
            }
            MainLib.drawPanel(centerX + vxx.toDouble() + Xoffset3.get().toInt(), centerY + vyy.toDouble() + Yoffset3.get().toInt(), vww.toDouble(), vhh.toDouble(), 0, 0, 0, 150)
            drawProgressRing(centerX + xxx + Xoffset3.get().toInt(), centerY + yyy + Yoffset3.get().toInt(), radiuss, remainingTime.toFloat() / countdownSeconds)
            drawText1("Location: A", centerX + vxx2 + Xoffset3.get().toInt(), centerY + vyy2 + Yoffset3.get().toInt(), 255, 255, 255)
            drawText1("Time: $remainingTime", centerX + vxx3 + Xoffset3.get().toInt(), centerY + vyy3 + Yoffset3.get().toInt(), 255, 255, 255)
        }
        if (isRenderB) {
            val scaledResolution = ScaledResolution(mc)
            val centerX = scaledResolution.scaledWidth / 2
            val centerY = scaledResolution.scaledHeight / 2

            val elapsedTime = (currentTimeMillis() - startTime) / 1000
            var remainingTime = countdownSeconds - elapsedTime

            if (remainingTime < 0) {
                remainingTime = 0
                isRenderB = false
            }
            MainLib.drawPanel(centerX + vxx.toDouble() + Xoffset3.get().toInt(), centerY + vyy.toDouble() + Yoffset3.get().toInt(), vww.toDouble(), vhh.toDouble(), 0, 0, 0, 150)
            drawProgressRing(centerX + xxx + Xoffset3.get().toInt(), centerY + yyy + Yoffset3.get().toInt(), radiuss, remainingTime.toFloat() / countdownSeconds)
            drawText1("Location: B", centerX + vxx2 + Xoffset3.get().toInt(), centerY + vyy2 + Yoffset3.get().toInt(), 255, 255, 255)
            drawText1("Time: $remainingTime", centerX + vxx3 + Xoffset3.get().toInt(), centerY + vyy3 + Yoffset3.get().toInt(), 255, 255, 255)
        }
    }
    fun renderCTWin() {
        if (ctw) {
            if (alpha <= 0) alpha = 0
            if (tick < ticks.get()) {
                tick++
                alpha += 2
                drawBlackPanel2(x2.toDouble() + Xoffset2.get().toInt(), y2.toDouble() + Yoffset2.get().toInt(), width1.toDouble(), height2.toDouble())
                drawBlackPanel(x.toDouble() + Xoffset2.get().toInt(), y.toDouble() + Yoffset2.get().toInt(), width1.toDouble(), height.toDouble())

                renderImage(imgCT, imgX.toInt() + Xoffset2.get().toInt(), imgY.toInt() + Yoffset2.get().toInt(), imgWidth, imgHeight)
                drawText2("Counter-Terrorists Win", textX.toInt() + Xoffset2.get().toInt(), textY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
                drawText3("KillStreak: You killed $kills players in the round", MVPX.toInt() + Xoffset2.get().toInt(), MVPY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
                if (!can) {
                    all += kills
                    can = true
                }
                drawText3("Now Playing: $soundName", soundTextX.toInt() + Xoffset2.get().toInt(), soundTextY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
                isRenderA = false
                isRenderB = false

                when (kills) {
                    1 -> renderImage(imgsilver1, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                    2 -> renderImage(imgsilver2, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                    3 -> renderImage(imgsilver3, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                    4 -> renderImage(imgsilver4, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                    5 -> renderImage(imgsilver5, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                }
            } else {
                ctw = false
                tick = 0
                alpha = 0
            }
        }
    }
    fun renderTWin(){
    if (tw) {
        if (alpha <= 0) alpha = 0
        if (tick < ticks.get()) {
            alpha += 2
            tick++
            drawBlackPanel2(x2.toDouble() + Xoffset2.get().toInt(), y2.toDouble() + Yoffset2.get().toInt(), width1.toDouble(), height2.toDouble())
            drawBlackPanel(x.toDouble() + Xoffset2.get().toInt(), y.toDouble() + Yoffset2.get().toInt(), width1.toDouble(), height.toDouble())
            renderImage(imgT, imgX.toInt() + Xoffset2.get().toInt(), imgY.toInt() + Yoffset2.get().toInt(), imgWidth, imgHeight)
            drawText2("Terrorists Win", textX.toInt() + Xoffset2.get().toInt(), textY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
            drawText3("KillStreak: You killed $kills players in the round", MVPX.toInt() + Xoffset2.get().toInt(), MVPY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
            if (!can) {
                all += kills
                can = true
            }
            drawText3("Now Playing: $soundName", soundTextX.toInt() + Xoffset2.get().toInt(), soundTextY.toInt() + Yoffset2.get().toInt(), 191, 215, 234)
            isRenderA = false
            isRenderB = false

            when (kills) {
                1 -> renderImage(imgsilver1, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                2 -> renderImage(imgsilver2, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                3 -> renderImage(imgsilver3, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                4 -> renderImage(imgsilver4, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
                5 -> renderImage(imgsilver5, silverX.toInt() + Xoffset2.get().toInt(), silverY.toInt() + Yoffset2.get().toInt(), silverWidth, silverHeight)
            }
        } else {
            tw = false
            tick = 0
            alpha = 0
        }
    }
    }
   private fun drawText1(text: String, x: Int, y: Int, r: Int, g: Int, b: Int){drawText(Fonts.font28, text, x, y, r, g, b)}
   private fun drawText2(text: String, x: Int, y: Int, rv: Int, gv: Int, bv: Int){drawText(Fonts.font28, text, x, y, rv, gv, bv)}
   private fun drawText3(text: String, x: Int, y: Int, rv: Int, gv: Int, bv: Int){drawText(Fonts.SFUI35, text, x, y, rv, gv, bv)}
    private fun renderImage(resourceLocation: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        mc.textureManager.bindTexture(resourceLocation)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
        GL11.glDisable(GL11.GL_BLEND)
    }
    private fun renderHotbar(){
        if (findM4() != -1 && findAK() == -1) RenderUtils.drawImage(m4, propx2.toInt() + Xoffset.get().toInt(), propy2.toInt() - 13 + YOffset.get().toInt(), propw2.toInt(), proph2.toInt())
        if (findAK() != -1 && findM4() == -1)  RenderUtils.drawImage(ak, propx2.toInt() + Xoffset.get().toInt(), propy2.toInt() - 13 + YOffset.get().toInt(), propw2.toInt(), proph2.toInt())
        if (p2000() != -1 && findDesertEagle() == -1) RenderUtils.drawImage(p2000, propx4.toInt() + Xoffset.get().toInt(), propy4.toInt() - 25 + YOffset.get().toInt(), propw4.toInt(), proph4.toInt())
        if (findDesertEagle() != -1 && p2000() == -1) RenderUtils.drawImage(DesertEagle, propx4.toInt() + Xoffset.get().toInt(), propy4.toInt() - 25 + YOffset.get().toInt(), propw4.toInt(), proph4.toInt())
        if (findSword() != -1) RenderUtils.drawImage(sword, propx5.toInt() + Xoffset.get().toInt(), propy5.toInt() + YOffset.get().toInt(), propw3.toInt(), proph3.toInt())
        if (findProp() != -1 && findProp2() == -1 && findProp3() == -1) RenderUtils.drawImage(prop1, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        if (findProp2() != -1 && findProp() == -1 && findProp3() == -1) RenderUtils.drawImage(prop2, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        if (findProp3() != -1 && findProp() == -1 && findProp2() == -1) RenderUtils.drawImage(prop6, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
            RenderUtils.drawImage(prop1, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
            RenderUtils.drawImage(prop2, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
            RenderUtils.drawImage(prop6, propx.toInt() - 20 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        }
        if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) {
            RenderUtils.drawImage( prop2, propx.toInt() + Xoffset.get().toInt(),propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
            RenderUtils.drawImage(prop6, propx.toInt() + Xoffset.get().toInt() - 10, propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        }
        if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) {
            RenderUtils.drawImage( prop1, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
            RenderUtils.drawImage(prop2, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        }
        if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) {
            RenderUtils.drawImage(prop1, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
            RenderUtils.drawImage(prop6, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
        }

        val player = mc.thePlayer ?: return
        val heldItem = player.heldItem ?: return

        when (heldItem.item) {
            Items.stone_hoe -> {
                RenderUtils.drawImage(ak2, propx2.toInt() + Xoffset.get().toInt(), propy2.toInt() - 13 + YOffset.get().toInt(), propw2.toInt(), proph2.toInt())
            }

            Items.iron_hoe -> {
                RenderUtils.drawImage(m42, propx2.toInt() + Xoffset.get().toInt(), propy2.toInt() - 13 + YOffset.get().toInt(), propw2.toInt(), proph2.toInt())
            }

            Items.wooden_pickaxe -> {
                RenderUtils.drawImage(p20002, propx4.toInt() + Xoffset.get().toInt(), propy4.toInt() - 25 + YOffset.get().toInt(), propw4.toInt(), proph4.toInt())
            }

            Items.golden_pickaxe -> {
                RenderUtils.drawImage(DesertEagle2, propx4.toInt() + Xoffset.get().toInt(), propy4.toInt() - 25 + YOffset.get().toInt(), propw4.toInt(), proph4.toInt())
            }

            Items.iron_axe -> {
                RenderUtils.drawImage(sword2, propx5.toInt() + Xoffset.get().toInt(), propy5.toInt() + YOffset.get().toInt(), propw3.toInt(), proph3.toInt()
                )
            }

            Items.stone_axe -> {
                RenderUtils.drawImage(sword2, propx5.toInt() + Xoffset.get().toInt(), propy5.toInt() + YOffset.get().toInt(), propw3.toInt(), proph3.toInt())
            }

            Items.blaze_powder -> {
                if (findProp() != -1 && findProp2() == -1 && findProp3() == -1) {
                    RenderUtils.drawImage(prop4, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(prop4, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) {
                    RenderUtils.drawImage(prop4, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) {
                    RenderUtils.drawImage(prop4, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) {
                    RenderUtils.drawImage(prop4, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
            }

            Items.carrot -> {
                if (findProp2() != -1 && findProp() == -1 && findProp3() == -1) {
                    RenderUtils.drawImage(prop3, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(prop3, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(),propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) {
                    RenderUtils.drawImage(prop3, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt()
                    )
                }
            }

            Items.potato -> {
                if (findProp3() != -1 && findProp() == -1 && findProp2() == -1) {
                    RenderUtils.drawImage(prop7, propx.toInt() + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(prop7, propx.toInt() - 20 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) {
                    RenderUtils.drawImage(prop7, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
                if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) {
                    RenderUtils.drawImage(prop7, propx.toInt() - 10 + Xoffset.get().toInt(), propy.toInt() + YOffset.get().toInt(), propw.toInt(), proph.toInt())
                }
            }

            null -> {
                Select1 = false
                Select2 = false
                Select3 = false
            }
        }
    }
    private fun drawProgressRing(centerX: Int, centerY: Int, radius: Float, progress: Float) {
        val segments = 4000
        val anglePerSegment = (2 * Math.PI / segments).toFloat()

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glLineWidth(3f)
        GL11.glBegin(GL11.GL_LINE_STRIP)
        for (i in 0..(segments * progress).toInt()) {
            val angle = i * anglePerSegment
            val x = centerX + radius * Math.cos(angle.toDouble()).toFloat()
            val y = centerY + radius * Math.sin(angle.toDouble()).toFloat()
            GL11.glVertex2f(x, y)
        }
        GL11.glEnd()

        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_BLEND)

        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GL11.glPopMatrix()
    }
    private fun playWinSound(musicMode: String, musicNumber: Int) {
        val soundList = listOf(
            "EZ4ENCE", "dashstar", "The Good Youth", "inhuman", "Heading for the Source",
            "The Lowlife Pack", "Under Bright Lights", "ULTIMATE", "I AM", "u mad!",
            "Void", "MVP SOUND 12", "MVP SOUND 13", "MVP SOUND 14", "MVP SOUND 15",
            "MVP SOUND 16", "MVP SOUND 17", "MVP SOUND 18", "MVP SOUND 19", "MVP SOUND 20",
            "MVP SOUND 21", "MVP SOUND 22", "MVP SOUND 23", "MVP SOUND 24", "MVP SOUND 25"
        )
        val sound = if (musicMode == "Random") soundList.random() else soundList.getOrNull(musicNumber - 1) ?: soundList.first()
        when (sound) {
            "EZ4ENCE" -> Leaf.tipSoundManager.winSound.asyncPlay()
            "dashstar" -> Leaf.tipSoundManager.winSound2.asyncPlay()
            "The Good Youth" -> Leaf.tipSoundManager.winSound3.asyncPlay()
            "inhuman" -> Leaf.tipSoundManager.winSound4.asyncPlay()
            "Heading for the Source" -> Leaf.tipSoundManager.winSound5.asyncPlay()
            "The Lowlife Pack" -> Leaf.tipSoundManager.winSound6.asyncPlay()
            "Under Bright Lights" -> Leaf.tipSoundManager.winSound7.asyncPlay()
            "ULTIMATE" -> Leaf.tipSoundManager.winSound8.asyncPlay()
            "I AM" -> Leaf.tipSoundManager.winSound9.asyncPlay()
            "u mad!" -> Leaf.tipSoundManager.winSound10.asyncPlay()
            "Void" -> Leaf.tipSoundManager.winSound11.asyncPlay()
            "MVP SOUND 12" -> Leaf.tipSoundManager.winSound12.asyncPlay()
            "MVP SOUND 13" -> Leaf.tipSoundManager.winSound13.asyncPlay()
            "MVP SOUND 14" -> Leaf.tipSoundManager.winSound14.asyncPlay()
            "MVP SOUND 15" -> Leaf.tipSoundManager.winSound15.asyncPlay()
            "MVP SOUND 16" -> Leaf.tipSoundManager.winSound16.asyncPlay()
            "MVP SOUND 17" -> Leaf.tipSoundManager.winSound17.asyncPlay()
            "MVP SOUND 18" -> Leaf.tipSoundManager.winSound18.asyncPlay()
            "MVP SOUND 19" -> Leaf.tipSoundManager.winSound19.asyncPlay()
            "MVP SOUND 20" -> Leaf.tipSoundManager.winSound20.asyncPlay()
            "MVP SOUND 21" -> Leaf.tipSoundManager.winSound21.asyncPlay()
            "MVP SOUND 22" -> Leaf.tipSoundManager.winSound22.asyncPlay()
            "MVP SOUND 23" -> Leaf.tipSoundManager.winSound23.asyncPlay()
            "MVP SOUND 24" -> Leaf.tipSoundManager.winSound24.asyncPlay()
            "MVP SOUND 25" -> Leaf.tipSoundManager.winSound25.asyncPlay()
        }
        soundName = sound
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
   private fun drawBlackPanel(x: Double, y: Double, width: Double, height: Double){drawPanel(x, y, width, height, Color(r, g, b, alpha))}
   private fun drawBlackPanel2(x: Double, y: Double, width: Double, height: Double){drawPanel(x, y, width, height, Color(r2, g2, b2, alpha))}
   private fun drawBlackPanel4(x: Double, y: Double, width: Double, height: Double){drawPanel(x, y, width, height, Color(r, g, b, 156))}
}