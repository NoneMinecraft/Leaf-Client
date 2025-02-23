
package net.nonemc.leaf.features.module.modules.render

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.MotionEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.RotationUtils.serverRotation
import net.nonemc.leaf.value.BoolValue

@ModuleInfo(name = "Rotations", category = ModuleCategory.RENDER)
object Rotations : Module() {

    private val realistic = BoolValue("Realistic", true)
    private val body = BoolValue("Body", true)
    
    val experimentalCurve = BoolValue("ExperimentalLinearCurveRotation", false)
    
    var prevHeadPitch = 0f
    var headPitch = 0f

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        prevHeadPitch = headPitch
        headPitch = serverRotation.pitch



        thePlayer.rotationYawHead = serverRotation.yaw


    }

    fun lerp(tickDelta: Float, old: Float, new: Float): Float {
        return old + (new - old) * tickDelta
    }

    /**
     * Rotate when current rotation is not null or special modules which do not make use of RotationUtils like Derp are enabled.
     */

}
