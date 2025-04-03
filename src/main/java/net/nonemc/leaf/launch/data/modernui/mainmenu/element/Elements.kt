package net.nonemc.leaf.launch.data.modernui.mainmenu.element

import net.minecraft.client.renderer.GlStateManager
import net.nonemc.leaf.utils.entity.ChatUtil
import net.nonemc.leaf.injection.access.StaticStorage
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu.*
import net.nonemc.leaf.launch.data.modernui.mainmenu.utils.Image.drawImage
import net.nonemc.leaf.ui.cape.GuiCapeManager.drawString
import net.nonemc.leaf.utils.mc
import net.nonemc.leaf.utils.render.ColorUtils
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


        fun call(panelPath: String): Any {
            return customCode(
                "Text\\$panelPath",
                config.id,
                scaledMouseX,
                scaledMouseY,
                config.x.toInt(),
                config.y.toInt(),
                config.x.toInt() +   mc.fontRendererObj.getStringWidth(config.text),
                config.y.toInt() +  5
            )
        }

        val result = call("Text-Main")
        if (result as Boolean) {
            val resultX = call("Text-X") as Number
            val resultY = call("Text-Y") as Number
            val resultR = call("Text-R") as Number
            val resultG = call("Text-G") as Number
            val resultB = call("Text-B") as Number
            val resultA = call("Text-A") as Number
            val resultScale = call("Text-Scale") as Number
            GlStateManager.scale(resultScale.toFloat(), resultScale.toFloat(), 1.0f)
            if (config.centered) {
                drawString(
                    mc.fontRendererObj,
                    config.text,
                    (resultX.toInt() / resultScale.toFloat()).toInt(),
                    (resultY.toInt() / resultScale.toFloat()).toInt(),
                    ColorUtils.toRGB(
                        resultR.toInt(), resultG.toInt(), resultB.toInt(), resultA.toInt()
                    )
                )
            } else {
                drawString(
                    mc.fontRendererObj,
                    config.text,
                    resultX.toInt(),
                    resultY.toInt(),
                    ColorUtils.toRGB(
                        resultR.toInt(), resultG.toInt(), resultB.toInt(), resultA.toInt()
                    )
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
            fun call(panelPath: String): Any {
                return customCode(
                    "Image\\$panelPath",
                    config.id,
                    scaledMouseX,
                    scaledMouseY,
                    config.x.toInt(),
                    config.y.toInt(),
                    config.x.toInt() + config.w,
                    config.y.toInt() + config.h
                )
            }
            val result = call("Image-Main")
            if (result as Boolean) {
                val resultX = call("Image-X") as Number
                val resultY = call("Image-Y") as Number
                val resultW = call("Image-W") as Number
                val resultH = call("Image-H") as Number
                val resultA = call("Image-A") as Number

                drawImage(
                    config.path,
                    resultX.toInt(),
                    resultY.toInt(),
                    resultW.toInt(),
                    resultH.toInt(),
                    resultA.toFloat()
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
            val scaleFactor = StaticStorage.scaledResolution.scaleFactor
            val scaledMouseX = mouseX / scaleFactor
            val scaledMouseY = (mc.displayHeight - mouseY) / scaleFactor

            fun call(panelPath: String): Any {
                return customCode(
                    "Panel\\$panelPath",
                    config.id,
                    scaledMouseX,
                    scaledMouseY,
                    config.x.toInt(),
                    config.y.toInt(),
                    config.x2.toInt(),
                    config.y2.toInt()
                )
            }

            val result = call("Panel-Main")
            if (result as Boolean) {
                val resultX = call("Panel-X") as Number
                val resultY = call("Panel-Y") as Number
                val resultRadius = call("Panel-Radius") as Number
                val resultR = call("Panel-R") as Number
                val resultG = call("Panel-G") as Number
                val resultB = call("Panel-B") as Number
                val resultA = call("Panel-A") as Number

                RenderUtils.drawRoundedCornerRect(
                    resultX.toFloat(),
                    resultY.toFloat(),
                    resultX.toFloat() + config.x2,
                    resultY.toFloat() + config.y2,
                    resultRadius.toFloat(),
                    Color(resultR.toInt(), resultG.toInt(), resultB.toInt(), resultA.toInt()).rgb
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun customCode(name:String,id:Int,mouseX: Int, mouseY: Int , elementX1:Int, elementY1:Int, elementX2:Int, elementY2:Int): Any {
    val engine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript") ?: return true
    val formattedExpression = getJs("Leaf-Data\\Scripts\\$name.js")
        .replace("mouseX", mouseX.toString())
        .replace("mouseY", mouseY.toString())
        .replace("elementStartX", elementX1.toString())
        .replace("elementStartY", elementY1.toString())
        .replace("elementEndX", elementX2.toString())
        .replace("elementEndY", elementY2.toString())
        .replace("id", id.toString())
    return try {
        when (val result = engine.eval(formattedExpression)) {
            is Boolean -> result
            is Number -> result
            else -> false
        }
    } catch (e: ScriptException) {
        ChatUtil.ChatPrint("Script Error: ${e.message}")
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