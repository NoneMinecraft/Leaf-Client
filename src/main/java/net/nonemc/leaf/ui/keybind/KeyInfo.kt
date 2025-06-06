﻿package net.nonemc.leaf.ui.keybind

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.macro.Macro
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.font.FontLoaders
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.ui.language.LanguageManager
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.libs.render.RenderUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color


class KeyInfo(
    val posX: Float,
    val posY: Float,
    val width: Float,
    val height: Float,
    val key: Int,
    val keyName: String,
    val keyDisplayName: String,
) : MinecraftInstance() {
    constructor(posX: Float, posY: Float, width: Float, height: Float, key: Int, keyName: String) :
            this(posX, posY, width, height, key, keyName, keyName)

    private val keyColor = Color(240, 240, 240).rgb
    private val shadowColor = Color(210, 210, 210).rgb
    private val unusedColor = Color(200, 200, 200).rgb
    private val usedColor = Color(0, 0, 0).rgb
    private val baseTabHeight = 150
    private val baseTabWidth = 100
    private val direction = posY >= 100

    private var modules = ArrayList<Module>()
    private var macros = ArrayList<Macro>()
    private var hasKeyBind = false
    private var stroll = 0
    private var maxStroll = 0

    fun render() {
        GL11.glPushMatrix()
        GL11.glTranslatef(posX, posY, 0F)

        RenderUtils.drawRect(0F, 0F, width, height, keyColor)
        RenderUtils.drawRect(0F, height * 0.9F, width, height, shadowColor)
        (if (hasKeyBind) {
            Fonts.font40
        } else {
            Fonts.font40
        })
            .drawCenteredString(
                keyName,
                width * 0.5F,
                height * 0.9F * 0.5F - (Fonts.font35.FONT_HEIGHT * 0.5F) + 3F,
                if (hasKeyBind) {
                    usedColor
                } else {
                    unusedColor
                },
                false
            )

        GL11.glPopMatrix()
    }

    fun renderTab() {
        GL11.glPushMatrix()

        GL11.glTranslatef(
            (posX + width * 0.5F) - baseTabWidth * 0.5F, if (direction) {
                posY - baseTabHeight
            } else {
                posY + height
            }, 0F
        )
        RenderUtils.drawRoundedCornerRect(0F, 0F, baseTabWidth.toFloat(), baseTabHeight.toFloat(),3f, Color(255,255,255,160).rgb)

        // render modules
        val fontHeight = 10F - Fonts.font40.height * 0.5F
        var yOffset = (12F + Fonts.font40.height + 10F) - stroll
        for (module in modules) {
            if (yOffset > 0 && (yOffset - 20) < 100) {
                GL11.glPushMatrix()
                GL11.glTranslatef(0F, yOffset, 0F)

                Fonts.font40.drawString(
                    LanguageManager.get(module.localizedName.replace("%", "")),
                    12F,
                    fontHeight,
                    Color.DARK_GRAY.rgb,
                    false
                )
                Fonts.font40.drawString(
                    "-", baseTabWidth - 12F - Fonts.font40.getStringWidth("-"), fontHeight, Color.RED.rgb, false
                )

                GL11.glPopMatrix()
            }
            yOffset += 20
        }
        for (macro in macros) {
            if (yOffset > 0 && (yOffset - 20) < 100) {
                GL11.glPushMatrix()
                GL11.glTranslatef(0F, yOffset, 0F)

                Fonts.font40.drawString(macro.command, 12F, fontHeight, Color.DARK_GRAY.rgb, false)
                Fonts.font40.drawString(
                    "-", baseTabWidth - 12F - Fonts.font40.getStringWidth("-"), fontHeight, Color.RED.rgb, false
                )

                GL11.glPopMatrix()
            }
            yOffset += 20
        }

        // cover the excess
        RenderUtils.drawRoundedCornerRect(0F, 0F, baseTabWidth.toFloat(), 12F + Fonts.font40.height + 10F,3f, Color(255,255,255,160).rgb)
        RenderUtils.drawRoundedCornerRect(
            0F,
            baseTabHeight - 22F - Fonts.font40.height,
            baseTabWidth.toFloat(),
            baseTabHeight.toFloat(),
            3f, Color(255,255,255,160).rgb
        )
        FontLoaders.C18.DisplayFonts(
            LanguageManager.getAndFormat("ui.keybind.key", keyDisplayName),
            12F,
            12F,
            Color.BLACK.rgb,
            FontLoaders.C18
        )
        FontLoaders.C18.DisplayFonts(
            "%ui.keybind.add%",
            baseTabWidth - 12F - Fonts.font40.getStringWidth("%ui.keybind.add%"),
            baseTabHeight - 12F - Fonts.font40.height,
            Color(0, 191, 255).rgb/*sky blue*/,
            FontLoaders.C18
        )

        GL11.glPopMatrix()
    }

    fun stroll(mouseX: Float, mouseY: Float, wheel: Int) {
        val scaledMouseX = mouseX - ((posX + width * 0.5F) - baseTabWidth * 0.5F)
        val scaledMouseY = mouseY - (if (direction) {
            posY - baseTabHeight
        } else {
            posY + height
        })
        if (scaledMouseX < 0 || scaledMouseY < 0 || scaledMouseX > baseTabWidth || scaledMouseY > baseTabHeight) {
            return
        }

        val afterStroll = stroll - (wheel / 40)
        if (afterStroll > 0 && afterStroll < (maxStroll - 150)) {
            stroll = afterStroll
        }
    }

    fun update() {
        modules = Leaf.moduleManager.getKeyBind(key) as ArrayList<Module>
        macros = Leaf.macroManager.macros.filter { it.key == key } as ArrayList<Macro>
        hasKeyBind = (modules.size + macros.size) > 0
        stroll = 0
        maxStroll = modules.size * 30 + macros.size * 30
    }

    fun click(mouseX: Float, mouseY: Float) {
        val keyBindMgr = Leaf.keyBindManager

        if (keyBindMgr.nowDisplayKey == null) {
            keyBindMgr.nowDisplayKey = this
            mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.click"), 1F))
        } else {
            val scaledMouseX = mouseX - ((posX + width * 0.5F) - baseTabWidth * 0.5F)
            val scaledMouseY = mouseY - (if (direction) {
                posY - baseTabHeight
            } else {
                posY + height
            })
            if (scaledMouseX < 0 || scaledMouseY < 0 || scaledMouseX > baseTabWidth || scaledMouseY > baseTabHeight) {
                keyBindMgr.nowDisplayKey = null // close it when click out of area
                return
            }

            if (scaledMouseY > 22F + Fonts.font40.height &&
                scaledMouseX > baseTabWidth - 12F - Fonts.font40.getStringWidth("%ui.keybind.add%")
            ) {
                if (scaledMouseY > baseTabHeight - 22F - Fonts.font40.height) {
                    keyBindMgr.popUI = KeySelectUI(this)
                } else {
                    var yOffset = (12F + Fonts.font40.height + 10F) - stroll
                    for (module in modules) {
                        if (scaledMouseY > (yOffset + 5) && scaledMouseY < (yOffset + 15)) {
                            module.keyBind = Keyboard.KEY_NONE
                            update()
                            break
                        }
                        yOffset += 20
                    }
                    for (macro in macros) {
                        if (scaledMouseY > (yOffset + 5) && scaledMouseY < (yOffset + 15)) {
                            Leaf.macroManager.macros.remove(macro)
                            update()
                            break
                        }
                        yOffset += 20
                    }
                }
            }
        }
    }
}