/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.MainLib.FindItems
import net.ccbluex.liquidbounce.features.MainLib.drawText
import net.ccbluex.liquidbounce.features.MainLib.renderLine
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils6.RenderU
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.lang.System.currentTimeMillis
import kotlin.random.Random

@ModuleInfo(name = "CounterStrike", category = ModuleCategory.COMBAT)
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
    private val gapValue = FloatValue("Cross-Gap", 5f, 0.25f, 15f)
    private val dynamicValue = BoolValue("Cross-Dynamic", true)

    private var kills = 0
    private var startTime = 0L
    private var countdownSeconds = 45

    private val armorx = FloatValue("armorx", 0f, -200f, 200f)
    private val armory = FloatValue("armory", 0f, -200f, 200f)
    private val armorw = FloatValue("armorw", 0f, -200f, 200f)
    private val armorh = FloatValue("armorh", 0f, -200f, 200f)


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
    private val armor1 = ResourceLocation("leaf/armor1.png")
    private val armor2 = ResourceLocation("leaf/armor2.png")
    private val armor3 = ResourceLocation("leaf/armor3.png")
    private var isRenderA = false
    private var isRenderB = false
    var Select1 = false
    var Select2 = false
    var Select3 = false
    var can = false
    var all = 0
    var names = ""
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
        sound = ""
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
    fun onRender(event: Render2DEvent) {


        val screenWidth = event.scaledResolution.scaledWidth / 2
        val screenHeight = event.scaledResolution.scaledHeight / 2
        cross(screenWidth, screenHeight)
        if (!can) {
            kd = all.toDouble() / die.toDouble()
            can = true
        }

        drawBlackPanel4(vx.toDouble() + Xoffset2.get(), vy.toDouble() + Yoffset2.get(), vw.toDouble(), vh.toDouble())
        drawBlackPanel3(
            vx2.toDouble() + Xoffset2.get(),
            vy2 + Yoffset2.get().toDouble(),
            vw2.toDouble(),
            vh2.toDouble()
        )
        drawBlackPanel3(
            vx3.toDouble() + Xoffset2.get(),
            vy3 + Yoffset2.get().toDouble(),
            vw3.toDouble(),
            vh3.toDouble()
        )
        drawText3(
            "$timerMinute:$timerSecounds",
            tx.toInt() + Xoffset2.get().toInt(),
            ty.toInt() + Yoffset2.get().toInt(),
            255,
            255,
            255
        )
        drawText3(
            ctwinValue.toString(),
            tx2.toInt() + Xoffset2.get().toInt(),
            ty2.toInt() + Yoffset2.get().toInt(),
            191,
            215,
            234
        )
        drawText3(
            twinValue.toString(),
            tx3.toInt() + Xoffset2.get().toInt(),
            ty3.toInt() + Yoffset2.get().toInt(),
            228,
            224,
            175
        )

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
            MainLib.drawPanel(
                centerX + vxx.toDouble() + Xoffset3.get().toInt(),
                centerY + vyy.toDouble() + Yoffset3.get().toInt(),
                vww.toDouble(),
                vhh.toDouble(),
                0,
                0,
                0,
                150
            )
            drawProgressRing(
                centerX + xxx + Xoffset3.get().toInt(),
                centerY + yyy + Yoffset3.get().toInt(),
                radiuss.toFloat(),
                remainingTime.toFloat() / countdownSeconds
            )
            drawText3(
                "Location: A",
                centerX + vxx2 + Xoffset3.get().toInt(),
                centerY + vyy2 + Yoffset3.get().toInt(),
                255,
                255,
                255
            )
            drawText3(
                "Time: $remainingTime",
                centerX + vxx3 + Xoffset3.get().toInt(),
                centerY + vyy3 + Yoffset3.get().toInt(),
                255,
                255,
                255
            )
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
            MainLib.drawPanel(
                centerX + vxx.toDouble() + Xoffset3.get().toInt(),
                centerY + vyy.toDouble() + Yoffset3.get().toInt(),
                vww.toDouble(),
                vhh.toDouble(),
                0,
                0,
                0,
                150
            )
            drawProgressRing(
                centerX + xxx + Xoffset3.get().toInt(),
                centerY + yyy + Yoffset3.get().toInt(),
                radiuss,
                remainingTime.toFloat() / countdownSeconds
            )
            drawText3(
                "Location: B",
                centerX + vxx2 + Xoffset3.get().toInt(),
                centerY + vyy2 + Yoffset3.get().toInt(),
                255,
                255,
                255
            )
            drawText3(
                "Time: $remainingTime",
                centerX + vxx3 + Xoffset3.get().toInt(),
                centerY + vyy3 + Yoffset3.get().toInt(),
                255,
                255,
                255
            )
        }

        if (alpha >= a) alpha = a
        if (ctw) {
            if (alpha <= 0) alpha = 0
            if (tick < ticks.get()) {
                tick++
                alpha += 2
                drawBlackPanel2(
                    x2.toDouble() + Xoffset2.get().toInt(),
                    y2.toDouble() + Yoffset2.get().toInt(),
                    width1.toDouble(),
                    height2.toDouble()
                )
                drawBlackPanel(
                    x.toDouble() + Xoffset2.get().toInt(),
                    y.toDouble() + Yoffset2.get().toInt(),
                    width1.toDouble(),
                    height.toDouble()
                )

                renderImage(
                    imgCT,
                    imgX.toInt() + Xoffset2.get().toInt(),
                    imgY.toInt() + Yoffset2.get().toInt(),
                    imgWidth,
                    imgHeight
                )
                drawText4(
                    "Counter-Terrorists Win",
                    textX.toInt() + Xoffset2.get().toInt(),
                    textY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                drawText5(
                    "KillStreak: You killed $kills players in the round",
                    MVPX.toInt() + Xoffset2.get().toInt(),
                    MVPY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                if (!can) {
                    all += kills
                    can = true
                }
                drawText5(
                    "Now Playing: $sound",
                    soundTextX.toInt() + Xoffset2.get().toInt(),
                    soundTextY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                isRenderA = false
                isRenderB = false

                when (kills) {
                    1 -> {
                        renderImage(
                            imgsilver1,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    2 -> {
                        renderImage(
                            imgsilver2,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    3 -> {
                        renderImage(
                            imgsilver3,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    4 -> {
                        renderImage(
                            imgsilver4,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    5 -> {
                        renderImage(
                            imgsilver5,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }
                }

            } else {
                ctw = false
                tick = 0
                alpha = 0
            }
        }
        if (tw) {
            if (alpha <= 0) alpha = 0
            if (tick < ticks.get()) {
                alpha += 2
                tick++
                drawBlackPanel2(
                    x2.toDouble() + Xoffset2.get().toInt(),
                    y2.toDouble() + Yoffset2.get().toInt(),
                    width1.toDouble(),
                    height2.toDouble()
                )
                drawBlackPanel(
                    x.toDouble() + Xoffset2.get().toInt(),
                    y.toDouble() + Yoffset2.get().toInt(),
                    width1.toDouble(),
                    height.toDouble()
                )
                renderImage(
                    imgT,
                    imgX.toInt() + Xoffset2.get().toInt(),
                    imgY.toInt() + Yoffset2.get().toInt(),
                    imgWidth,
                    imgHeight
                )
                drawText4(
                    "Terrorists Win",
                    textX.toInt() + Xoffset2.get().toInt(),
                    textY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                drawText5(
                    "KillStreak: You killed $kills players in the round",
                    MVPX.toInt() + Xoffset2.get().toInt(),
                    MVPY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                if (!can) {
                    all += kills
                    can = true
                }
                drawText5(
                    "Now Playing: $sound",
                    soundTextX.toInt() + Xoffset2.get().toInt(),
                    soundTextY.toInt() + Yoffset2.get().toInt(),
                    191,
                    215,
                    234
                )
                isRenderA = false
                isRenderB = false

                when (kills) {
                    1 -> {
                        renderImage(
                            imgsilver1,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    2 -> {
                        renderImage(
                            imgsilver2,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    3 -> {
                        renderImage(
                            imgsilver3,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    4 -> {
                        renderImage(
                            imgsilver4,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }

                    5 -> {
                        renderImage(
                            imgsilver5,
                            silverX.toInt() + Xoffset2.get().toInt(),
                            silverY.toInt() + Yoffset2.get().toInt(),
                            silverWidth,
                            silverHeight
                        )
                    }
                }
            } else {
                tw = false
                tick = 0
                alpha = 0
            }
        }
        if (findM4() != -1 && findAK() == -1) {
            RenderUtils.drawImage(
                m4,
                propx2.toInt() + Xoffset.get().toInt(),
                propy2.toInt() - 13 + YOffset.get().toInt(),
                propw2.toInt(),
                proph2.toInt()
            )
        }
        if (findAK() != -1 && findM4() == -1) {
            RenderUtils.drawImage(
                ak,
                propx2.toInt() + Xoffset.get().toInt(),
                propy2.toInt() - 13 + YOffset.get().toInt(),
                propw2.toInt(),
                proph2.toInt()
            )
        }
        if (p2000() != -1 && findDesertEagle() == -1) {
            RenderUtils.drawImage(
                p2000,
                propx4.toInt() + Xoffset.get().toInt(),
                propy4.toInt() - 25 + YOffset.get().toInt(),
                propw4.toInt(),
                proph4.toInt()
            )
        }
        if (findDesertEagle() != -1 && p2000() == -1) {
            RenderUtils.drawImage(
                DesertEagle,
                propx4.toInt() + Xoffset.get().toInt(),
                propy4.toInt() - 25 + YOffset.get().toInt(),
                propw4.toInt(),
                proph4.toInt()
            )
        }

        if (findSword() != -1) {
            RenderUtils.drawImage(
                sword,
                propx5.toInt() + Xoffset.get().toInt(),
                propy5.toInt() + YOffset.get().toInt(),
                propw3.toInt(),
                proph3.toInt()
            )
        }
        if (findProp() != -1 && findProp2() == -1 && findProp3() == -1) {
            RenderUtils.drawImage(
                prop1,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }
        if (findProp2() != -1 && findProp() == -1 && findProp3() == -1) {
            RenderUtils.drawImage(
                prop2,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }
        if (findProp3() != -1 && findProp() == -1 && findProp2() == -1) {
            RenderUtils.drawImage(
                prop6,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }

        if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
            RenderUtils.drawImage(
                prop1,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
            RenderUtils.drawImage(
                prop2,
                propx.toInt() - 10 + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
            RenderUtils.drawImage(
                prop6,
                propx.toInt() - 20 + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }

        if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) { //2,3
            RenderUtils.drawImage(
                prop2,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
            RenderUtils.drawImage(
                prop6,
                propx.toInt() + Xoffset.get().toInt() - 10,
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }
        if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) { // 1,2
            RenderUtils.drawImage(
                prop1,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
            RenderUtils.drawImage(
                prop2,
                propx.toInt() - 10 + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }
        if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) { // 1,3
            RenderUtils.drawImage(
                prop1,
                propx.toInt() + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
            RenderUtils.drawImage(
                prop6,
                propx.toInt() - 10 + Xoffset.get().toInt(),
                propy.toInt() + YOffset.get().toInt(),
                propw.toInt(),
                proph.toInt()
            )
        }

        when (mc.thePlayer.heldItem.item) {
            Items.stone_hoe -> {
                RenderUtils.drawImage(
                    ak2,
                    propx2.toInt() + Xoffset.get().toInt(),
                    propy2.toInt() - 13 + YOffset.get().toInt(),
                    propw2.toInt(),
                    proph2.toInt()
                )
            }

            Items.iron_hoe -> {
                RenderUtils.drawImage(
                    m42,
                    propx2.toInt() + Xoffset.get().toInt(),
                    propy2.toInt() - 13 + YOffset.get().toInt(),
                    propw2.toInt(),
                    proph2.toInt()
                )
            }

            Items.wooden_pickaxe -> {
                RenderUtils.drawImage(
                    p20002,
                    propx4.toInt() + Xoffset.get().toInt(),
                    propy4.toInt() - 25 + YOffset.get().toInt(),
                    propw4.toInt(),
                    proph4.toInt()
                )
            }

            Items.golden_pickaxe -> {
                RenderUtils.drawImage(
                    DesertEagle2,
                    propx4.toInt() + Xoffset.get().toInt(),
                    propy4.toInt() - 25 + YOffset.get().toInt(),
                    propw4.toInt(),
                    proph4.toInt()
                )
            }

            Items.iron_axe -> {
                RenderUtils.drawImage(
                    sword2,
                    propx5.toInt() + Xoffset.get().toInt(),
                    propy5.toInt() + YOffset.get().toInt(),
                    propw3.toInt(),
                    proph3.toInt()
                )
            }

            Items.stone_axe -> {
                RenderUtils.drawImage(
                    sword2,
                    propx5.toInt() + Xoffset.get().toInt(),
                    propy5.toInt() + YOffset.get().toInt(),
                    propw3.toInt(),
                    proph3.toInt()
                )
            }

            Items.blaze_powder -> {
                if (findProp() != -1 && findProp2() == -1 && findProp3() == -1) {
                    RenderUtils.drawImage(
                        prop4,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(
                        prop4,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) {
                    RenderUtils.drawImage(
                        prop4,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) {
                    RenderUtils.drawImage(
                        prop4,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) {
                    RenderUtils.drawImage(
                        prop4,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
            }

            Items.carrot -> {
                if (findProp2() != -1 && findProp() == -1 && findProp3() == -1) {
                    RenderUtils.drawImage(
                        prop3,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(
                        prop3,
                        propx.toInt() - 10 + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp() != -1 && findProp3() == -1) {
                    RenderUtils.drawImage(
                        prop3,
                        propx.toInt() - 10 + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
            }

            Items.potato -> {
                if (findProp3() != -1 && findProp() == -1 && findProp2() == -1) {
                    RenderUtils.drawImage(
                        prop7,
                        propx.toInt() + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp3() != -1 && findProp() != -1) {
                    RenderUtils.drawImage(
                        prop7,
                        propx.toInt() - 20 + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() != -1 && findProp() == -1 && findProp3() != -1) {
                    RenderUtils.drawImage(
                        prop7,
                        propx.toInt() - 10 + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
                if (findProp2() == -1 && findProp() != -1 && findProp3() != -1) {
                    RenderUtils.drawImage(
                        prop7,
                        propx.toInt() - 10 + Xoffset.get().toInt(),
                        propy.toInt() + YOffset.get().toInt(),
                        propw.toInt(),
                        proph.toInt()
                    )
                }
            }

            null -> {
                Select1 = false
                Select2 = false
                Select3 = false
            }
        }
        val playerArmor2 = mc.thePlayer.inventory.armorInventory[3].item as ItemArmor
        val playerArmor1 = mc.thePlayer.inventory.armorInventory[2].item as ItemArmor

      if (playerArmor1.armorMaterial == ItemArmor.ArmorMaterial.IRON && playerArmor2.armorMaterial == ItemArmor.ArmorMaterial.IRON) {
          RenderUtils.drawImage(
              armor1,
              armorx.get().toInt(),
              armory.get().toInt(),
              armorw.get().toInt(),
              armorh.get().toInt()
          )
      }else if (playerArmor1.armorMaterial == ItemArmor.ArmorMaterial.IRON && playerArmor2.armorMaterial != ItemArmor.ArmorMaterial.IRON) {
            RenderUtils.drawImage(
                armor2,
                armorx.get().toInt(),
                armory.get().toInt(),
                armorw.get().toInt(),
                armorh.get().toInt()
            )
        }else if (playerArmor1.armorMaterial != ItemArmor.ArmorMaterial.IRON && playerArmor2.armorMaterial == ItemArmor.ArmorMaterial.IRON) {
            RenderUtils.drawImage(
                armor3,
                armorx.get().toInt(),
                armory.get().toInt(),
                armorw.get().toInt(),
                armorh.get().toInt()
            )
        }
        if (playerArmor1.armorMaterial == ItemArmor.ArmorMaterial.CHAIN && playerArmor2.armorMaterial == ItemArmor.ArmorMaterial.CHAIN) {
            RenderUtils.drawImage(
                armor1,
                armorx.get().toInt(),
                armory.get().toInt(),
                armorw.get().toInt(),
                armorh.get().toInt()
            )
        }else if (playerArmor1.armorMaterial == ItemArmor.ArmorMaterial.CHAIN && playerArmor2.armorMaterial != ItemArmor.ArmorMaterial.CHAIN) {
            RenderUtils.drawImage(
                armor2,
                armorx.get().toInt(),
                armory.get().toInt(),
                armorw.get().toInt(),
                armorh.get().toInt()
            )
        }else if (playerArmor1.armorMaterial != ItemArmor.ArmorMaterial.CHAIN && playerArmor2.armorMaterial == ItemArmor.ArmorMaterial.CHAIN) {
            RenderUtils.drawImage(
                armor3,
                armorx.get().toInt(),
                armory.get().toInt(),
                armorw.get().toInt(),
                armorh.get().toInt()
            )
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
        val playerArmor = mc.thePlayer.inventory.armorInventory[3]
        val myItemArmor = playerArmor.item as ItemArmor

        if (event.packet is S45PacketTitle) {
            val titlePacket = event.packet as S45PacketTitle
            val message = titlePacket.message.unformattedText
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
            val regex = Regex(mc.thePlayer.name+" - (\\d+) kills")
            val matchResult = regex.find(message)

            if (matchResult != null) {
                kills = matchResult.groupValues[1].toInt()
            }

            if (message.contains(resetTitle.get(), ignoreCase = true)) {
                ctwinValue = 0
                twinValue =0
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
    private fun findM4(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.iron_hoe) {
                return i
            }
        }
        return -1
    }
    private fun findAK(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.stone_hoe) {
                return i
            }
        }
        return -1
    }
    private fun p2000(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.wooden_pickaxe) {
                return i
            }
        }
        return -1
    }
    private fun findDesertEagle(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.golden_pickaxe) {
                return i
            }
        }
        return -1
    }
    private fun findSword(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.iron_axe||stack.item == Items.stone_axe) {
                return i
            }
        }
        return -1
    }
    private fun findProp(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.blaze_powder) {
                return i
            }
        }
        return -1
    }
    private fun findProp2(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.carrot) {
                return i
            }
        }
        return -1
    }
    private fun findProp3(): Int {
        for (i in 0 until net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.mainInventory.size) {
            val stack = net.ccbluex.liquidbounce.utils.mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.potato) {
                return i
            }
        }
        return -1
    }
        private  fun cross(screenWidth:Int,screenHeight:Int) {
        val player = mc.thePlayer
        val movementSpeed = Math.sqrt((player.motionX * player.motionX + player.motionZ * player.motionZ).toDouble()).toFloat()

        val gap = if (dynamicValue.get()) gapValue.get() + movementSpeed *5 else gapValue.get()

        val red = colorRedValue.get() / 255f
        val green = colorGreenValue.get() / 255f
        val blue = colorBlueValue.get() / 255f
        val alpha = colorAlphaValue.get() / 255f


        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glColor4f(red, green, blue, alpha)
        GL11.glLineWidth(widthValue.get())

        GL11.glBegin(GL11.GL_LINES)

        // 上部线
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap).toFloat())
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight - gap - sizeValue.get()).toFloat())

        // 下部线
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap).toFloat())
        GL11.glVertex2f(screenWidth.toFloat(), (screenHeight + gap + sizeValue.get()).toFloat())

        // 左部线
        GL11.glVertex2f((screenWidth - gap).toFloat(), screenHeight.toFloat())
        GL11.glVertex2f((screenWidth - gap - sizeValue.get()).toFloat(), screenHeight.toFloat())

        // 右部线
        GL11.glVertex2f((screenWidth + gap).toFloat(), screenHeight.toFloat())
        GL11.glVertex2f((screenWidth + gap + sizeValue.get()).toFloat(), screenHeight.toFloat())

        GL11.glEnd()

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
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


    private fun renderImage(resourceLocation: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        mc.textureManager.bindTexture(resourceLocation)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
        GL11.glDisable(GL11.GL_BLEND)
    }
    private fun drawProgressRing(centerX: Int, centerY: Int, radius: Float, progress: Float) {
        val segments = 4000
        val anglePerSegment = (2 * Math.PI / segments).toFloat()

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(1f, 1f, 1f, 1f) // 设置白色

        GL11.glLineWidth(3f)
        GL11.glBegin(GL11.GL_LINE_STRIP)
        for (i in 0..(segments * progress).toInt()) {
            val angle = i * anglePerSegment
            val x = centerX + radius * Math.cos(angle.toDouble()).toFloat()
            val y = centerY + radius * Math.sin(angle.toDouble()).toFloat()
            GL11.glVertex2f(x, y)
        }
        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
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

    data class Color(val red: Int, val green: Int, val blue: Int)
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