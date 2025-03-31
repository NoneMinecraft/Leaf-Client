package net.nonemc.leaf.launch.data.modernui.mainmenu.element

import net.minecraft.client.renderer.GlStateManager
import net.nonemc.leaf.features.Util
import net.nonemc.leaf.injection.access.StaticStorage
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu.*
import net.nonemc.leaf.launch.data.modernui.mainmenu.utils.Image.drawImage
import net.nonemc.leaf.ui.cape.GuiCapeManager.drawString
import net.nonemc.leaf.utils.mc
import net.nonemc.leaf.utils.render.RenderUtils
import org.lwjgl.input.Mouse
import java.awt.Color
import java.io.File
import java.io.IOException
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

fun renderCustomTexts() {
    for (config in textConfigs) {
        val mouseX = Mouse.getX()
        val mouseY = Mouse.getY()
        val scaledMouseX: Int = mouseX / StaticStorage.scaledResolution.scaleFactor
        val scaledMouseY: Int = (mc.displayHeight - mouseY) / StaticStorage.scaledResolution.scaleFactor
        GlStateManager.pushMatrix()
        GlStateManager.scale(config.scale, config.scale, 1.0f)

        val scaledX = config.x / config.scale
        val scaledY = config.y / config.scale
        val color = config.colorARGB
        val result = customCode("Text-Main",scaledMouseX, scaledMouseY , config.x.toInt(),config.y.toInt(),config.x.toInt() +10,config.y.toInt()+10.toInt())
        if (config != null && result) {
        if (config.centered) {
            drawString(
                mc.fontRendererObj,
                config.text,
                (scaledX / config.scale).toInt(),
                (scaledY / config.scale).toInt(),
                color
            )
        } else {
            drawString(
                mc.fontRendererObj,
                config.text,
                scaledX.toInt(),
                scaledY.toInt(),
                color
            )
        }
        }

        GlStateManager.popMatrix()
    }
}

fun renderImage() {
    val mouseX = Mouse.getX()
    val mouseY = Mouse.getY()
    val scaledMouseX: Int = mouseX / StaticStorage.scaledResolution.scaleFactor
    val scaledMouseY: Int = (mc.displayHeight - mouseY) / StaticStorage.scaledResolution.scaleFactor

    for (config in imageConfigs) {
        try {
            val result = customCode("Image-Main",scaledMouseX, scaledMouseY , config.x.toInt(),config.y.toInt(),config.x+config.w,config.y+config.h.toInt())
            if (config != null && result) {
                drawImage(
                    config.path,
                    config.x,
                    config.y,
                    config.w,
                    config.h,
                    config.alpha
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun renderCustomPanel() {
    for (config in panelConfigs) {
        try {
            val mouseX = Mouse.getX()
            val mouseY = Mouse.getY()
            val scaledMouseX: Int = mouseX / StaticStorage.scaledResolution.scaleFactor
            val scaledMouseY: Int = (mc.displayHeight - mouseY) / StaticStorage.scaledResolution.scaleFactor
            val result = customCode("Panel-Main",scaledMouseX, scaledMouseY , config.x.toInt(),config.y.toInt(),config.x2.toInt(),config.y2.toInt())
            if (config != null && result) {
                RenderUtils.drawRoundedCornerRect(
                    config.x,
                    config.y,
                    config.x2,
                    config.y2,
                    config.radius,
                    Color(
                        config.red,
                        config.green,
                        config.blue,
                        config.alpha
                    ).rgb
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun customCode(name:String,mouseX: Int, mouseY: Int , elementX1:Int, elementY1:Int, elementX2:Int, elementY2:Int): Boolean {
    val engine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript") ?: return true
    val formattedExpression = getJs("Leaf-Data\\Scripts\\$name.js")
        .replace("mouseX", mouseX.toString())
        .replace("mouseY", mouseY.toString())
        .replace("elementStartX", elementX1.toString())
        .replace("elementStartY", elementY1.toString())
        .replace("elementEndX", elementX2.toString())
        .replace("elementEndY", elementY2.toString())
    return try {
        when (val result = engine.eval(formattedExpression)) {
            is Boolean -> result
            is Number -> result.toInt() != 0
            else -> false
        }
    } catch (e: ScriptException) {
        Util.ChatPrint("Script Error: ${e.message}")
        true
    } catch (e: NullPointerException) {
        true
    }
}

fun renderCustomButton(mouseX: Int, mouseY: Int) {
    if (draggingButton != null && Mouse.isButtonDown(1)) {
        draggingButton.setPosition(
            mouseX - dragOffsetX,
            mouseY - dragOffsetY
        )
    }
    for (button in buttons) {
        button.draw(mouseX, mouseY)
    }
}


fun getJs(filePath: String): String {
    val file = File(filePath)
    if (file.exists() && file.extension == "js") {
        try {
            return file.readText()
        } catch (e: IOException) {
            println(e.message)
        }
    }
    return ""
}