package net.nonemc.leaf.launch.data.modernui.mainmenu.config

import com.google.gson.reflect.TypeToken
import net.minecraft.network.status.server.S00PacketServerInfo.GSON
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu
import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu.*
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

inline fun <reified T> loadConfigFile(configFile: File, defaultConfig: () -> Unit, validateConfig: (List<T>) -> List<T>, configList: (List<T>) -> Unit) {
    try {
        if (!configFile.exists()) {
            defaultConfig()
            return
        }
        InputStreamReader(FileInputStream(configFile), StandardCharsets.UTF_8).use { reader ->
            val loaded: List<T> = GSON.fromJson(reader, object : TypeToken<List<T?>?>() {}.type)
            if (loaded.isEmpty()) {
                defaultConfig()
            } else {
                configList(validateConfig(loaded))
            }
        }
    } catch (e: Exception) {
        defaultConfig()
    }
}

fun loadImageConfig() {
    loadConfigFile(IMAGE_CONFIG_FILE, ::createDefaultImageConfig, ::validateImageConfigs) { imageConfigs = it }
}

fun loadPanelConfig() {
    loadConfigFile(PANEL_CONFIG_FILE, ::createDefaultPanelConfig, ::validatePanelConfigs) { panelConfigs = it }
}

fun loadTextConfigurations() {
    loadConfigFile(TEXT_CONFIG_FILE, ::createDefaultTextConfig, ::validateTextConfigs) { textConfigs = it }
}

fun loadTextConfig() {
    try {
        if (!BUTTON_CONFIG_FILE.exists()) return
        InputStreamReader(FileInputStream(BUTTON_CONFIG_FILE), StandardCharsets.UTF_8).use { reader ->
            buttonConfigs = MainMenu.GSON.fromJson(reader, object : TypeToken<Map<String?, ButtonConfig?>?>() {}.type)
        }
    } catch (e: Exception) {
        buttonConfigs = HashMap()
    }
}
