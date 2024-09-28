/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
 package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "AntiAim", category = ModuleCategory.COMBAT)
class AntiAim : Module() {
    private val C04 = BoolValue("C04Spoof",false)
    private val Yaw = FloatValue("Yaw",45F,-180F,180F)
    private val Pitch = FloatValue("Pitch",45F,-0F,90F)
    private val SneakOnAir = BoolValue("SneakOnAir",false)
    private val SneakOnAirTick = IntegerValue("SneakOnAirTick",5,0,20)
    private val OnlySneak = BoolValue("OnlySneak",false)
    var sneakTick = 0
    var tick = 0
    var tick2 = 2
    override fun onDisable() {
        sneakTick = 0
        tick = 0
        tick2 = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (OnlySneak.get()){
            if (mc.thePlayer.isSneaking){
                if (tick < 15) {
                    tick++
                    RotationUtils.setTargetRotation(Rotation(Yaw.get() + 45, Pitch.get()))
                } else if (tick2 < 15) {
                    tick2++
                    RotationUtils.setTargetRotation(Rotation(Yaw.get() - 45, Pitch.get()))
                } else {
                    tick2 = 0
                    tick = 0
                }
            }
        } else{
            RotationUtils.setTargetRotation(Rotation(Yaw.get(), Pitch.get()))
        }
        if (SneakOnAir.get()){
            if (mc.thePlayer.onGround){
                mc.gameSettings.keyBindSneak.pressed = false
                sneakTick = 0
            }else if (sneakTick < SneakOnAirTick.get()){
                sneakTick ++
                mc.gameSettings.keyBindSneak.pressed = false
            }else{
                mc.gameSettings.keyBindSneak.pressed = true
            }
        }
        if (C04.get()){
            mc.thePlayer.sendQueue.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.001,
                    mc.thePlayer.posZ,
                    true
                )
            )

            mc.thePlayer.sendQueue.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 0.001,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
    }
}