package net.nonemc.leaf.script

import net.nonemc.leaf.file.configDir
import net.nonemc.leaf.script.remapper.Remapper
import java.io.File

class ScriptManager {
    val scripts = mutableListOf<Script>()
    val scriptsFolder = File(configDir, "scripts")
    fun loadScripts() {
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdir()
        }
        scriptsFolder.listFiles().forEach {
            if (it.name.endsWith(".js", true)) {
                Remapper.loadSrg()
                loadJsScript(it)
            }
        }
    }
    fun unloadScripts() {
        scripts.clear()
    }
    private fun loadJsScript(scriptFile: File) {
        try {
            scripts.add(Script(scriptFile))
        } catch (t: Throwable) {
            println("[ScriptAPI] Failed to load script '${scriptFile.name}'")
        }
    }
    fun enableScripts() {
        scripts.forEach { it.onEnable() }
    }
    fun disableScripts() {
        scripts.forEach { it.onDisable() }
    }
}