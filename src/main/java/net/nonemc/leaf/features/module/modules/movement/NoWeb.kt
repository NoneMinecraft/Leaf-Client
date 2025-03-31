/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.JumpEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "NoWeb", category = ModuleCategory.MOVEMENT)
class NoWeb : Module() {

    private val modeValue = ListValue(
        "Mode",
        arrayOf("None", "Intave", "LAAC", "Rewinside", "Horizon", "Spartan", "AAC4", "AAC5", "Matrix", "Test", "Fall"),
        "None"
    )
    private val horizonSpeed = FloatValue("HorizonSpeed", 0.1F, 0.01F, 0.8F)
    private val speed = FloatValue("Speed", 1.3F, 1F, 5F)
    private val minSpeed = FloatValue("MinSpeed", 0.9F, 0.1F, 2F)

    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer.isInWeb) {
            return
        }

        when (modeValue.get().lowercase()) {
            "none" -> mc.thePlayer.isInWeb = false
            "Intave" -> {
                if (MovementUtils.isMoving() && mc.thePlayer.moveStrafing == 0.0f) {
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                            MovementUtils.strafe(0.734f)
                        } else {
                            mc.thePlayer.jump()
                            MovementUtils.strafe(0.346f)
                        }
                    }
                }
            }

            "fall" -> {
                if (mc.thePlayer.onGround) mc.thePlayer.jump()
                if (mc.thePlayer.fallDistance > 0.01) mc.timer.timerSpeed = speed.get() else {
                    mc.timer.timerSpeed = minSpeed.get()
                }
            }

            "laac" -> {
                mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer.motionY = 0.0
                }

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                }
            }

            "aac4" -> {
                mc.timer.timerSpeed = 0.99F
                mc.thePlayer.jumpMovementFactor = 0.02958f
                mc.thePlayer.motionY -= 0.00775
                if (mc.thePlayer.onGround) {
                    // mc.thePlayer.jump()
                    mc.thePlayer.motionY = 0.4050
                    mc.timer.timerSpeed = 1.35F
                }
            }

            "horizon" -> {
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(horizonSpeed.get())
                }
            }

            "spartan" -> {
                MovementUtils.strafe(0.27F)
                mc.timer.timerSpeed = 3.7F
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer.motionY = 0.0
                }
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.timer.timerSpeed = 1.7F
                }
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3F
                }
                usedTimer = true
            }

            "matrix" -> {
                mc.thePlayer.jumpMovementFactor = 0.12425f
                mc.thePlayer.motionY = -0.0125
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.1625

                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3.0F
                    usedTimer = true
                }
            }

            "aac5" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                }
            }

            "test" -> {
                if (mc.thePlayer.ticksExisted % 7 == 0) {
                    mc.thePlayer.jumpMovementFactor = 0.42f
                }
                if (mc.thePlayer.ticksExisted % 7 == 1) {
                    mc.thePlayer.jumpMovementFactor = 0.33f
                }
                if (mc.thePlayer.ticksExisted % 7 == 2) {
                    mc.thePlayer.jumpMovementFactor = 0.08f
                }
            }

            "rewinside" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                }
            }
        }
    }

    fun onJump(event: JumpEvent) {
        if (modeValue.equals("AAC4")) {
            event.cancelEvent()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
    }

    override val tag: String
        get() = modeValue.get()
}
