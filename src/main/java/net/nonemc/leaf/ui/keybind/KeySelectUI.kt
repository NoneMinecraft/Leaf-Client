﻿package net.nonemc.leaf.ui.keybind

import net.minecraft.util.ChatAllowedCharacters
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.macro.Macro
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.ui.language.LanguageManager
import net.nonemc.leaf.libs.render.RenderUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class KeySelectUI(val info: KeyInfo) : PopUI(LanguageManager.get("ui.keybind.select")) {
    private var str = ""
    private var modules = Leaf.moduleManager.modules.toList()
    private val singleHeight = 4F + Fonts.font35.height
    private var stroll = 0
    private var maxStroll = modules.size * singleHeight
    private val height = 8F + Fonts.font40.height + Fonts.font35.height + 0.5F

    override fun render() {
        // modules
        var yOffset = height - stroll + 5F
        if (str.startsWith(".")) {
            Fonts.font35.drawString(
                LanguageManager.get("ui.keybind.addMacro"),
                8F,
                singleHeight + yOffset,
                Color.BLACK.rgb,
                false
            )
        } else {
            for (module in modules) {
                if (yOffset > (height - singleHeight) && (yOffset - singleHeight) < 190) {
                    GL11.glPushMatrix()
                    GL11.glTranslatef(0F, yOffset, 0F)

                    val name = module.name
                    Fonts.font35.drawString(
                        if (str.isNotEmpty()) {
                            "§0" + name.substring(0, str.length) + "§7" + name.substring(str.length, name.length)
                        } else {
                            "§0$name"
                        }, 8F, singleHeight * 0.5F, Color.BLACK.rgb, false
                    )

                    GL11.glPopMatrix()
                }
                yOffset += singleHeight
            }
        }
        RenderUtils.drawRoundedCornerRect(0F, 8F + Fonts.font40.height, baseWidth.toFloat(), height + 5F, 3f ,Color(255,255,255,160).rgb)
        // search bar
        Fonts.font35.drawString(
            str.ifEmpty { LanguageManager.get("ui.keybind.search") },
            8F,
            8F + Fonts.font40.height + 4F,
            Color.LIGHT_GRAY.rgb,
            false
        )
    }

    override fun key(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_BACK) {
            if (str.isNotEmpty()) {
                str = str.substring(0, str.length - 1)
                update()
            }
            return
        } else if (keyCode == Keyboard.KEY_RETURN) {
            if (str.startsWith(".")) {
                Leaf.macroManager.macros.add(Macro(info.key, str))
                Leaf.keyBindManager.updateAllKeys()
                close()
            } else if (modules.isNotEmpty()) {
                apply(modules[0])
            }
            return
        }

        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            str += typedChar
            update()
        }
    }

    override fun stroll(mouseX: Float, mouseY: Float, wheel: Int) {
        val afterStroll = stroll - (wheel / 10)
        if (afterStroll > 0 && afterStroll < (maxStroll - 100)) {
            stroll = afterStroll
        }
    }

    override fun click(mouseX: Float, mouseY: Float) {
        if (mouseX < 8 || mouseX > (baseWidth - 8) || mouseY < height || mouseY > (baseHeight - singleHeight)) {
            return
        }

        var yOffset = height - stroll + 2F
        for (module in modules) {
            if (mouseY > yOffset && mouseY < (yOffset + singleHeight)) {
                apply(module)
                break
            }
            yOffset += singleHeight
        }
    }

    private fun apply(module: Module) {
        module.keyBind = info.key
        Leaf.keyBindManager.updateAllKeys()
        close()
    }

    override fun close() {
        Leaf.keyBindManager.popUI = null
    }

    private fun update() {
        modules = if (str.isNotEmpty()) {
            Leaf.moduleManager.modules.filter { it.name.startsWith(str, ignoreCase = true) }
        } else {
            Leaf.moduleManager.modules.toList()
        }
        maxStroll = modules.size * singleHeight
        stroll = 0
    }
}