﻿package net.nonemc.leaf

import com.google.gson.JsonObject
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.IChatComponent
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.event.EventManager
import net.nonemc.leaf.features.command.CommandManager
import net.nonemc.leaf.features.macro.MacroManager
import net.nonemc.leaf.features.module.ModuleManager
import net.nonemc.leaf.features.special.AntiForge
import net.nonemc.leaf.features.special.BungeeCordSpoof
import net.nonemc.leaf.features.special.CombatManager
import net.nonemc.leaf.features.special.ServerSpoof
import net.nonemc.leaf.file.FileManager
import net.nonemc.leaf.file.config.ConfigManager
import net.nonemc.leaf.launch.EnumLaunchFilter
import net.nonemc.leaf.launch.LaunchFilterInfo
import net.nonemc.leaf.launch.LaunchOption
import net.nonemc.leaf.launch.data.GuiLaunchOptionSelectMenu
import net.nonemc.leaf.launch.data.modernui.scriptOnline.ScriptSubscribe
import net.nonemc.leaf.launch.data.modernui.scriptOnline.Subscriptions
import net.nonemc.leaf.script.ScriptManager
import net.nonemc.leaf.system.isFirstLaunch
import net.nonemc.leaf.system.showDialog
import net.nonemc.leaf.ui.cape.GuiCapeManager
import net.nonemc.leaf.ui.client.hud.HUD
import net.nonemc.leaf.ui.client.keybind.KeyBindManager
import net.nonemc.leaf.ui.font.Fonts
import net.nonemc.leaf.ui.font.FontsGC
import net.nonemc.leaf.ui.sound.TipSoundManager
import net.nonemc.leaf.utils.*
import net.nonemc.leaf.utils.entity.StatisticsUtils
import net.nonemc.leaf.utils.inventory.InventoryUtils
import net.nonemc.leaf.utils.misc.ClassUtils
import net.nonemc.leaf.utils.misc.SessionUtils
import net.nonemc.leaf.utils.rotation.RotationUtils
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display

object Leaf {
    const val CLIENT_NAME = "Leaf Client"
    const val COLORED_NAME = "§bLeaf §c» "
    const val CLIENT_CREATOR = "NoneMinecraft"
    const val CLIENT_WEBSITE = "github.com/NoneMinecraft/Leaf-Client"

    @JvmField
    val CLIENT_VERSION = ""
    var isStarting = true
    var isLoadingConfig = true
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    private lateinit var subscriptions: Subscriptions
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager
    lateinit var macroManager: MacroManager
    lateinit var configManager: ConfigManager
    lateinit var hud: HUD
    lateinit var mainMenu: GuiScreen
    lateinit var keyBindManager: KeyBindManager
    var background: ResourceLocation? = ResourceLocation("leaf/background.png")
    val launchFilters = mutableListOf<EnumLaunchFilter>()
    private val dynamicLaunchOptions: Array<LaunchOption>
        get() = ClassUtils.resolvePackage(
            "${LaunchOption::class.java.`package`.name}.options",
            LaunchOption::class.java
        )
            .filter {
                val annotation = it.getDeclaredAnnotation(LaunchFilterInfo::class.java)
                if (annotation != null) {
                    return@filter annotation.filters.toMutableList() == launchFilters
                }
                false
            }
            .map {
                try {
                    it.newInstance()
                } catch (e: IllegalAccessException) {
                    ClassUtils.getObjectInstance(it) as LaunchOption
                }
            }.toTypedArray()

    fun initClient() {
        fileManager = FileManager()
        configManager = ConfigManager()
        subscriptions = Subscriptions()
        eventManager = EventManager()
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge)
        eventManager.registerListener(InventoryUtils)
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(ServerSpoof)
        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(StatisticsUtils())
        commandManager = CommandManager()
        fileManager.loadConfigs(
            fileManager.accountsConfig,
            fileManager.friendsConfig,
            fileManager.specialConfig,
            fileManager.subscriptsConfig
        )
        Fonts.loadFonts()
        eventManager.registerListener(FontsGC)
        macroManager = MacroManager()
        eventManager.registerListener(macroManager)
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        try {
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            println(throwable)
        }
        commandManager.registerCommands()
        tipSoundManager = TipSoundManager()
        keyBindManager = KeyBindManager()
        combatManager = CombatManager()
        eventManager.registerListener(combatManager)

        GuiCapeManager.load()
        mainMenu = GuiLaunchOptionSelectMenu()
        hud = HUD.createDefault()
        fileManager.loadConfigs(fileManager.hudConfig, fileManager.xrayConfig)
        for (subscript in fileManager.subscriptsConfig.subscripts) {
            Subscriptions.addSubscribes(ScriptSubscribe(subscript.url, subscript.name))
            scriptManager.disableScripts()
            scriptManager.unloadScripts()
            for (scriptSubscribe in Subscriptions.subscribes) {
                scriptSubscribe.load()
            }
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        }
        setTitle()
        showMessage()
    }

    @JvmStatic
    val logger = LogManager.getLogger("LeafClient")
    private fun showMessage(){
        if (isFirstLaunch()){
            Thread{ showDialog("Leaf-Client is utilising a custom main menu.","Leaf-Client 正在使用自定义的主菜单\n您随时可以编辑它")}.start()
        }
    }
    fun displayAlert(message: String) {
        displayChatMessage(COLORED_NAME + message)
    }
    fun setTitle() {
        Display.setTitle("Leaf Client")
    }
    fun displayChatMessage(message: String) {
        if (mc.thePlayer == null) {
            logger.info("(MCChat) $message")
            return
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("text", message)
        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()))
    }
    fun startClient() {
        dynamicLaunchOptions.forEach {
            it.start()
        }
        configManager.loadLegacySupport()
        configManager.loadConfigSet()
        isStarting = false
        isLoadingConfig = false
    }
}
