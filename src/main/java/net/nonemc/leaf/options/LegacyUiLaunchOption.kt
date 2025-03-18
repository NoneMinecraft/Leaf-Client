package net.nonemc.leaf.launch.options

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.launch.EnumLaunchFilter
import net.nonemc.leaf.launch.LaunchFilterInfo
import net.nonemc.leaf.launch.LaunchOption
import net.nonemc.leaf.launch.data.modernui.ClickGUIModule
import net.nonemc.leaf.launch.data.modernui.ClickGuiConfig
import net.nonemc.leaf.launch.data.modernui.GuiMainMenu
import net.nonemc.leaf.launch.data.modernui.clickgui.ClickGui
import java.io.File

@LaunchFilterInfo([EnumLaunchFilter.MODERN_UI])
object modernuiLaunchOption : LaunchOption() {

    @JvmStatic
    lateinit var clickGui: ClickGui

    @JvmStatic
    lateinit var clickGuiConfig: ClickGuiConfig

    override fun start() {
        Leaf.mainMenu = GuiMainMenu()
        Leaf.moduleManager.registerModule(ClickGUIModule())

        clickGui = ClickGui()
        clickGuiConfig = ClickGuiConfig(File(Leaf.fileManager.dir, "clickgui.json"))
        Leaf.fileManager.loadConfig(clickGuiConfig)
    }

    override fun stop() {
        Leaf.fileManager.saveConfig(clickGuiConfig)
    }
}