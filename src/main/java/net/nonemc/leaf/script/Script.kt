package net.nonemc.leaf.script

import jdk.internal.dynalink.beans.StaticClass
import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptUtils
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.script.api.*
import net.nonemc.leaf.script.api.global.Chat
import net.nonemc.leaf.script.api.global.Notifications
import net.nonemc.leaf.script.api.global.Setting
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.item.InventoryItem
import net.nonemc.leaf.libs.packet.PacketLib
import java.io.File
import java.util.function.Function

class Script(private val scriptFile: File) : MinecraftInstance() {
    private val scriptEngine = NashornScriptEngineFactory().getScriptEngine(
        emptyArray(),
        this.javaClass.classLoader,
        ScriptSafetyManager.classFilter
    )
    var scriptText: String =
        if (!scriptFile.path.contains("CloudLoad")) scriptFile.readText(Charsets.UTF_8) else "//api_version=2"
    var isOnline = false
    lateinit var scriptName: String
    lateinit var scriptVersion: String
    lateinit var scriptAuthors: Array<String>

    private var state = false
    private var isEnable = false
    private val events = HashMap<String, JSObject>()
    private val registeredModules = mutableListOf<Module>()
    private val registeredCommands = mutableListOf<Command>()
    fun getState(): Boolean {
        return isEnable
    }

    init {
        //Main
        scriptEngine.put("Chat", StaticClass.forClass(Chat::class.java))
        scriptEngine.put("Setting", StaticClass.forClass(Setting::class.java))
        scriptEngine.put("Notifications", StaticClass.forClass(Notifications::class.java))

        // Global instances
        scriptEngine.put("mc", mc)
        scriptEngine.put("moduleManager", Leaf.moduleManager)
        scriptEngine.put("commandManager", Leaf.commandManager)
        scriptEngine.put("scriptManager", Leaf.scriptManager)

        // Utils
        scriptEngine.put("EntityMoveLib", EntityMoveLib)
        scriptEngine.put("PacketLib", PacketLib)
        scriptEngine.put("InventoryItem", InventoryItem)

        // Global functions
        scriptEngine.put("registerScript", RegisterScript())
        supportLegacyScripts()

        scriptEngine.eval(scriptText)
        callEvent("load")
    }

    @Suppress("UNCHECKED_CAST")
    inner class RegisterScript : Function<JSObject, Script> {
        override fun apply(scriptObject: JSObject): Script {
            scriptName = scriptObject.getMember("name") as String
            scriptVersion = scriptObject.getMember("version") as String
            scriptAuthors =
                ScriptUtils.convert(scriptObject.getMember("authors"), Array<String>::class.java) as Array<String>

            return this@Script
        }
    }

    @Suppress("unused")
    fun registerModule(moduleObject: JSObject, callback: JSObject) {
        val module = ScriptModule(moduleObject)
        Leaf.moduleManager.registerModule(module)
        registeredModules += module
        callback.call(moduleObject, module)
    }
    @Suppress("unused")
    fun registerCommand(commandObject: JSObject, callback: JSObject) {
        val command = ScriptCommand(commandObject)
        Leaf.commandManager.registerCommand(command)
        registeredCommands += command
        callback.call(commandObject, command)
    }

    private fun supportLegacyScripts() {
        if (!scriptText.lines().first().contains("api_version=2")) {
            val legacyScript = Leaf::class.java.getResource("/assets/minecraft/leaf/scriptapi/legacy.js").readText()
            scriptEngine.eval(legacyScript)
        }
    }
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }
    fun onEnable() {
        if (state) return

        callEvent("enable")
        state = true
    }
    fun onDisable() {
        if (!state) return

        registeredModules.forEach { Leaf.moduleManager.unregisterModule(it) }
        registeredCommands.forEach { Leaf.commandManager.unregisterCommand(it) }

        callEvent("disable")
        state = false
    }
    fun import(scriptFile: String) {
        scriptEngine.eval(File(Leaf.scriptManager.scriptsFolder, scriptFile).readText())
    }
    fun callEvent(eventName: String) {
        when (eventName) {
            "enable" -> isEnable = true
            "disable" -> isEnable = false
        }
        try {
            events[eventName]?.call(null)
        } catch (throwable: Throwable) {
            println("[ScriptAPI] Exception in script '$scriptName'!")
        }
    }
}