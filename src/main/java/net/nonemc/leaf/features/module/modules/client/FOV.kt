package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue

@ModuleInfo(name = "FOV", category = ModuleCategory.CLIENT)
class FOV : Module() {
    val fovValue = FloatValue("FOV", 1f, 0f, 1.5f)
}
