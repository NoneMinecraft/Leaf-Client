﻿package net.nonemc.leaf.ui.hud.element.elements

import com.google.gson.JsonElement
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.libs.file.openFileChooser
import net.nonemc.leaf.libs.random.randomNumber
import net.nonemc.leaf.ui.hud.element.Border
import net.nonemc.leaf.ui.hud.element.Element
import net.nonemc.leaf.ui.hud.element.ElementInfo
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.value.TextValue
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JOptionPane

/**
 * CustomHUD ai element
 *
 * Draw custom ai
 */
@ElementInfo(name = "Image")
class Image : Element() {

    companion object {

        /**
         * Create default element
         */
        fun default(): Image {
            val image = Image()

            image.x = 0.0
            image.y = 0.0

            return image
        }
    }

    private val image: TextValue = object : TextValue("Image", "") {

        override fun fromJson(element: JsonElement) {
            super.fromJson(element)

            if (get().isEmpty()) {
                return
            }

            setImage(get())
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (get().isEmpty()) {
                return
            }

            setImage(get())
        }
    }

    private val resourceLocation = ResourceLocation(randomNumber(128))
    private var width = 64
    private var height = 64

    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border {
        RenderUtils.drawImage(resourceLocation, 0, 0, width / 2, height / 2)

        return Border(0F, 0F, width / 2F, height / 2F)
    }

    override fun createElement(): Boolean {
        val file = openFileChooser() ?: return false

        if (!file.exists()) {
            showErrorPopup("Error", "The file does not exist.")
            return false
        }

        if (file.isDirectory) {
            showErrorPopup("Error", "The file is a directory.")
            return false
        }

        setImage(file)
        return true
    }
    fun showErrorPopup(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
    }
    private fun setImage(image: String): Image {
        try {
            this.image.changeValue(image)

            val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(image))
            val bufferedImage = ImageIO.read(byteArrayInputStream)
            byteArrayInputStream.close()

            width = bufferedImage.width
            height = bufferedImage.height

            mc.textureManager.loadTexture(resourceLocation, DynamicTexture(bufferedImage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setImage(image: File): Image {
        try {
            setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath())))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }
}