package net.nonemc.leaf.file.configs

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.special.*
import net.nonemc.leaf.file.FileManager
import net.nonemc.leaf.file.FileConfigManager
import net.nonemc.leaf.ui.mainmenu.GuiBackground
import net.nonemc.leaf.ui.altmanager.GuiAltManager
import java.io.File

class SpecialConfig(file: File) : FileManager(file) {
    var useGlyphFontRenderer = true

    override fun loadConfig(config: String) {
        val json = JsonParser().parse(config).asJsonObject

        Leaf.commandManager.prefix = '.'
        AutoReconnect.delay = 5000
        AntiForge.enabled = true
        AntiForge.blockFML = true
        AntiForge.blockProxyPacket = true
        AntiForge.blockPayloadPackets = true
        GuiBackground.enabled = true
        GuiBackground.particles = false
        GuiAltManager.randomAltField.text = "F%nD%nP%n_%s%s%s"
        useGlyphFontRenderer = true

        if (json.has("prefix")) {
            Leaf.commandManager.prefix = json.get("prefix").asCharacter
        }
        if (json.has("auto-reconnect")) {
            AutoReconnect.delay = json.get("auto-reconnect").asInt
        }
        if (json.has("alt-field")) {
            GuiAltManager.randomAltField.text = json.get("alt-field").asString
        }
        if (json.has("use-glyph-fontrenderer")) {
            useGlyphFontRenderer = json.get("use-glyph-fontrenderer").asBoolean
        }
        if (json.has("anti-forge")) {
            val jsonValue = json.getAsJsonObject("anti-forge")

            if (jsonValue.has("enable")) {
                AntiForge.enabled = jsonValue.get("enable").asBoolean
            }
            if (jsonValue.has("block-fml")) {
                AntiForge.blockFML = jsonValue.get("block-fml").asBoolean
            }
            if (jsonValue.has("block-proxy")) {
                AntiForge.blockProxyPacket = jsonValue.get("block-proxy").asBoolean
            }
            if (jsonValue.has("block-payload")) {
                AntiForge.blockPayloadPackets = jsonValue.get("block-payload").asBoolean
            }
        }

        if (json.has("background")) {
            val jsonValue = json.getAsJsonObject("background")

            if (jsonValue.has("enable")) {
                GuiBackground.enabled = jsonValue.get("enable").asBoolean
            }
            if (jsonValue.has("particles")) {
                GuiBackground.particles = jsonValue.get("particles").asBoolean
            }
        }
    }

    override fun saveConfig(): String {
        val json = JsonObject()

        json.addProperty("prefix", Leaf.commandManager.prefix)
        json.addProperty("auto-reconnect", AutoReconnect.delay)
        json.addProperty("alt-field", GuiAltManager.randomAltField.text)
        json.addProperty("use-glyph-fontrenderer", useGlyphFontRenderer)

        val antiForgeJson = JsonObject()
        antiForgeJson.addProperty("enable", AntiForge.enabled)
        antiForgeJson.addProperty("block-fml", AntiForge.blockFML)
        antiForgeJson.addProperty("block-proxy", AntiForge.blockProxyPacket)
        antiForgeJson.addProperty("block-payload", AntiForge.blockPayloadPackets)
        json.add("anti-forge", antiForgeJson)

        val backgroundJson = JsonObject()
        backgroundJson.addProperty("enable", GuiBackground.enabled)
        backgroundJson.addProperty("particles", GuiBackground.particles)
        json.add("background", backgroundJson)

        return FileConfigManager.PRETTY_GSON.toJson(json)
    }
}