/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils.TickUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "FakeLag", category = ModuleCategory.Rage)
class FakeLag : Module() {
    private val time = TickUtils()
    private val timeValue = IntegerValue("Time",3,1,20)
    private val lowSpeed = FloatValue("LowSpeed",0.2F,0.03F,1F)
    private val changeSpeed = FloatValue("ChangeSpeed",2F,1F,10F)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        time.update()
        if (time.ticks(timeValue.get())) mc.timer.timerSpeed = lowSpeed.get() else{
            mc.timer.timerSpeed = changeSpeed.get()
            time.reset()
        }
    }
}