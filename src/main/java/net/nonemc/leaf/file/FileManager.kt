package net.nonemc.leaf.file

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.macro.Macro
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.file.configs.*
import net.nonemc.leaf.utils.MinecraftInstance
import java.io.*
import java.util.*
import javax.imageio.ImageIO

class FileManager : MinecraftInstance() {
    val dir = File(mc.mcDataDir, "Leaf Client")
    val fontsDir = File(dir, "fonts")
    val configsDir = File(dir, "configs")
    val soundsDir = File(dir, "sounds")
    val legacySettingsDir = File(dir, "legacy-settings")
    val capesDir = File(dir, "capes")
    val themesDir = File(dir, "themes")
    val legalDir = File(dir, "legal")
    val accountsConfig = AccountsConfig(File(dir, "accounts.json"))
    var friendsConfig = FriendsConfig(File(dir, "friends.json"))
    val xrayConfig = XRayConfig(File(dir, "xray-blocks.json"))
    val hudConfig = HudConfig(File(dir, "hud.json"))
    val subscriptsConfig = ScriptConfig(File(dir, "subscripts.json"))
    val specialConfig = SpecialConfig(File(dir, "special.json"))
    val backgroundFile = File(dir, "background.png")

    init {
        setupFolder()
        loadBackground()
    }

    fun setupFolder() {
        if (!dir.exists()) {
            dir.mkdir()
        }

        if (!fontsDir.exists()) {
            fontsDir.mkdir()
        }

        if (!configsDir.exists()) {
            configsDir.mkdir()
        }

        if (!soundsDir.exists()) {
            soundsDir.mkdir()
        }

        if (!legalDir.exists()) {
            legalDir.mkdir()
        }

        if (!capesDir.exists()) {
            capesDir.mkdir()
        }

        if (!themesDir.exists()) {
            themesDir.mkdir()
        }

    }
    fun loadConfigs(vararg configs: FileConfig) {
        for (fileConfig in configs)
            loadConfig(fileConfig)
    }
    fun loadConfig(config: FileConfig) {
        if (!config.hasConfig()) {
            saveConfig(config, true)
            return
        }
        try {
            config.loadConfig(config.loadConfigFile())
        } catch (t: Throwable) {
           println("[FileManager] Failed to load config file: " + config.file.name + ".")
        }
    }

    fun saveConfig(config: FileConfig) {
        saveConfig(config, true)
    }

    private fun saveConfig(config: FileConfig, ignoreStarting: Boolean) {
        if (!ignoreStarting && Leaf.isStarting) return
        try {
            if (!config.hasConfig()) config.createConfig()
            config.saveConfigFile(config.saveConfig())
        } catch (t: Throwable) {
           println("[FileManager] Failed to save config file: " + config.file.name + ".")
        }
    }

    /**
     * Load background for background
     */
    private fun loadBackground() {
        if (backgroundFile.exists()) {
            try {
                val bufferedImage = ImageIO.read(FileInputStream(backgroundFile)) ?: return
                Leaf.background = ResourceLocation(Leaf.CLIENT_NAME.lowercase(Locale.getDefault()) + "/background.png")
                mc.textureManager.loadTexture(Leaf.background, DynamicTexture(bufferedImage))
            } catch (e: Exception) {
                println("[FileManager] Failed to load background.")
            }
        }
    }

    @Throws(IOException::class)
    fun loadLegacy(): Boolean {
        var modified = false
        val modulesFile = File(dir, "modules.json")
        if (modulesFile.exists()) {
            modified = true
            val fr = FileReader(modulesFile)
            try {
                val jsonElement = JsonParser().parse(BufferedReader(fr))
                for ((key, value) in jsonElement.asJsonObject.entrySet()) {
                    val module = Leaf.moduleManager.getModule(key)
                    if (module != null) {
                        val jsonModule = value as JsonObject
                        module.state = jsonModule["State"].asBoolean
                        module.keyBind = jsonModule["KeyBind"].asInt
                        if (jsonModule.has("Array")) module.array = jsonModule["Array"].asBoolean
                        if (jsonModule.has("AutoDisable")) module.autoDisable =
                            EnumAutoDisableType.valueOf(jsonModule["AutoDisable"].asString)
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            try {
                fr.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val valuesFile = File(dir, "values.json")
        if (valuesFile.exists()) {
            modified = true
            val fr = FileReader(valuesFile)
            try {
                val jsonObject = JsonParser().parse(BufferedReader(fr)).asJsonObject
                for ((key, value) in jsonObject.entrySet()) {
                    val module = Leaf.moduleManager.getModule(key)
                    if (module != null) {
                        val jsonModule = value as JsonObject
                        for (moduleValue in module.values) {
                            val element = jsonModule[moduleValue.name]
                            if (element != null) moduleValue.fromJson(element)
                        }
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            try {
                fr.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val macrosFile = File(dir, "macros.json")
        if (macrosFile.exists()) {
            modified = true
            val fr = FileReader(macrosFile)
            try {
                val jsonArray = JsonParser().parse(BufferedReader(fr)).asJsonArray
                for (jsonElement in jsonArray) {
                    val macroJson = jsonElement.asJsonObject
                    Leaf.macroManager.macros
                        .add(Macro(macroJson["key"].asInt, macroJson["command"].asString))
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            try {
                fr.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val shortcutsFile = File(dir, "shortcuts.json")
        if (shortcutsFile.exists()) shortcutsFile.delete()

        return modified
    }

    companion object {
        val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()
    }
}
