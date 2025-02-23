/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.nonemc.leaf.features.command.commands

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.command.Command
import net.nonemc.leaf.features.command.CommandManager
import net.nonemc.leaf.ui.cape.GuiCapeManager
import net.nonemc.leaf.ui.font.Fonts

class ReloadCommand : Command("reload", emptyArray()) {
    override fun execute(args: Array<String>) {
        alert("Reloading...")
        alert("§c§lReloading commands...")
        Leaf.commandManager = CommandManager()
        Leaf.commandManager.registerCommands()
        Leaf.isStarting = true
        Leaf.isLoadingConfig = true
        Leaf.scriptManager.disableScripts()
        Leaf.scriptManager.unloadScripts()
        for (module in Leaf.moduleManager.modules)
            Leaf.moduleManager.generateCommand(module)
        alert("§c§lReloading scripts...")
        Leaf.scriptManager.loadScripts()
        Leaf.scriptManager.enableScripts()
        alert("§c§lReloading fonts...")
        Fonts.loadFonts()
        alert("§c§lReloading modules...")
        Leaf.configManager.load(Leaf.configManager.nowConfig, false)
        GuiCapeManager.load()
        alert("§c§lReloading accounts...")
        Leaf.fileManager.loadConfig(Leaf.fileManager.accountsConfig)
        alert("§c§lReloading friends...")
        Leaf.fileManager.loadConfig(Leaf.fileManager.friendsConfig)
        alert("§c§lReloading xray...")
        Leaf.fileManager.loadConfig(Leaf.fileManager.xrayConfig)
        alert("§c§lReloading HUD...")
        Leaf.fileManager.loadConfig(Leaf.fileManager.hudConfig)
        alert("Reloaded.")
        Leaf.isStarting = false
        Leaf.isLoadingConfig = false
        System.gc()
    }
}
