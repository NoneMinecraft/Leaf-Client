package net.nonemc.leaf.launch.data.modernui.mainmenu.config

import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu.buttonConfigs
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenuButton
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.ButtonConfig
import net.nonemc.leaf.launch.data.modernui.mainmenu.dir
import java.awt.Color
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


fun saveButtonConfig() {
    try {
        if (!dir.exists()) dir.mkdirs()

        val configs: MutableMap<String, ButtonConfig> = HashMap()
        for (button in MainMenu.buttons) {
            configs[button.text] = button.config
        }

        OutputStreamWriter(FileOutputStream(BUTTON_CONFIG_FILE), StandardCharsets.UTF_8).use { writer ->
            MainMenu.GSON.toJson(configs, writer)
        }
    } catch (e: Exception) {
        println(e)
    }
}
fun createButton(
    text: String,
    action: Runnable,
    width: Float,
    height: Float,
    defaultX: Float,
    defaultY: Float,
): MainMenuButton {
    val config = buttonConfigs.getOrDefault(
        text,
        ButtonConfig(defaultX, defaultY, 1.0f, 1.0f, 1.0f, Color(100, 100, 255, 200).getRGB(), 5)
    )
    return MainMenuButton(
        text,
        action,
        width,
        height,
        config.x,
        config.y,
        config.xScale,
        config.yScale,
        config.textScale,
        config.color,
        config.cornerRadius
    )
}

fun updateButtonConfig(button: MainMenuButton, newConfig: ButtonConfig?) {
    button.applyConfig(newConfig)
}