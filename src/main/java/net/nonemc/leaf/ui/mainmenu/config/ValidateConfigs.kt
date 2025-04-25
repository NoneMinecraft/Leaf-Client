package net.nonemc.leaf.ui.mainmenu.config

import net.nonemc.leaf.ui.mainmenu.config.configs.ImageConfig
import net.nonemc.leaf.ui.mainmenu.config.configs.PanelConfig
import net.nonemc.leaf.ui.mainmenu.config.configs.TextConfig

inline fun <reified T> validateConfigs(configs: List<T>, validateCondition: (T) -> Boolean): List<T> {
    return configs.filter { validateCondition(it) }
}

fun validateTextConfigs(configs: List<TextConfig>): List<TextConfig> {
    return validateConfigs(configs) { it.text != null }
}

fun validatePanelConfigs(configs: List<PanelConfig>): List<PanelConfig> {
    return validateConfigs(configs) { true }
}

fun validateImageConfigs(configs: List<ImageConfig>): List<ImageConfig> {
    return validateConfigs(configs) { it.path != null }
}
