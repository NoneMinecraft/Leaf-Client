﻿package net.nonemc.leaf.ui.hud.designer

import net.minecraft.client.gui.GuiScreen
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.file.hudConfig
import net.nonemc.leaf.file.saveConfig
import net.nonemc.leaf.ui.hud.element.Element
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import kotlin.math.min

class GuiHudDesigner : GuiScreen() {

    private var editorPanel = EditorPanel(this, 2, 2)

    var selectedElement: Element? = null
    private var buttonAction = false

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        editorPanel = EditorPanel(this, width / 2, height / 2)
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        Leaf.hud.render(true, partialTicks)
        Leaf.hud.handleMouseMove(mouseX, mouseY)

        if (!Leaf.hud.elements.contains(selectedElement)) {
            selectedElement = null
        }

        val wheel = Mouse.getDWheel()

        editorPanel.drawPanel(mouseX, mouseY, wheel)

        if (wheel != 0) {
            for (element in Leaf.hud.elements) {
                if (element.isInBorder(
                        mouseX / element.scale - element.renderX,
                        mouseY / element.scale - element.renderY
                    )
                ) {
                    element.scale = element.scale + if (wheel > 0) 0.05f else -0.05f
                    break
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        if (buttonAction) {
            buttonAction = false
            return
        }

        Leaf.hud.handleMouseClick(mouseX, mouseY, mouseButton)

        if (!(mouseX >= editorPanel.x && mouseX <= editorPanel.x + editorPanel.width && mouseY >= editorPanel.y &&
                    mouseY <= editorPanel.y + min(editorPanel.realHeight, 200))
        ) {
            selectedElement = null
            editorPanel.create = false
        }

        if (mouseButton == 0) {
            for (element in Leaf.hud.elements) {
                if (element.isInBorder(
                        mouseX / element.scale - element.renderX,
                        mouseY / element.scale - element.renderY
                    )
                ) {
                    selectedElement = element
                    break
                }
            }
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)

        Leaf.hud.handleMouseReleased()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        saveConfig(hudConfig)

        super.onGuiClosed()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_DELETE -> if (Keyboard.KEY_DELETE == keyCode && selectedElement != null) {
                Leaf.hud.removeElement(selectedElement!!)
            }

            Keyboard.KEY_ESCAPE -> {
                selectedElement = null
                editorPanel.create = false
            }

            else -> Leaf.hud.handleKey(typedChar, keyCode)
        }

        super.keyTyped(typedChar, keyCode)
    }
}