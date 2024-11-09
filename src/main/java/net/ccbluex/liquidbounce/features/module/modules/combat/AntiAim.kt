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
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "AntiAim", category = ModuleCategory.COMBAT)
class AntiAim : Module() {
    private val mode = ListValue("Mode", arrayOf("Custom","LegitRotate"),"Custom")
    private val yaw = FloatValue("Yaw",45F,-180F,180F)
    private val pitch = FloatValue("Pitch",45F,-0F,90F)
    private val auto = BoolValue("Auto",true)
    private val yawOffset = FloatValue("AutoYawOffset",45F,-180F,180F)
    private val rotateValue = BoolValue("SilentRotate", true)
    private val dynamic = BoolValue("Dynamic", true)
    private val dynamicAmplitude = FloatValue("DynamicAmplitude",15F,0F,180F)
    private val sneakMode = ListValue("SneakMode", arrayOf("Packet","Legit","Off"),"Off")

    private val maxHurtTimeA = IntegerValue("MaxHurtTimeA",2,1,10)
    private val minHurtTimeA = IntegerValue("MinHurtTimeA",1,1,10)
    private val maxHurtTimeB = IntegerValue("MaxHurtTimeB",4,1,10)
    private val minHurtTimeB = IntegerValue("MinHurtTimeB",3,1,10)
    private val velocity = FloatValue("Velocity",0.2F,0F,2F)
    private var yawValue = 0F
    private var pitchValue = 0F
    private var rotationTick = 0
    private var tmpYaw = 0F
    private var tmpPitch = 0F

    override fun onDisable() {
        yawValue = 0F
        pitchValue = 0F
        rotationTick = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode.get() == "Custom") {
            if (rotateValue.get()) RotationUtils.setTargetRotation(Rotation(yawValue, pitchValue)) else {
                mc.thePlayer.rotationYaw = yawValue
                mc.thePlayer.rotationPitch = pitchValue
            }
            if (auto.get()) {
                yawValue = mc.thePlayer.rotationYaw + yawOffset.get()
                pitchValue = pitch.get()
            } else {
                yawValue = yaw.get()
                pitchValue = pitch.get()
            }
            if (sneakMode.get() == "Packet") mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.START_SNEAKING
                )
            )
            else if (sneakMode.get() == "Legit") mc.thePlayer.isSneaking = true
            else mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )

            if (dynamic.get()) if (rotationTick < 10) {
                rotationTick++
            } else {
                yawValue += dynamicAmplitude.get()
                rotationTick = 0
            }
        } else if (mode.get() == "LegitRotate") {
            if (mc.thePlayer.hurtTime != 0){
                if (mc.thePlayer.hurtTime in minHurtTimeA.get()..maxHurtTimeA.get() &&(mc.thePlayer.motionZ > velocity.get() || mc.thePlayer.motionX > velocity.get())){
                    tmpYaw = mc.thePlayer.rotationYaw
                    tmpPitch = mc.thePlayer.rotationPitch
                    mc.thePlayer.rotationPitch = pitchValue
                    mc.thePlayer.rotationYaw -= 180
                }
                if (mc.thePlayer.hurtTime in minHurtTimeB.get()..maxHurtTimeB.get()){
                    mc.thePlayer.rotationPitch = tmpPitch
                    mc.thePlayer.rotationYaw = tmpYaw
                }
            }
        }
    }
}