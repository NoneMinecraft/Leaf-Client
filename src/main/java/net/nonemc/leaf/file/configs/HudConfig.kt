package net.nonemc.leaf.file.configs

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.file.FileManager
import net.nonemc.leaf.ui.hud.Config
import java.io.File

class HudConfig(file: File) : FileManager(file) {
    override fun loadConfig(config: String) {
        Leaf.hud.clearElements()
        Leaf.hud = Config(config).toHUD()
    }

    override fun saveConfig(): String {
        return Config(Leaf.hud).toJson()
    }
}