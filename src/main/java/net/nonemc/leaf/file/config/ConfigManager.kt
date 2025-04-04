﻿package net.nonemc.leaf.file.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.client.Target
import net.nonemc.leaf.file.FileManager
import net.nonemc.leaf.utils.misc.ClassUtils
import net.nonemc.leaf.utils.misc.StringUtils
import net.nonemc.leaf.value.*
import org.lwjgl.input.Keyboard
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.concurrent.schedule

class ConfigManager {
    private val configSetFile = File(Leaf.fileManager.dir, "config-settings.json")

    private val sections = mutableListOf<ConfigSection>()

    var nowConfig = "default"
    private var nowConfigInFile = "default"
    var configFile = File(Leaf.fileManager.configsDir, "$nowConfig.json")
    var needSave = false

    init {
        ClassUtils.resolvePackage("${this.javaClass.`package`.name}.sections", ConfigSection::class.java)
            .forEach(this::registerSection)

        // add an interval timer to save the config every 30 seconds
        Timer().schedule(30000, 30000) {
            saveTicker()
        }
    }

    fun load(name: String, save: Boolean = true) {
        Leaf.isLoadingConfig = true
        if (save && nowConfig != name) {
            save(true, true) // 保存老配置
        }

        nowConfig = name
        configFile = File(Leaf.fileManager.configsDir, "$nowConfig.json")

        val json = if (configFile.exists()) {
            JsonParser().parse(configFile.reader(Charsets.UTF_8)).asJsonObject
        } else {
            JsonObject() // 这样方便一点,虽然效率会低
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
        if (Leaf.isLoadingConfig && !forceSave) {
            return
        }

        val config = JsonObject()

        for (section in sections) {
            config.add(section.sectionName, section.save())
        }

        configFile.writeText(FileManager.PRETTY_GSON.toJson(config), Charsets.UTF_8)

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

        configSetFile.writeText(FileManager.PRETTY_GSON.toJson(configSet), Charsets.UTF_8)
    }

    fun loadLegacySupport() {
        if (Leaf.fileManager.loadLegacy()) {
            if (File(Leaf.fileManager.configsDir, "$nowConfig.json").exists()) {
                nowConfig = "legacy"
                configFile = File(Leaf.fileManager.configsDir, "$nowConfig.json")
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
                        val url = StringUtils.toCompleteString(args, 1)
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

        val oldSettingDir = File(Leaf.fileManager.dir, "settings")
        if (oldSettingDir.exists()) {
            oldSettingDir.listFiles().forEach {
                if (it.isFile) {
                    val name = nowConfig
                    load("default", false)
                    nowConfig = it.name
                    configFile = File(Leaf.fileManager.configsDir, "$nowConfig.json")
                    executeScript(String(Files.readAllBytes(it.toPath())))
                    save(false, true)
                    // set data back
                    nowConfig = name
                    configFile = File(Leaf.fileManager.configsDir, "$nowConfig.json")
                    saveConfigSet()
                }
                if (!Leaf.fileManager.legacySettingsDir.exists()) {
                    Leaf.fileManager.legacySettingsDir.mkdir()
                }

                it.renameTo(File(Leaf.fileManager.legacySettingsDir, it.name))
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