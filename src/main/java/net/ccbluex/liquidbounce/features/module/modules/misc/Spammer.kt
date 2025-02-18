
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.GuiChat

@ModuleInfo(name = "Spammer", category = ModuleCategory.MISC)
class Spammer : Module() {
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
