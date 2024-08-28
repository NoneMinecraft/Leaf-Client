/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "IntaveFastUse", category = ModuleCategory.MOVEMENT)
class IntaveFastUse : Module() {
    private val LowTimer = FloatValue("LowTimer", 0.3F, 0.01F, 10F)
    private val MaxTimer = FloatValue("MaxTimer", 0.3F, 0.01F, 10F)
    private val Ticks = FloatValue("Ticks", 1F, 1F, 20F)
    var iseating = 10
    override fun onDisable() {
        iseating = Ticks.get().toInt()
        mc.timer.timerSpeed = 1F
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {

      if(mc.thePlayer.isEating){
          if (iseating>=1){
              iseating--
              mc.timer.timerSpeed = LowTimer.get()

          }else{
              iseating=10
              mc.timer.timerSpeed = MaxTimer.get()
          }
      }else{
          iseating = Ticks.get().toInt()
          mc.timer.timerSpeed = 1F
      }
}
}
