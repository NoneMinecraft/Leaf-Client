package net.nonemc.leaf.script

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.script.remapper.Remapper
import java.io.File

class ScriptManager {

    val scripts = mutableListOf<Script>()
    val scriptsFolder = File(Leaf.fileManager.dir, "scripts")

    /**
     * Loads all scripts inside the scripts folder.
     */
    fun loadScripts() {
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdir()
        }

        scriptsFolder.listFiles().forEach {
            if (it.name.endsWith(".js", true)) {
                Remapper.loadSrg() // load SRG if needed, this will optimize the performance
                loadJsScript(it)
            }
        }
    }

    /**
     * Unloads all scripts.
     */
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

    /**
     * Enables all scripts.
     */
    fun enableScripts() {
        scripts.forEach { it.onEnable() }
    }

    /**
     * Disables all scripts.
     */
    fun disableScripts() {
        scripts.forEach { it.onDisable() }
    }
}