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
import net.nonemc.leaf.libs.base.MinecraftInstance
import java.io.*
import java.util.*
import javax.imageio.ImageIO

val dir = File(net.nonemc.leaf.libs.base.mc.mcDataDir, "Leaf-Client")

val configDir = File(dir,"config")
val configsDir = File(configDir, "configs")
val soundsDir = File(configDir, "sounds")
val legacySettingsDir = File(configDir, "legacy-settings")
val capesDir = File(configDir, "capes")
val themesDir = File(configDir, "themes")
val legalDir = File(configDir, "legal")
val miscDir = File(configDir, "misc")
val accountsConfig = AccountsConfig(File(miscDir, "accounts.json"))
var friendsConfig = FriendsConfig(File(miscDir, "friends.json"))
val xrayConfig = XRayConfig(File(miscDir, "xray-blocks.json"))
val hudConfig = HudConfig(File(miscDir, "hud.json"))
val subscriptsConfig = ScriptConfig(File(miscDir, "subscripts.json"))
val specialConfig = SpecialConfig(File(miscDir, "special.json"))
val backgroundDir = File(configDir, "background")
val backgroundFile = File(backgroundDir, "background.png")

val dataDir = File(dir, "data")
val mainMenuDir = File(dataDir, "MainMenu")
val scriptDir = File(mainMenuDir, "Scripts")

val neuralNetworkDir = File(dataDir, "NeuralNetwork")
val neuralNetworkModelsDir = File(neuralNetworkDir, "Models")
val neuralNetworkModelDir = File(neuralNetworkModelsDir, "Model")

val storageDir = File(dataDir, "Storage")
val storageRotationDir = File(storageDir, "Rotation")
val storageMiscDir = File(storageDir, "Misc")

fun createData(){
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
    if (!mainMenuDir.exists()) {
        mainMenuDir.mkdirs()
    }
    if (!scriptDir.exists()) {
        scriptDir.mkdirs()
    }
    if (!neuralNetworkDir.exists()) {
        neuralNetworkDir.mkdirs()
    }
    if (!neuralNetworkModelsDir.exists()) {
        neuralNetworkModelsDir.mkdirs()
    }
    if (!neuralNetworkModelDir.exists()) {
        neuralNetworkModelDir.mkdirs()
    }
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    if (!storageMiscDir.exists()) {
        storageMiscDir.mkdirs()
    }
    if (!storageRotationDir.exists()) {
        storageRotationDir.mkdirs()
    }
}

fun createConfig() {
    if (!backgroundDir.exists()) {
        backgroundDir.mkdir()
    }
    if (!configDir.exists()) {
        configDir.mkdir()
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
    if (!miscDir.exists()) {
         miscDir.mkdir()
    }
}

fun loadConfigs(vararg configs: FileManager) {
    for (fileConfig in configs)
        loadConfig(fileConfig)
}

fun loadConfig(config: FileManager) {
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

fun saveConfig(config: FileManager) {
    saveConfig(config, true)
}

fun saveConfig(config: FileManager, ignoreStarting: Boolean) {
    try {
        if (!config.hasConfig()) config.createConfig()
        config.saveConfigFile(config.saveConfig())
    } catch (t: Throwable) {
        println("[FileManager] Failed to save config file: " + config.file.name + ".")
    }
}

fun loadBackground() {
    if (backgroundFile.exists()) {
        try {
            val bufferedImage = ImageIO.read(FileInputStream(backgroundFile)) ?: return
            Leaf.background = ResourceLocation(Leaf.CLIENT_NAME.lowercase(Locale.getDefault()) + "/background.png")
            net.nonemc.leaf.libs.base.mc.textureManager.loadTexture(Leaf.background, DynamicTexture(bufferedImage))
        } catch (e: Exception) {
            println("[FileManager] Failed to load background.")
        }
    }
}

@Throws(IOException::class)
fun loadLegacy(): Boolean {
    var modified = false
    val modulesFile = File(configDir, "modules.json")
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

    val valuesFile = File(configDir, "values.json")
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

    val macrosFile = File(configDir, "macros.json")
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

    val shortcutsFile = File(configDir, "shortcuts.json")
    if (shortcutsFile.exists()) shortcutsFile.delete()
    return modified
}

class FileConfigManager : MinecraftInstance() {
    init {
        createConfig()
        loadBackground()
    }
    companion object {
        val PRETTY_GSON = GsonBuilder().setPrettyPrinting().create()
    }
}
