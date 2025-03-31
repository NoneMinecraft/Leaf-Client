package net.nonemc.leaf.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.client.gui.FontRenderer
import net.nonemc.leaf.ui.font.Fonts

class FontValue(valueName: String, value: FontRenderer) : Value<FontRenderer>(valueName, value) {

    override fun toJson(): JsonElement {
        val fontDetails = Fonts.getFontDetails(value)
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails[0] as String)
        valueObject.addProperty("fontSize", fontDetails[1] as Int)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }

    fun set(name: String): Boolean {
        if (name.equals("Minecraft", true)) {
            set(Fonts.minecraftFont)
            return true
        } else if (name.contains(" - ")) {
            val spiced = name.split(" - ")
            set(Fonts.getFontRenderer(spiced[0], spiced[1].toInt()) ?: return false)
            return true
        }
        return false
    }
}