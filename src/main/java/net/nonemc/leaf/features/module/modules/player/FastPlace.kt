package net.nonemc.leaf.features.module.modules.player

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.IntegerValue

@ModuleInfo(name = "FastPlace", category = ModuleCategory.PLAYER)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}
