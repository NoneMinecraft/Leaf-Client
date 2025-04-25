package net.nonemc.leaf

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
import net.nonemc.leaf.file.*
import net.nonemc.leaf.file.config.ConfigManager
import net.nonemc.leaf.libs.ai.json.loadAIImageJson
import net.nonemc.leaf.libs.neuralnetwork.learnNetwork
import net.nonemc.leaf.script.ScriptManager
import net.nonemc.leaf.libs.system.showDialog
import net.nonemc.leaf.ui.cape.GuiCapeManager
import net.nonemc.leaf.ui.hud.HUD
import net.nonemc.leaf.ui.keybind.KeyBindManager
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.font.FontsGC
import net.nonemc.leaf.ui.sound.TipSoundManager
import net.nonemc.leaf.libs.file.Unpack
import net.nonemc.leaf.libs.item.InventoryItem
import net.nonemc.leaf.libs.session.SessionLib
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.libs.neuralnetwork.learnReverseNetwork
import net.nonemc.leaf.ui.mainmenu.GuiMainMenu
import net.nonemc.leaf.ui.mainmenu.getAIBackGround
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display
import java.io.*

object Leaf {
    const val CLIENT_NAME = "Leaf Client"
    const val COLORED_NAME = "§bLeaf §c» "
    const val CLIENT_CREATOR = "NoneMinecraft"
    const val CLIENT_WEBSITE = "github.com/NoneMinecraft/Leaf-Client"

    private const val latestLogTitle = "Leaf-Client is testing a neural network"
    private const val latestLogMessage = "Leaf-Client 正在测试一个神经网络"

    @JvmStatic
    val logger = LogManager.getLogger("LeafClient")
    @JvmField
    val CLIENT_VERSION = ""
    var isStarting = true
    var isLoadingConfig = true
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileConfigManager
    lateinit var scriptManager: ScriptManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager
    lateinit var macroManager: MacroManager
    lateinit var configManager: ConfigManager
    lateinit var hud: HUD
    lateinit var mainMenu: GuiScreen
    lateinit var keyBindManager: KeyBindManager
    var background: ResourceLocation? = ResourceLocation("leaf/background.png")
    fun initClient() {
        if (!dir.exists()) {
            dir.mkdir()
        }
        createConfig()
        createData()
        Thread{
            learnNetwork()
            learnReverseNetwork()
        }.start()
        fileManager = FileConfigManager()
        configManager = ConfigManager()
        eventManager = EventManager()
        eventManager.registerListener(RotationBaseLib())
        eventManager.registerListener(AntiForge)
        eventManager.registerListener(InventoryItem)
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(SessionLib())
        commandManager = CommandManager()
        loadConfigs(
            accountsConfig,
            friendsConfig,
            specialConfig,
            subscriptsConfig
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
        if (!backgroundFile.exists()) {
            Unpack.unpackFile(backgroundFile, "assets/minecraft/leaf/background.png")
        }
       if (loadAIImageJson(File(backgroundDir,"AIBackGround.json")) != null) {
           getAIBackGround(1920,1080)
       }
        GuiCapeManager.load()
        mainMenu = GuiMainMenu()
        hud = HUD.createDefault()
        loadConfigs(hudConfig, xrayConfig)
        loadBackground()
        setTitle()
        showMessage()
        isStarting = false
        configManager.loadLegacySupport()
        configManager.loadConfigSet()
    }
    private fun showMessage(){
        Thread{showDialog(latestLogTitle, latestLogMessage)}.start()
    }
    fun displayAlert(message: String) {
        displayChatMessage(COLORED_NAME + message)
    }
    fun setTitle() {
        Display.setTitle("Leaf Client")
    }
    fun displayChatMessage(message: String) {
        if (net.nonemc.leaf.libs.base.mc.thePlayer == null) {
            logger.info("(MCChat) $message")
            return
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty("text", message)
        net.nonemc.leaf.libs.base.mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()))
    }
}
