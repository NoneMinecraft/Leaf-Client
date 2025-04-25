package net.nonemc.leaf.ui

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.client.ClickGUIModule
import net.nonemc.leaf.file.dir
import net.nonemc.leaf.file.loadConfig
import net.nonemc.leaf.file.saveConfig
import net.nonemc.leaf.launch.EnumLaunchFilter
import net.nonemc.leaf.launch.LaunchFilterInfo
import net.nonemc.leaf.launch.LaunchOption
import net.nonemc.leaf.ui.clickgui.ClickGui
import net.nonemc.leaf.ui.clickgui.ClickGuiConfig
import java.io.File

@LaunchFilterInfo([EnumLaunchFilter.MODERN_UI])
object UILaunchOption : LaunchOption() {

    @JvmStatic
    lateinit var clickGui: ClickGui

    @JvmStatic
    lateinit var clickGuiConfig: ClickGuiConfig

    override fun start() {
        Leaf.moduleManager.registerModule(ClickGUIModule())

        clickGui = ClickGui()
        clickGuiConfig = ClickGuiConfig(File(dir, "clickgui.json"))
        loadConfig(clickGuiConfig)
    }

    override fun stop() {
        saveConfig(clickGuiConfig)
    }
}