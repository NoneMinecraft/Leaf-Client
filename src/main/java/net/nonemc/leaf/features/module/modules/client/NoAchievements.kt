package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.TickEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "NoAchievements", category = ModuleCategory.CLIENT, array = false)
class NoAchievements : Module() {
    @EventTarget
    fun onTick(event: TickEvent) {
        mc.guiAchievement.clearAchievements()
    }
}
