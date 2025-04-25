package net.nonemc.leaf.features.module.modules.misc

import net.minecraft.client.gui.GuiChat
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.libs.timer.TimeUtils
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.TextValue

@ModuleInfo(name = "AutoChat", category = ModuleCategory.MISC)
class AutoChat : Module() {
    private val maxDelayValue: IntegerValue = IntegerValue("MaxDelay", 1000, 0, 5000)
    private val minDelayValue: IntegerValue = IntegerValue("MinDelay", 500, 0, 5000)
    private val messageValue = TextValue("Message", "Message")

    private val delayTimer = MSTimer()
    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.currentScreen != null && mc.currentScreen is GuiChat) return
        if (delayTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(messageValue.get())
            delayTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        }
    }
}
