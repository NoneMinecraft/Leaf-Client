package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "HudShadows", category = ModuleCategory.CLIENT, canEnable = false)
object HudShadows : Module() {
    val buttonShadowValue = BoolValue("ButtonShadow", false)
    val UiShadowValue = ListValue("UiShadowMode", arrayOf("None"), "None")
}
