/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.rage

import net.minecraft.util.MathHelper
import net.nonemc.leaf.data.Rotation
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.StrafeEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.rage.rage.control.idle
import net.nonemc.leaf.utils.rotation.RotationUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue

@ModuleInfo(name = "AntiAim", category = ModuleCategory.Rage)
class AntiAim : Module() {
    private val yaw = FloatValue("Yaw", 45F, -180F, 180F)
    private val pitch = FloatValue("Pitch", 45F, -90F, 90F)
    private val auto = BoolValue("Auto", true)
    private val yawOffset = FloatValue("AutoYawOffset", 45F, -180F, 180F)
    private val rotateValue = BoolValue("SilentRotate", true)
    private val dynamic = BoolValue("Dynamic", true)
    private val dynamicAmplitude = FloatValue("DynamicAmplitude", 15F, 0F, 180F)
    private val dynamicTick = IntegerValue("DynamicTick", 10, 1, 20)
    private val whenRageBotIsIdle = BoolValue("WhenRageBotIsIdle", true)
    private val strictStrafe = BoolValue("StrictStrafe", true)
    private val onSneak = BoolValue("OnSneak", true)
    private var yawValue = 0F
    private var pitchValue = 0F
    private var rotationTick = 0
    override fun onDisable() {
        yawValue = 0F
        pitchValue = 0F
        rotationTick = 0
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (strictStrafe.get()) {
            val (yaw) = RotationUtils.targetRotation ?: return
            var strafe = event.strafe
            var forward = event.forward
            val friction = event.friction

            var f = strafe * strafe + forward * forward

            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt_float(f)

                if (f < 1.0F) {
                    f = 1.0F
                }

                f = friction / f
                strafe *= f
                forward *= f

                val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
            }
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((!whenRageBotIsIdle.get() || idle()) && (!onSneak.get() || mc.thePlayer.isSneaking)) {
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
            if (dynamic.get()) if (rotationTick < dynamicTick.get()) rotationTick++ else {
                yawValue += dynamicAmplitude.get()
                rotationTick = 0
            }

        }
    }
}