﻿package net.nonemc.leaf.ui.hud.element.elements

import net.minecraft.client.Minecraft
import net.minecraft.util.ChatAllowedCharacters
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.libs.extensions.ping
import net.nonemc.leaf.ui.hud.designer.GuiHudDesigner
import net.nonemc.leaf.ui.hud.element.Border
import net.nonemc.leaf.ui.hud.element.Element
import net.nonemc.leaf.ui.hud.element.ElementInfo
import net.nonemc.leaf.ui.hud.element.Side
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.libs.mouse.Click
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.session.SessionLib
import net.nonemc.leaf.libs.render.ColorUtils
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.value.*
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text", blur = true)
class Text(
    x: Double = 10.0,
    y: Double = 10.0,
    scale: Float = 1F,
    side: Side = Side.default(),
) : Element(x, y, scale, side) {

    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("#.##")
        val NO_DECIMAL_FORMAT = DecimalFormat("#")
    }

    val displayString = TextValue("DisplayText", "")
    val textStyle = ListValue("Text-Style", arrayOf("Default"), "Default")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow"), "Custom")
    private val shadow = BoolValue("Shadow", false)
    val rectValue = ListValue("Rect", arrayOf("Normal", "RNormal", "OneTap", "Skeet", "Rounded", "None"), "None")
    val rectColorModeValue =
        ListValue("RectColor", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow"), "Custom")
    private val rectRedValue = IntegerValue("RectRed", 0, 0, 255)
    private val rectGreenValue = IntegerValue("RectGreen", 0, 0, 255)
    private val rectBlueValue = IntegerValue("RectBlue", 0, 0, 255)
    private val rectAlphaValue = IntegerValue("RectAlpha", 255, 0, 255)
    private val rectExpandValue = FloatValue("RectExpand", 0.3F, 0F, 1F)
    private val rectRoundValue = FloatValue("RectRoundingMultiplier", 1.5F, 0.1F, 4F)
    private val rainbowSpeed = IntegerValue("RainbowSpeed", 10, 1, 10)
    private val rainbowIndex = IntegerValue("RainbowIndex", 1, 1, 20)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode) {
                "Text Element"
            } else {
                displayString.get()
            }

            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer.posZ)
                "xpos" -> return NO_DECIMAL_FORMAT.format(mc.thePlayer.posX)
                "ypos" -> return NO_DECIMAL_FORMAT.format(mc.thePlayer.posY)
                "zpos" -> return NO_DECIMAL_FORMAT.format(mc.thePlayer.posZ)
                "xdp" -> return mc.thePlayer.posX.toString()
                "ydp" -> return mc.thePlayer.posY.toString()
                "zdp" -> return mc.thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ))
                "ping" -> return "${mc.thePlayer.ping}"
                "speed" -> return DECIMAL_FORMAT.format(EntityMoveLib.bps)
                "bps" -> return DECIMAL_FORMAT.format(EntityMoveLib.bps)
                "health" -> return DECIMAL_FORMAT.format(mc.thePlayer.health)
                "yaw" -> return DECIMAL_FORMAT.format(mc.thePlayer.rotationYaw)
                "pitch" -> return DECIMAL_FORMAT.format(mc.thePlayer.rotationPitch)
                "attackDist" -> return if (Leaf.combatManager.target != null) mc.thePlayer.getDistanceToEntity(Leaf.combatManager.target)
                    .toString() + " Blocks" else "Hasn't attacked"
            }
        }

        return when (str) {
            "playtime" -> {
                if (mc.isSingleplayer) {
                    "Singleplayer"
                } else {
                    SessionLib.getFormatSessionTime()
                }
            }
            "username" -> mc.getSession().username
            "clientName" -> Leaf.CLIENT_NAME
            "clientVersion" -> Leaf.CLIENT_VERSION
            "clientCreator" -> Leaf.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> getRemoteIp()
            "cps", "lcps" -> return Click.getCPS(Click.MouseButton.LEFT).toString()
            "mcps" -> return Click.getCPS(Click.MouseButton.MIDDLE).toString()
            "rcps" -> return Click.getCPS(Click.MouseButton.RIGHT).toString()
            else -> null // Null = don't replace
        }
    }
    private fun getRemoteIp(): String {
        var serverIp = "Idling"

        if (mc.isIntegratedServerRunning) {
            serverIp = "SinglePlayer"
        } else if (mc.theWorld != null && mc.theWorld.isRemote) {
            val serverData = mc.currentServerData
            if (serverData != null) serverIp = serverData.serverIP
        }

        return serverIp
    }
    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    override fun drawElement(partialTicks: Float): Border {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get())

        val fontRenderer = fontValue.get()


        val rectColor = when (rectColorModeValue.get().lowercase()) {
            "rainbow" -> ColorUtils.hslRainbow(rainbowIndex.get(), indexOffset = 100 * rainbowSpeed.get()).rgb
            "skyrainbow" -> ColorUtils.skyRainbow(rainbowIndex.get(), 1F, 1F, rainbowSpeed.get().toDouble()).rgb
            "anotherrainbow" -> ColorUtils.fade(
                Color(
                    rectRedValue.get(),
                    rectGreenValue.get(),
                    rectBlueValue.get(),
                    rectAlphaValue.get()
                ), 100, rainbowIndex.get()
            ).rgb

            else -> Color(rectRedValue.get(), rectGreenValue.get(), rectBlueValue.get(), rectAlphaValue.get()).rgb
        }
        val expand = fontRenderer.FONT_HEIGHT * rectExpandValue.get()
        when (rectValue.get().lowercase()) {
            "normal" -> {
                RenderUtils.drawRect(
                    -expand,
                    -expand,
                    fontRenderer.getStringWidth(displayText) + expand,
                    fontRenderer.FONT_HEIGHT + expand,
                    rectColor
                )
            }

            "rounded" -> {
                RenderUtils.drawRoundedCornerRect(
                    -expand,
                    -expand,
                    fontRenderer.getStringWidth(displayText) + expand,
                    fontRenderer.FONT_HEIGHT + expand,
                    2 + (expand / 4) * rectRoundValue.get(),
                    rectColor
                )
            }

            "rnormal" -> {
                RenderUtils.drawRect(
                    -expand,
                    -expand - 1,
                    fontRenderer.getStringWidth(displayText) + expand,
                    -expand,
                    ColorUtils.rainbow()
                )
                RenderUtils.drawRect(
                    -expand,
                    -expand,
                    fontRenderer.getStringWidth(displayText) + expand,
                    fontRenderer.FONT_HEIGHT + expand,
                    rectColor
                )
            }

            "onetap" -> {
                RenderUtils.drawRect(
                    -4.0f,
                    -8.0f,
                    (fontRenderer.getStringWidth(displayText) + 3).toFloat(),
                    fontRenderer.FONT_HEIGHT.toFloat(),
                    Color(43, 43, 43).rgb
                )
                RenderUtils.drawGradientSidewaysH(
                    -3.0,
                    -7.0,
                    fontRenderer.getStringWidth(displayText) + 2.0,
                    -3.0,
                    Color(rectColor).darker().rgb,
                    rectColor
                )
            }

            "skeet" -> {
                RenderUtils.drawRect(
                    -11.0,
                    -11.0,
                    (fontRenderer.getStringWidth(displayText) + 10).toDouble(),
                    fontRenderer.FONT_HEIGHT.toDouble() + 8.0,
                    Color(0, 0, 0).rgb
                )
                RenderUtils.drawOutLineRect(
                    -10.0,
                    -10.0,
                    (fontRenderer.getStringWidth(displayText) + 9).toDouble(),
                    fontRenderer.FONT_HEIGHT.toDouble() + 7.0,
                    8.0,
                    Color(59, 59, 59).rgb,
                    Color(59, 59, 59).rgb
                )
                RenderUtils.drawOutLineRect(
                    -9.0,
                    -9.0,
                    (fontRenderer.getStringWidth(displayText) + 8).toDouble(),
                    fontRenderer.FONT_HEIGHT.toDouble() + 6.0,
                    4.0,
                    Color(59, 59, 59).rgb,
                    Color(40, 40, 40).rgb
                )
                RenderUtils.drawOutLineRect(
                    -4.0,
                    -4.0,
                    (fontRenderer.getStringWidth(displayText) + 3).toDouble(),
                    fontRenderer.FONT_HEIGHT.toDouble() + 1.0,
                    1.0,
                    Color(18, 18, 18).rgb,
                    Color(0, 0, 0).rgb
                )
            }
        }
        if (textStyle.get().contains("Default")) {
            fontRenderer.drawString(
                displayText, 0F, 0F, when (colorModeValue.get().lowercase()) {
                    "rainbow" -> ColorUtils.hslRainbow(rainbowIndex.get(), indexOffset = 100 * rainbowSpeed.get()).rgb
                    "skyrainbow" -> ColorUtils.skyRainbow(rainbowIndex.get(), 1F, 1F, rainbowSpeed.get().toDouble()).rgb
                    "anotherrainbow" -> ColorUtils.fade(color, 100, rainbowIndex.get()).rgb
                    else -> color.rgb
                }, shadow.get()
            )
        }

        if (editMode && mc.currentScreen is GuiHudDesigner && editTicks <= 40) {
            fontRenderer.drawString(
                "_", fontRenderer.getStringWidth(displayText) + 2F,
                0F, Color.WHITE.rgb, shadow.get()
            )
        }

        if (editMode && mc.currentScreen !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }

        return Border(
            -2F,
            -2F,
            fontRenderer.getStringWidth(displayText) + 2F,
            fontRenderer.FONT_HEIGHT.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L) {
                editMode = true
            }

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty()) {
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))
                }

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§') {
                displayString.set(displayString.get() + c)
            }

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }
}
