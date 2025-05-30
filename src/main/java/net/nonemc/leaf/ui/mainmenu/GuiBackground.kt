﻿package net.nonemc.leaf.ui.mainmenu

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.file.backgroundDir
import net.nonemc.leaf.file.backgroundFile
import net.nonemc.leaf.file.saveConfig
import net.nonemc.leaf.file.specialConfig
import net.nonemc.leaf.libs.extensions.drawCenteredString
import net.nonemc.leaf.libs.file.openFileChooser
import net.nonemc.leaf.libs.ai.downloadImage
import net.nonemc.leaf.libs.ai.getUrl
import net.nonemc.leaf.libs.ai.json.loadAIImageJson
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.JOptionPane

class GuiBackground(val prevGui: GuiScreen) : GuiScreen() {
    companion object {
        var enabled = true
        var particles = false
        var blur = false
    }

    private lateinit var enabledButton: GuiButton
    private lateinit var blurButton: GuiButton
    private lateinit var particlesButton: GuiButton
    private lateinit var typeButton: GuiButton
    private lateinit var lastButton: GuiButton
    private lateinit var nextButton: GuiButton
    private lateinit var animatedButton: GuiButton

    override fun initGui() {
        enabledButton = GuiButton(1, width / 2 - 100, height / 4 + 35, "")
        buttonList.add(enabledButton)
        blurButton = GuiButton(9, width / 2 - 100, height / 4, "")
        buttonList.add(blurButton)

        typeButton = GuiButton(5, width / 2 - 100, height / 4 + 40 + 25, "")
        buttonList.add(typeButton)
        lastButton = GuiButton(6, width / 2 - 100, height / 4 + 40 + 25 * 2, 20, 20, "<")
        buttonList.add(lastButton)
        nextButton = GuiButton(7, width / 2 + 80, height / 4 + 40 + 25 * 2, 20, 20, ">")
        buttonList.add(nextButton)
        animatedButton = GuiButton(8, width / 2 - 100, height / 4 + 40 + 25 * 3, "")
        buttonList.add(animatedButton)

        particlesButton = GuiButton(2, width / 2 - 100, height / 4 + 40 + 25 * 4, "")
        buttonList.add(particlesButton)
        buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 40 + 25 * 5, 98, 20, "%ui.background.change%"))
        buttonList.add(GuiButton(4, width / 2 + 2, height / 4 + 40 + 25 * 5, 98, 20, "%ui.background.reset%"))

        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 40 + 25 * 6 + 10, "%ui.back%"))

        updateButtons()
    }

    private fun updateButtons() {
        enabledButton.displayString = "%ui.status% (${if (enabled) "%ui.on%" else "%ui.off%"})"
        blurButton.displayString = "Background Blur (${if (blur) "%ui.on%" else "%ui.off%"})"
        particlesButton.displayString = "%ui.background.particles% (${if (particles) "%ui.on%" else "%ui.off%"})"
        val hasCustomBackground = Leaf.background != null
        lastButton.enabled = !hasCustomBackground
        nextButton.enabled = !hasCustomBackground
        typeButton.enabled = !hasCustomBackground
        animatedButton.enabled = !hasCustomBackground
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> {
                enabled = !enabled
            }

            9 -> {
                blur = !blur
            }

            2 -> {
                particles = !particles
            }

            3 -> {
                val file = openFileChooser() ?: return
                if (file.isDirectory) return

                try {
                    Files.copy(file.toPath(), FileOutputStream(backgroundFile))

                    val image = ImageIO.read(FileInputStream(backgroundFile))
                    Leaf.background = ResourceLocation(Leaf.CLIENT_NAME.lowercase() + "/background.png")
                    mc.textureManager.loadTexture(Leaf.background, DynamicTexture(image))
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorPopup(
                        "Error",
                        "Exception class: " + e.javaClass.name + "\nMessage: " + e.message
                    )
                    backgroundFile.delete()
                }
            }

            4 -> {
                Leaf.background = null
                backgroundFile.delete()
            }

            0 -> mc.displayGuiScreen(prevGui)
        }

        updateButtons()

        saveConfig(specialConfig)
    }
    fun showErrorPopup(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        mc.fontRendererObj.drawCenteredString("%ui.background%", this.width / 2F, height / 8F + 5F, 4673984, true)
        mc.fontRendererObj.drawCenteredString(
            "%ui.background.gcurrent%: Customized",
            this.width / 2F,
            height / 4 + 40 + 25 * 2f + (20 - mc.fontRendererObj.FONT_HEIGHT) * 0.5f,
            Color.WHITE.rgb,
            true
        )

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}
fun getAIBackGround(w: Int, h: Int) {
    val imageUrl = loadAIImageJson(File(backgroundDir,"AIBackGround.json"))?.let { getUrl(it, w, h) }
    imageUrl?.let { downloadImage(it, backgroundFile) }
}