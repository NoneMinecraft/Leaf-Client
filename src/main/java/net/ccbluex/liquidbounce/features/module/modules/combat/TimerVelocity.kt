/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.status.client.C01PacketPing

@ModuleInfo(name = "TimerVelocity", category = ModuleCategory.COMBAT)
class TimerVelocity : Module() {
    var jump = false
    var lowticks = 0
    var maxticks = 0
    var cancel = false
    private val Low = FloatValue("LowTimer", 0.1F, 0.05F, 1F)
    private val LowTimerTicks = IntegerValue("LowTimerTicks", 1, 1, 20)
    private val Max = FloatValue("MaxTimer", 2F, 1F, 5F)
    private val MaxTimerTicks = IntegerValue("MaxTimerTicks", 1, 1, 20)
    private val C03 = BoolValue("C03", true)
    private val OnAir = BoolValue("OnAir", true)
    private val JumpReset = BoolValue("JumpReset", false)
    override fun onDisable() {
        cancel = false
        maxticks= 0
        lowticks=0
        mc.timer.timerSpeed = 1F
        jump = false
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (cancel && C03.get()&&packet is C03PacketPlayer && !(packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C05PacketPlayerLook || packet is C03PacketPlayer.C06PacketPlayerPosLook)) {
            event.cancelEvent()
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (JumpReset.get()) {
            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                mc.gameSettings.keyBindJump.pressed = true
                jump = true
            } else if (jump) {
                mc.gameSettings.keyBindJump.pressed = false
                jump = false
            }
        }
        if (mc.thePlayer.hurtTime > 0) {
            cancel = true
            when {
                lowticks <= LowTimerTicks.get() -> {
                    lowticks++
                    mc.timer.timerSpeed = Low.get()
                }
                maxticks <= MaxTimerTicks.get() -> {
                    maxticks++
                    if (OnAir.get() && mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = Max.get()
                    } else if (!OnAir.get()) {
                        mc.timer.timerSpeed = Max.get()
                    }
                }
                else -> {
                    lowticks = 0
                    maxticks = 0
                }
            }
        } else {
            cancel = false
            mc.timer.timerSpeed = 1F
        }
    }
}