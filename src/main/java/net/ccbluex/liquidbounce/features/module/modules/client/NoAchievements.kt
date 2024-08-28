
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "NoAchievements", category = ModuleCategory.CLIENT, array = false)
class NoAchievements : Module() {
    @EventTarget
    fun onTick(event: TickEvent) {
        mc.guiAchievement.clearAchievements()
    }
}
