﻿package net.nonemc.leaf.features.module.modules.movement

import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.potion.Potion
import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.combat.Aura.sprintValue
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT, defaultOn = true)
class Sprint : Module() {
    private val mode = ListValue("Mode", arrayOf("Normal", "Legit"), "Legit")
    val jumpDirectionsValue = BoolValue("JumpDirections", false)
    val allDirectionsValue = BoolValue("AllDirections", true)
    private val allDirectionsBypassValue = ListValue(
        "AllDirectionsBypass",
        arrayOf("Rotate", "Rotate2", "Toggle", "Minemora", "Spoof", "LimitSpeed", "None"),
        "None"
    ).displayable { allDirectionsValue.get() }
    private val blindnessValue = BoolValue("Blindness", true)
    val useItemValue = BoolValue("UseItem", false)
    val foodValue = BoolValue("Food", true)
    val noStopServerSide = BoolValue("ServerSideKeepSprint", false).displayable { !noPacket.get() }
    val checkServerSide = BoolValue("CheckServerSide", false)
    val checkServerSideGround = BoolValue("CheckServerSideOnlyGround", false).displayable { checkServerSide.get() }
    private val noPacket = BoolValue("NoPacket", false)
    private val allDirectionsLimitSpeedGround = BoolValue("AllDirectionsLimitSpeedOnlyGround", true)
    private val allDirectionsLimitSpeedValue = FloatValue(
        "AllDirectionsLimitSpeed",
        0.7f,
        0.5f,
        1f
    ).displayable { allDirectionsBypassValue.displayable && allDirectionsBypassValue.equals("LimitSpeed") }

    private var spoofStat = false
        set(value) {
            if (field != value) {
                if (value) {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                } else {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                }
                field = value
            }
        }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (sprintValue) {
            if (mode.get() == "Normal") {

                mc.thePlayer.isSprinting = true

                if (!EntityMoveLib.isMoving() || mc.thePlayer.isSneaking || blindnessValue.get() &&
                    mc.thePlayer.isPotionActive(Potion.blindness) || foodValue.get() &&
                    !(mc.thePlayer.foodStats.foodLevel > 6.0f || mc.thePlayer.capabilities.allowFlying) ||
                    (useItemValue.get() && mc.thePlayer.isUsingItem) ||
                    (checkServerSide.get() && (mc.thePlayer.onGround || !checkServerSideGround.get()) &&
                            !allDirectionsValue.get() && RotationBaseLib.targetRotation != null &&
                            RotationBaseLib.getRotationDifference(
                                Rotation(
                                    mc.thePlayer.rotationYaw,
                                    mc.thePlayer.rotationPitch
                                )
                            ) > 30)
                ) {
                    mc.thePlayer.isSprinting = false
                    return
                }

                if (allDirectionsValue.get()) {
                    mc.thePlayer.isSprinting = true
                    if (RotationBaseLib.getRotationDifference(
                            Rotation(
                                (EntityMoveLib.direction * 180f / Math.PI).toFloat(),
                                mc.thePlayer.rotationPitch
                            )
                        ) > 30
                    ) {
                        when (allDirectionsBypassValue.get().lowercase()) {
                            "rotate" -> RotationBaseLib.setTargetRotation(
                                Rotation(
                                    EntityMoveLib.movingYaw,
                                    mc.thePlayer.rotationPitch
                                ), 10
                            )

                            "rotate2" -> {
                                var movingForward = mc.thePlayer.moveForward > 0.0F
                                var movingBackward = mc.thePlayer.moveForward < 0.0F
                                var movingRight = mc.thePlayer.moveStrafing > 0.0F
                                var movingLeft = mc.thePlayer.moveStrafing < 0.0F

                                var MovingSideways = movingLeft || movingRight
                                var MovingStraight = movingForward || movingBackward
                                var direction = mc.thePlayer.rotationYaw

                                if (movingForward && !MovingSideways) {

                                } else if (movingBackward && !MovingSideways) {
                                    direction += 180.0f
                                } else if (movingForward && movingLeft) {
                                    direction += 45.0f
                                } else if (movingForward) {
                                    direction -= 45.0f
                                } else if (!MovingStraight && movingLeft) {
                                    direction += 90.0f
                                } else if (!MovingStraight && movingRight) {
                                    direction -= 90.0f
                                } else if (movingBackward && movingRight) {
                                    direction -= 135.0f
                                } else if (movingBackward) {
                                    direction += 135.0f
                                }

                                RotationBaseLib.setTargetRotation(Rotation(direction, mc.thePlayer.rotationPitch), 10)

                            }

                            "toggle" -> {
                                mc.netHandler.addToSendQueue(
                                    C0BPacketEntityAction(
                                        mc.thePlayer,
                                        C0BPacketEntityAction.Action.STOP_SPRINTING
                                    )
                                )
                                mc.netHandler.addToSendQueue(
                                    C0BPacketEntityAction(
                                        mc.thePlayer,
                                        C0BPacketEntityAction.Action.START_SPRINTING
                                    )
                                )
                            }

                            "minemora" -> {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.setPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY + 0.0000013,
                                        mc.thePlayer.posZ
                                    )
                                    mc.thePlayer.motionY = 0.0
                                }
                            }

                            "limitspeed" -> {
                                if (!allDirectionsLimitSpeedGround.get() || mc.thePlayer.onGround) {
                                    EntityMoveLib.limitSpeedByPercent(allDirectionsLimitSpeedValue.get())
                                }
                            }

                            "spoof" -> spoofStat = true
                        }
                    } else {
                        when (allDirectionsBypassValue.get().lowercase()) {
                            "spoof" -> spoofStat = false
                        }
                    }
                }
            } else {
                mc.gameSettings.keyBindSprint.pressed = true
            }
        } else {
            mc.gameSettings.keyBindSprint.pressed = false
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mode.get() == "Normal") {
            val packet = event.packet

            if (noPacket.get() && packet is C0BPacketEntityAction && (packet.action == C0BPacketEntityAction.Action.START_SPRINTING || packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                event.cancelEvent()
            }
            if (noStopServerSide.get() && packet is C0BPacketEntityAction && packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancelEvent()
            }
        }
    }
}
