package net.nonemc.leaf.features.module.modules.render

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "NoBob", category = ModuleCategory.RENDER)
class NoBob : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.distanceWalkedModified = 0f
    }
}
