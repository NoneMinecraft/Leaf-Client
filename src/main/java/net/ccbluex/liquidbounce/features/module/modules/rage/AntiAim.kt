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
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.control.idle
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "AntiAim", category = ModuleCategory.Rage)
class AntiAim : Module() {
    private val yaw = FloatValue("Yaw",45F,-180F,180F)
    private val pitch = FloatValue("Pitch",45F,-0F,90F)
    private val auto = BoolValue("Auto",true)
    private val yawOffset = FloatValue("AutoYawOffset",45F,-180F,180F)
    private val rotateValue = BoolValue("SilentRotate", true)
    private val dynamic = BoolValue("Dynamic", true)
    private val dynamicAmplitude = FloatValue("DynamicAmplitude",15F,0F,180F)
    private val sneakMode = ListValue("SneakMode", arrayOf("Packet","Legit","Off"),"Off")
    private val whenRageBotIsIdle = BoolValue("WhenRageBotIsIdle", true)
    private var yawValue = 0F
    private var pitchValue = 0F
    private var rotationTick = 0
    override fun onDisable() {
        yawValue = 0F
        pitchValue = 0F
        rotationTick = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!whenRageBotIsIdle.get() || idle()) {
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
            if (dynamic.get()) if (rotationTick < 10) rotationTick++ else {
                yawValue += dynamicAmplitude.get()
                rotationTick = 0
            }
        }
    }
}