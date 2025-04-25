package net.nonemc.leaf.features.module.modules.player

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue

@ModuleInfo(name = "NoSlowBreak", category = ModuleCategory.PLAYER)
class NoSlowBreak : Module() {
    val airValue = BoolValue("Air", true)
    val waterValue = BoolValue("Water", false)
}
