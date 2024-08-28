/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.font.CFontRenderer
import net.ccbluex.liquidbounce.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.*
import net.ccbluex.liquidbounce.ui.realpha
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.value.*
import net.ccbluex.liquidbounce.utils.render.shadowRenderUtils
import net.minecraft.util.ResourceLocation
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", blur = true)
class Notifications(x: Double = 0.0, y: Double = 0.0, scale: Float = 1F,side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {


    private val backGroundAlphaValue = IntegerValue("BackGroundAlpha", 170, 0, 255)
    private val titleShadow = BoolValue("TitleShadow", false)
    private val motionBlur = BoolValue("Motionblur", false)
    private val contentShadow = BoolValue("ContentShadow", true)
    private val whiteText = BoolValue("WhiteTextColor", true)
    private val modeColored = BoolValue("CustomModeColored", true)
    companion object {
        val styleValue = ListValue("Mode", arrayOf("Modern", "Better"), "Better")
    }

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "example notification", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border? {
        // bypass java.util.ConcurrentModificationException
        LiquidBounce.hud.notifications.map { it }.forEachIndexed { index, notify ->
            GL11.glPushMatrix()

            if (notify.drawNotification(index, FontLoaders.C16, backGroundAlphaValue.get(), blurValue.get(), this.renderX.toFloat(), this.renderY.toFloat(), scale,contentShadow.get(),titleShadow.get(),motionBlur.get(),whiteText.get(),modeColored.get(), Companion)) {
                LiquidBounce.hud.notifications.remove(notify)
            }

            GL11.glPopMatrix()
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification)) {
                LiquidBounce.hud.addNotification(exampleNotification)
            }

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
        }

        return null
    }

    override fun drawBoarderBlur(blurRadius: Float) {}
}


class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    private val animeTime: Int = 500
) {
    var width = 100
    val height = 30

    var x = 0F
    var textLengthtitle = 0
    var textLengthcontent = 0
    var textLength = 0f
    init {
        textLengthtitle = Fonts.font35.getStringWidth(title)
        textLengthcontent = Fonts.font35.getStringWidth(content)
        textLength = textLengthcontent.toFloat() + textLengthtitle.toFloat()
    }

    var fadeState = FadeState.IN
    private var nowY = -height
    var displayTime = System.currentTimeMillis()
    private var animeXTime = System.currentTimeMillis()
    private var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(
        index: Int, font: CFontRenderer, alpha: Int, blurRadius: Float, x: Float, y: Float, scale: Float,
        contentShadow: Boolean,
        titleShadow: Boolean,
        motionBlur: Boolean,
        whiteText: Boolean,
        modeColored: Boolean,
        parent: Notifications.Companion
        
    ): Boolean {
        this.width = 100.coerceAtLeast(
            font.getStringWidth(content)
                .coerceAtLeast(font.getStringWidth(title)) + 15
        )
        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        var transY = nowY.toDouble()
        var lbtl = font.getStringWidth(title + ": " + content)
        var x = 0f

        var textColor = Color(255, 255, 255).rgb

        if (whiteText) {
            textColor = Color(255, 255, 255).rgb
        } else {
            textColor = Color(10, 10, 10).rgb
        }

        // Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            transY += (realY - nowY) * pct
        } else {
            animeYTime = nowTime
        }

        // X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        val transX = width - (width * pct) - width
        GL11.glTranslated(transX, transY, 0.0)
        // draw notify
        val style = parent.styleValue.get()

        if (style.equals("Modern")) {
            if (blurRadius != 0f) {
                BlurUtils.draw(4 + (x + transX).toFloat() * scale, (y + transY).toFloat() * scale, (width * scale), (27f - 5f) * scale, blurRadius)
            }
            var colorRed = type.renderColor.red
            var colorGreen = type.renderColor.green
            var colorBlue = type.renderColor.blue

            if (modeColored) {
                //success
                if (colorRed    == 60)   colorRed    = 36
                if (colorGreen  == 224)  colorGreen  = 211
                if (colorBlue   == 92)   colorBlue   = 99

                //error
                if (colorRed    == 255) colorRed    = 248
                if (colorGreen  == 47)  colorGreen  = 72
                if (colorBlue   == 47)  colorBlue   = 72

                //warning
                if (colorRed    == 245) colorRed    = 251
                if (colorGreen  == 253)  colorGreen  = 189
                if (colorBlue   == 0)  colorBlue   = 23

                //info
                if (colorRed    == 64) colorRed    = 242
                if (colorGreen  == 90)  colorGreen  = 242
                if (colorBlue   == 167)  colorBlue   = 242
            }

            val colors = Color(colorRed, colorGreen, colorBlue, alpha / 3)

            if (motionBlur) {
                when (fadeState) {
                    FadeState.IN -> {
                        RenderUtils.drawRoundedCornerRect(3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(3F, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                    }

                    FadeState.STAY -> {
                        RenderUtils.drawRoundedCornerRect(3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(3F, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                    }

                    FadeState.OUT -> {
                        RenderUtils.drawRoundedCornerRect(4F, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(5F, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                    }
                }
            } else {
                RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
                RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
            }
            RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, colors.rgb)
            shadowRenderUtils.drawShadowWithCustomAlpha(0F + 3f, 0F, width.toFloat() + 5f, 27f - 5f, 240f)
            RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)) + 5f, 0F), 27f - 5f, 2f, Color(0, 0, 0, 26).rgb)
            FontLoaders.C12.DisplayFont2(FontLoaders.C12, title, 4F, 3F, textColor, titleShadow)
            font.DisplayFont2(font, content, 4F, 10F, textColor, contentShadow)
            return false
        }

        if(style.equals("Better")){
            val colors=Color(type.renderColor.red,type.renderColor.green,type.renderColor.blue,200)
            shadowRenderUtils.drawShadowWithCustomAlpha(2f, 0F, width.toFloat() + 5f, 27f - 5f, 250f) // oops
            RenderUtils.drawRect(3F, 0F, width.toFloat() + 5f, 27f - 5f, Color(0,0,0,0))
            RenderUtils.drawRect(2f, 27f-6f, max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time))+5f, 0F), 27f - 5f ,colors.rgb)
            Fonts.font35.drawString(title,5F,3F,Color(255,255,255,255).rgb)
            Fonts.font40.drawString(content,5F,11F,Color(255,255,255,255).rgb)
            return false
        }
    return false
    }
      
}

//NotifyType Color
enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x60E092)),
    ERROR(Color(0xFF2F2F)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(0x6490A7));
}



enum class FadeState { IN, STAY, OUT, END }


