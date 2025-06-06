﻿//All the code was written by N0ne.
package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.nonemc.leaf.event.AttackEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "Criticals", category = ModuleCategory.COMBAT, autoDisable = EnumAutoDisableType.FLAG)
class Criticals : Module() {
    private val mode = ListValue(
        "Mode",
        arrayOf(
            "C04",
            "Motion",
            "LegitJump",
            "FastPos",
            "SetPos",
            "FastMotion",
            "Packet",
            "More",
            "Double",
            "PreDouble",
            "Smart",
            "NoGround"
        ),
        "C04"
    )
    private val MotionValue = FloatValue("MotionValue", 0.1F, 0.01F, 0.42F)
    private val hurttime = IntegerValue("HurtTime", 7, 1, 10)
    private var LastRegen = 0
    private var reverse = false
    var tick = 0
    override fun onDisable() {
        tick = 0
        reverse = false
        LastRegen = 0
    }

    override fun onEnable() {
        when (mode.get()) {
            "NoGround" -> {
                mc.thePlayer.jump()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer
        when (mode.get()) {
            "Smart" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    when (mc.thePlayer.hurtTime) {
                        in 9..10 -> {
                            mc.thePlayer.motionY = -1.0
                        }

                        in 7..8 -> {
                            mc.thePlayer.motionY = 0.0809
                        }

                        in 5..6 -> {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.345,
                                mc.thePlayer.posZ
                            )
                        }

                        in 3..4 -> {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY - 0.255,
                                mc.thePlayer.posZ
                            )
                        }

                        in 1..2 -> {
                            mc.thePlayer.motionY = -0.42
                        }
                    }
                } else if (mc.thePlayer.onGround && mc.thePlayer.isSwingInProgress) {
                    mc.thePlayer.setPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 0.2345,
                        mc.thePlayer.posZ
                    )
                    mc.thePlayer.setPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 0.2345,
                        mc.thePlayer.posZ
                    )
                }
            }

            "Double" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in 9..10) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.514,
                                mc.thePlayer.posZ
                            )
                        }
                    } else if (mc.thePlayer.hurtTime in 7..8) {
                        if (mc.thePlayer.foodStats.foodLevel < 15) {
                            mc.thePlayer.motionY = -0.783
                        } else {
                            mc.thePlayer.motionY = -0.424
                        }
                    }
                    if (mc.thePlayer.hurtTime in 5..6) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.514,
                                mc.thePlayer.posZ
                            )
                        }
                    } else if (mc.thePlayer.hurtTime in 3..4) {
                        if (mc.thePlayer.foodStats.foodLevel < 15) {
                            mc.thePlayer.motionY = -0.783
                        } else {
                            mc.thePlayer.motionY = -0.424
                        }
                    }
                }
            }

            "PreDouble" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in 9..10) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX + 0.0561,
                                mc.thePlayer.posY + 0.514,
                                mc.thePlayer.posZ + 0.0374
                            )
                        }
                    } else if (mc.thePlayer.hurtTime in 7..8) {
                        if (mc.thePlayer.foodStats.foodLevel < 18) {
                            mc.thePlayer.motionY = -0.893
                        } else {
                            mc.thePlayer.motionY = -0.524
                        }
                    }
                    if (mc.thePlayer.hurtTime in 5..6) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX + 0.0561,
                                mc.thePlayer.posY + 0.514,
                                mc.thePlayer.posZ + 0.0374
                            )
                        }
                    } else if (mc.thePlayer.hurtTime in 3..4) {
                        if (mc.thePlayer.foodStats.foodLevel < 15) {
                            mc.thePlayer.motionY = -0.783
                        } else {
                            mc.thePlayer.motionY = -0.424
                        }
                    }
                } else if (mc.thePlayer.isSwingInProgress) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.34
                    } else {
                        mc.thePlayer.motionY = 0.0
                    }
                }
            }

            "FastMotion" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime.toDouble() in hurttime.get().toDouble()..10.0) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.1423
                        }
                    } else if (mc.thePlayer.hurtTime.toDouble() in 0.0..6.999) {
                        if (mc.thePlayer.foodStats.foodLevel < 15) {
                            mc.thePlayer.motionY = -0.123
                        } else {
                            mc.thePlayer.motionY = -0.424

                        }
                    }
                }
            }

            "FastPos" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime.toDouble() in 7.0..10.0) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX + 0.0561,
                                mc.thePlayer.posY + 0.514,
                                mc.thePlayer.posZ + 0.0374
                            )
                        }
                    } else if (mc.thePlayer.hurtTime.toDouble() in 0.0..6.999) {
                        if (mc.thePlayer.foodStats.foodLevel < 15) {
                            mc.thePlayer.motionY = -0.783
                        } else {
                            mc.thePlayer.motionY = -0.424
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        when (mode.get()) {
            "NoGround" -> {
                if (event.packet is C03PacketPlayer) {
                    event.packet.onGround = false
                }
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity as? EntityLivingBase ?: return

        when (mode.get()) {

            "More" -> {
                sendCriticalPacket(yOffset = 0.00000000001, ground = false)
                sendCriticalPacket(ground = false)
            }

            "NCP" -> {
                sendCriticalPacket(yOffset = 0.11, ground = false)
                sendCriticalPacket(yOffset = 0.1100013579, ground = false)
                sendCriticalPacket(yOffset = 0.0000013579, ground = false)
            }


            "Packet" -> {
                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 0.0625,
                        mc.thePlayer.posZ,
                        true
                    )
                )
                mc.thePlayer.onGround = false

                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 1.1E-5,
                        mc.thePlayer.posZ,
                        false
                    )
                )
                mc.thePlayer.onGround = false
            }

            "C04" -> {

                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 2.43192168e-14,
                        mc.thePlayer.posZ,
                        true
                    )
                )
                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 1.265e-256,
                        mc.thePlayer.posZ,
                        false
                    )
                )
            }

            "SetPos" -> {
                if (mc.thePlayer.onGround) tick = 0
                if (tick < 1) {
                    tick++
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.234, mc.thePlayer.posZ)
                } else {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.234, mc.thePlayer.posZ)
                    tick = 0
                }
            }

            "Motion" -> {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = MotionValue.get().toDouble()
                }
            }

            "LegitJump" -> {
                mc.gameSettings.keyBindJump.pressed = true
            }
        }
    }

    override val tag: String
        get() = mode.get()

    private fun onceFunc(arr1: IntArray, arr2: IntArray): IntArray {
        return intArrayOf(arr1[0] * arr2[0], arr1[1] * arr2[1])
    }

    private fun inRange(value: Int, min: Int, max: Int): Boolean {
        return value in min..max
    }

    private fun setPos(x: Double, y: Double, z: Double) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z)
    }

    private fun criticalCondition(): Boolean {
        return true
    }

    private fun onceFunc(range1: Array<Double>, range2: Array<Double>): Array<Double> {
        return arrayOf(0.5, 3.0)
    }

    private fun sendPositionPacket(x: Double, y: Double, z: Double) {
        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX + x,
                mc.thePlayer.posY + y,
                mc.thePlayer.posZ + z,
                mc.thePlayer.onGround
            )
        )
    }

    companion object {
        var reverse = false
    }

    fun sendCriticalPacket(
        xOffset: Double = 0.0,
        yOffset: Double = 0.0,
        zOffset: Double = 0.0,
        ground: Boolean,
    ) {
        val x = mc.thePlayer.posX + xOffset
        val y = mc.thePlayer.posY + yOffset
        val z = mc.thePlayer.posZ + zOffset
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, ground))
    }
}
