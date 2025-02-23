/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue
import net.minecraft.client.Minecraft

@ModuleInfo(name = "FastFall", category = ModuleCategory.MOVEMENT)
class FastFall : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Intave13",",Motion"), "Intave13")
    private val MaxFallDistance = FloatValue("MaxFallDistance", 5F, 1F, 100F)
    private val FallSpeed = FloatValue("FallSpeed", 5F, 1F, 20F)
    private val NoXZ = BoolValue("NoXZ", true)
    private val mc = Minecraft.getMinecraft()
    private var lastY = 0.0
    private var fallDistance = 0.0
    private var falling = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer
        if (player != null && !player.onGround) {
            if (!falling) {
                falling = true
                lastY = player.posY
                fallDistance = 0.0
            } else {
                val deltaY = lastY - player.posY
                if (deltaY > 0) {
                    fallDistance += deltaY
                    lastY = player.posY

                    if (fallDistance >= MaxFallDistance.get()) {
                        Timer()
                        fallDistance = 0.0
                    }
                }
            }
        }   else {
            if (falling) {
                onNotFalling()
                falling = false
                fallDistance = 0.0
            }
        }
    }

    private fun Timer() {
        if(NoXZ.get()){
            mc.thePlayer.motionX= 0.0
            mc.thePlayer.motionZ= 0.0
        }
    mc.timer.timerSpeed = FallSpeed.get()
    }
    private fun onNotFalling() {
        mc.timer.timerSpeed = 1F
    }
    override val tag: String
        get() = modeValue.get()
}
