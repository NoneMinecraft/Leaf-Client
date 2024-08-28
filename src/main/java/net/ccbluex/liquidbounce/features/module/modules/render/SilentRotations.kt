package net.ccbluex.liquidbounce.features.module.modules.render


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event4.EventTarget
import net.ccbluex.liquidbounce.event4.JumpEvent
import net.ccbluex.liquidbounce.event4.Render3DEvent
import net.ccbluex.liquidbounce.event4.StrafeEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.LegitScaffold
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils4.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "SilentRotations", category = ModuleCategory.RENDER)
object SilentRotations : Module() {

    val customStrafe = BoolValue("CustomStrafing", true)

    var rotating = false

    override fun onEnable() {
        RotationUtils.enableLook()
    }

    override fun onDisable() {
        RotationUtils.disableLook()
        rotating = false
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (RotationUtils.targetRotation != null) {
            rotating = true
            if (!customStrafe.get()) {
                if (!LiquidBounce.moduleManager.getModule(LegitScaffold::class.java)?.state!! && !LiquidBounce.moduleManager.getModule(
                        Scaffold::class.java
                    )?.state!!
                )
                    event.yaw = RotationUtils.targetRotation?.yaw!!
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
            mc.thePlayer.isSprinting = false
        if (RotationUtils.targetRotation != null) {
            rotating = true
            if (!customStrafe.get()) {
                if (!LiquidBounce.moduleManager.getModule(LegitScaffold::class.java)?.state!! && !LiquidBounce.moduleManager.getModule(
                        Scaffold::class.java
                    )?.state!!
                )
                    event.yaw = RotationUtils.targetRotation?.yaw!!
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (mc.thePlayer == null || RotationUtils.targetRotation == null) {
            if (rotating)
                rotating = false

            mc.thePlayer.prevRotationYaw = RotationUtils.prevCameraYaw
            mc.thePlayer.prevRotationPitch = RotationUtils.prevCameraPitch
            mc.thePlayer.rotationYaw = RotationUtils.cameraYaw
            mc.thePlayer.rotationPitch = RotationUtils.cameraPitch
            return
        }

        if (!RotationUtils.perspectiveToggled)
            RotationUtils.enableLook()

        mc.thePlayer.rotationYaw = RotationUtils.targetRotation?.yaw!!
        mc.thePlayer.prevRenderYawOffset = mc.thePlayer.prevRotationYaw
        mc.thePlayer.prevRotationYawHead = mc.thePlayer.prevRotationYaw
        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw
        mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw
        mc.thePlayer.rotationPitch = RotationUtils.targetRotation?.pitch!!
    }
}