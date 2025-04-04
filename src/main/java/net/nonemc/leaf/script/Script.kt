﻿package net.nonemc.leaf.script

import jdk.internal.dynalink.beans.StaticClass
import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptUtils
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.launch.data.modernui.scriptOnline.Subscriptions
import net.nonemc.leaf.script.api.*
import net.nonemc.leaf.script.api.global.Chat
import net.nonemc.leaf.script.api.global.Notifications
import net.nonemc.leaf.script.api.global.Setting
import net.nonemc.leaf.utils.*
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.utils.inventory.InventoryUtils
import net.nonemc.leaf.utils.packet.PacketUtils
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

    // Script information
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

    fun getRegisteredModules(): MutableList<Module> {
        return registeredModules
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
        scriptEngine.put("MovementUtils", MovementUtils)
        scriptEngine.put("PacketUtils", PacketUtils)
        scriptEngine.put("InventoryUtils", InventoryUtils)

        // Global functions
        scriptEngine.put("registerScript", RegisterScript())
        if (Subscriptions.loadingCloud) {
            scriptText = Subscriptions.tempJs
            isOnline = true
        }
        supportLegacyScripts()

        scriptEngine.eval(scriptText)
        callEvent("load")
    }

    @Suppress("UNCHECKED_CAST")
    inner class RegisterScript : Function<JSObject, Script> {
        /**
         * Global function 'registerScript' which is called to register a script.
         * @param scriptObject JavaScript object containing information about the script.
         * @return The instance of this script.
         */
        override fun apply(scriptObject: JSObject): Script {
            scriptName = scriptObject.getMember("name") as String
            scriptVersion = scriptObject.getMember("version") as String
            scriptAuthors =
                ScriptUtils.convert(scriptObject.getMember("authors"), Array<String>::class.java) as Array<String>

            return this@Script
        }
    }

    /**
     * Registers a new script module.
     * @param moduleObject JavaScript object containing information about the module.
     * @param callback JavaScript function to which the corresponding instance of [ScriptModule] is passed.
     * @see ScriptModule
     */
    @Suppress("unused")
    fun registerModule(moduleObject: JSObject, callback: JSObject) {
        val module = ScriptModule(moduleObject)
        Leaf.moduleManager.registerModule(module)
        registeredModules += module
        callback.call(moduleObject, module)
    }

    /**
     * Registers a new script command.
     * @param commandObject JavaScript object containing information about the command.
     * @param callback JavaScript function to which the corresponding instance of [ScriptCommand] is passed.
     * @see ScriptCommand
     */
    @Suppress("unused")
    fun registerCommand(commandObject: JSObject, callback: JSObject) {
        val command = ScriptCommand(commandObject)
        Leaf.commandManager.registerCommand(command)
        registeredCommands += command
        callback.call(commandObject, command)
    }

    fun regAnyThing() {
        registeredModules.forEach { Leaf.moduleManager.registerModule(it) }
        registeredCommands.forEach { Leaf.commandManager.registerCommand(it) }
    }

    fun supportLegacyScripts() {
        if (!scriptText.lines().first().contains("api_version=2")) {
            val legacyScript = Leaf::class.java.getResource("/assets/minecraft/leaf/scriptapi/legacy.js").readText()
            scriptEngine.eval(legacyScript)
        }
    }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    /**
     * Called when the client enables the script.
     */
    fun onEnable() {
        if (state) return

        callEvent("enable")
        state = true
    }

    /**
     * Called when the client disables the script. Handles unregistering all modules and commands
     * created with this script.
     */
    fun onDisable() {
        if (!state) return

        registeredModules.forEach { Leaf.moduleManager.unregisterModule(it) }
        registeredCommands.forEach { Leaf.commandManager.unregisterCommand(it) }

        callEvent("disable")
        state = false
    }

    /**
     * Imports another JavaScript file inro the context of this script.
     * @param scriptFile Path to the file to be imported.
     */
    fun import(scriptFile: String) {
        scriptEngine.eval(File(Leaf.scriptManager.scriptsFolder, scriptFile).readText())
    }

    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     */
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