/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
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
        if (modeValue.get()=="Intave13"){
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
        if (modeValue.get()=="Motion"){
            mc.thePlayer.motionY = -1.0
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
