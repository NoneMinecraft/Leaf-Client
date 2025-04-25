package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue

@ModuleInfo(name = "Bob", category = ModuleCategory.CLIENT)
class Bob : Module() {
    private val bobValue = FloatValue("Bob",0f,0f,1f)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.distanceWalkedModified = bobValue.get()
    }
}
