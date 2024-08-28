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
    private val Yaw = FloatValue("Yaw",45F,-90F,90F)
    private val Pitch = FloatValue("Pitch",45F,-360F,360F)
    private val SneakOnAir = BoolValue("SneakOnAir",false)
    private val SneakOnAirTick = IntegerValue("SneakOnAirTick",5,0,20)
    var sneakTick = 0
    override fun onDisable() {
        sneakTick = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        RotationUtils.setTargetRotation(Rotation(Yaw.get(),Pitch.get()))
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
                    mc.thePlayer.posY + 0.0001,
                    mc.thePlayer.posZ,
                    true
                )
            )

            mc.thePlayer.sendQueue.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 0.0001,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
    }
}