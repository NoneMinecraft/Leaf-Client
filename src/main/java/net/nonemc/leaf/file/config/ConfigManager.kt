package net.nonemc.leaf.file.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.client.Target
import net.nonemc.leaf.file.*
import net.nonemc.leaf.libs.clazz.ClassReflect
import net.nonemc.leaf.libs.string.StringLib
import net.nonemc.leaf.value.*
import org.lwjgl.input.Keyboard
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.concurrent.schedule

class ConfigManager {
    private val configSetFile = File(configDir, "config-settings.json")

    private val sections = mutableListOf<ConfigSection>()

    var nowConfig = "default"
    private var nowConfigInFile = "default"
    var configFile = File(configsDir, "$nowConfig.json")
    var needSave = false

    init {
        ClassReflect.resolvePackage("${this.javaClass.`package`.name}.sections", ConfigSection::class.java)
            .forEach(this::registerSection)
        Timer().schedule(30000, 30000) {
            saveTicker()
        }
    }

    fun load(name: String, save: Boolean = true) {
        Leaf.isLoadingConfig = true
        if (save && nowConfig != name) {
            save(true, true)
        }

        nowConfig = name
        configFile = File(configsDir, "$nowConfig.json")

        val json = if (configFile.exists()) {
            JsonParser().parse(configFile.reader(Charsets.UTF_8)).asJsonObject
        } else {
            JsonObject()
        }
        for (section in sections) {
            section.load(
                if (json.has(section.sectionName)) {
                    json.getAsJsonObject(section.sectionName)
                } else {
                    JsonObject()
                }
            )
        }

        if (!configFile.exists()) {
            save(forceSave = true)
        }

        if (save) {
            saveConfigSet()
        }
        Leaf.isLoadingConfig = false
    }

    fun save(saveConfigSet: Boolean = nowConfigInFile != nowConfig, forceSave: Boolean = false) {
        val config = JsonObject()

        for (section in sections) {
            config.add(section.sectionName, section.save())
        }

        configFile.writeText(FileConfigManager.PRETTY_GSON.toJson(config), Charsets.UTF_8)

        if (saveConfigSet || forceSave) {
            saveConfigSet()
        }
        needSave = false
    }

    private fun saveTicker() {
        if (!needSave) {
            return
        }
        save()
    }

    fun smartSave() {
        needSave = true
    }

    fun loadConfigSet() {
        val configSet = if (configSetFile.exists()) {
            JsonParser().parse(configSetFile.reader(Charsets.UTF_8)).asJsonObject
        } else {
            JsonObject()
        }

        nowConfigInFile = if (configSet.has("file")) {
            configSet.get("file").asString
        } else {
            "default"
        }

        load(nowConfigInFile, false)
    }

    fun saveConfigSet() {
        val configSet = JsonObject()

        configSet.addProperty("file", nowConfig)

        configSetFile.writeText(FileConfigManager.PRETTY_GSON.toJson(configSet), Charsets.UTF_8)
    }

    fun loadLegacySupport() {
        if (loadLegacy()) {
            if (File(configsDir, "$nowConfig.json").exists()) {
                nowConfig = "legacy"
                configFile = File(configsDir, "$nowConfig.json")
                save(forceSave = true)
            } else {
                save(forceSave = true)
            }
        }

        fun executeScript(script: String) {
            script.lines().filter { it.isNotEmpty() && !it.startsWith('#') }.forEachIndexed { _, s ->
                val args = s.split(" ").toTypedArray()

                if (args.size <= 1) {
                    return@forEachIndexed
                }

                when (args[0]) {
                    "load" -> {
                        val url = StringLib.toCompleteString(args, 1)
                        try {
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    "targetPlayer", "targetPlayers" -> {
                        Target.playerValue.set(args[1].equals("true", ignoreCase = true))
                    }

                    "targetMobs" -> {
                        Target.mobValue.set(args[1].equals("true", ignoreCase = true))
                    }

                    "targetAnimals" -> {
                        Target.animalValue.set(args[1].equals("true", ignoreCase = true))
                    }

                    "targetInvisible" -> {
                        Target.invisibleValue.set(args[1].equals("true", ignoreCase = true))
                    }

                    "targetDead" -> {
                        Target.deadValue.set(args[1].equals("true", ignoreCase = true))
                    }

                    else -> {
                        if (args.size != 3) {
                            return@forEachIndexed
                        }

                        val moduleName = args[0]
                        val valueName = args[1]
                        val value = args[2]
                        val module = Leaf.moduleManager.getModule(moduleName) ?: return@forEachIndexed

                        if (valueName.equals("toggle", ignoreCase = true)) {
                            module.state = value.equals("true", ignoreCase = true)
                            return@forEachIndexed
                        }

                        if (valueName.equals("bind", ignoreCase = true)) {
                            module.keyBind = Keyboard.getKeyIndex(value)
                            return@forEachIndexed
                        }

                        val moduleValue = module.getValue(valueName) ?: return@forEachIndexed

                        try {
                            when (moduleValue) {
                                is BoolValue -> moduleValue.changeValue(value.toBoolean())
                                is FloatValue -> moduleValue.changeValue(value.toFloat())
                                is IntegerValue -> moduleValue.changeValue(value.toInt())
                                is TextValue -> moduleValue.changeValue(value)
                                is ListValue -> moduleValue.changeValue(value)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        val oldSettingDir = File(configDir, "settings")
        if (oldSettingDir.exists()) {
            oldSettingDir.listFiles()?.forEach {
                if (it.isFile) {
                    val name = nowConfig
                    load("default", false)
                    nowConfig = it.name
                    configFile = File(configsDir, "$nowConfig.json")
                    executeScript(String(Files.readAllBytes(it.toPath())))
                    save(false, true)
                    // set data back
                    nowConfig = name
                    configFile = File(configsDir, "$nowConfig.json")
                    saveConfigSet()
                }
                if (!legacySettingsDir.exists()) {
                    legacySettingsDir.mkdir()
                }

                it.renameTo(File(legacySettingsDir, it.name))
            }
            oldSettingDir.delete()
        }
    }
    fun registerSection(section: ConfigSection) = sections.add(section)

    private fun registerSection(sectionClass: Class<out ConfigSection>) {
        try {
            registerSection(sectionClass.newInstance())
        } catch (e: Throwable) {
            println("Failed to load config section: ${sectionClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }
}