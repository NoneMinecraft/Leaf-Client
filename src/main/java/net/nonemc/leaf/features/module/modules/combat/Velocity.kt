package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.nonemc.leaf.event.*
import net.nonemc.leaf.libs.packet.PacketText.chatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import kotlin.math.abs
import kotlin.math.atan2

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {
    private val mode = ListValue(
        "Mode", arrayOf(
            "Cancel", "Jump", "IntaveJump", "IntaveTimer",
            "Custom", "CustomTimer", "Intave", "AAC5","PacketSpoofFlag"
        ), "Cancel"
    )

    private val customX = FloatValue("Custom-MotionX", 0F, 0F, 1F).displayable { mode.get() == "Custom" }
    private val customY = FloatValue("Custom-MotionY", 0F, 0F, 1F).displayable { mode.get() == "Custom" }
    private val customZ = FloatValue("Custom-MotionZ", 0F, 0F, 1F).displayable { mode.get() == "Custom" }
    private val customMotionMaxHurtTime =
        IntegerValue("Custom-Motion-MaxHurtTime", 10, 0, 10).displayable { mode.get() == "Custom" }
    private val customMotionMinHurtTime =
        IntegerValue("Custom-Motion-MinHurtTime", 0, 0, 10).displayable { mode.get() == "Custom" }
    private val customJump = BoolValue("Custom-Jump", false).displayable { mode.get() == "Custom" }
    private val customJumpSprint = BoolValue("Custom-Jump-Sprint", false).displayable { mode.get() == "Custom" }
    private val customJumpMaxHurtTime =
        IntegerValue("Custom-Jump-MaxHurtTime", 10, 0, 10).displayable { mode.get() == "Custom" && customJump.get() }
    private val customJumpMinHurtTime =
        IntegerValue("Custom-Jump-MinHurtTime", 0, 0, 10).displayable { mode.get() == "Custom" && customJump.get() }


    private val customTimerLow = FloatValue("CustomTimer-Low", 0.7F, 0F, 1F).displayable { mode.get() == "CustomTimer" }
    private val customTimerMax = FloatValue("CustomTimer-Max", 1.2F, 0F, 1F).displayable { mode.get() == "CustomTimer" }
    private val customTimerLowTick =
        IntegerValue("CustomTimer-LowTick", 5, 0, 10).displayable { mode.get() == "CustomTimer" }
    private val customTimerC03 = BoolValue("CustomTimer-C03", true).displayable { mode.get() == "CustomTimer" }


    private val intaveVelocity = FloatValue("Intave-Velocity", 0.66F, 0F, 1F).displayable { mode.get() == "Intave" }
    private val intaveMaxHurtTime = IntegerValue("Intave-MaxHurtTime", 6, 0, 10).displayable { mode.get() == "Intave" }
    private val intaveMinHurtTime = IntegerValue("Intave-MinHurtTime", 3, 0, 10).displayable { mode.get() == "Intave" }

    private val packetSpoofFlagMaxHurtTime = IntegerValue("PacketSpoofFlag-MaxHurtTime", 6, 0, 10).displayable { mode.get() == "PacketSpoofFlag" }
    private val packetSpoofFlagMinHurtTime = IntegerValue("PacketSpoofFlag-MinHurtTime", 3, 0, 10).displayable { mode.get() == "PacketSpoofFlag" }
    private val packetSpoofFlagTimer = FloatValue("PacketSpoofFlag-Timer", 0.7f, 0.3f, 1.0f).displayable { mode.get() == "PacketSpoofFlag" }
    private val packetSpoofFlagSleep = IntegerValue("PacketSpoofFlag-Sleep",  50, 0, 5000).displayable { mode.get() == "PacketSpoofFlag" }

    private val sleepTime = MSTimer()



    private val objectMouseOver = BoolValue("ObjectMouseOver", false).displayable { mode.get() == "Intave" }
    private val forward = BoolValue("Forward", true).displayable { mode.get() == "Intave" }
    private val swingInProgress = BoolValue("SwingInProgress", true).displayable { mode.get() == "Intave" }
    private val sprint = BoolValue("Sprint", true).displayable { mode.get() == "Intave" }
    private val noGUI = BoolValue("NoGUI", true).displayable { mode.get() == "Intave" }
    private val jump2 = BoolValue("Jump", true).displayable { mode.get() == "Intave" }
    private val reduceYValue = BoolValue("ReduceY", false).displayable { mode.get() == "Intave" }
    private val reduceY = FloatValue("ReduceVelocity", 0.99f, 0f, 1f).displayable { mode.get() == "Intave" }
    private val onHurt = BoolValue("OnHurt", true).displayable { mode.get() == "Intave" }
    private val debug = BoolValue("Debug", false)
    var jump = false
    private var jumped = 0
    var jumped2 = false
    private var hasReceivedVelocity = false
    private var limitUntilJump = 0
    private var attack = false

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attack = true
        when (mode.get()) {
            "Intave" -> {
                if (objectMouseOver.get() && mc.objectMouseOver == mc.thePlayer) return
                if (!onHurt.get() || mc.thePlayer.hurtTime != 0) {
                    mc.gameSettings.keyBindJump.pressed = mc.thePlayer.onGround && jump2.get()
                    if (mc.thePlayer.hurtTime in intaveMinHurtTime.get()..intaveMaxHurtTime.get() &&
                        (!forward.get() || mc.gameSettings.keyBindForward.pressed) &&
                        (!swingInProgress.get() || mc.thePlayer.isSwingInProgress) && (!sprint.get() || mc.thePlayer.isSprinting) &&
                        (!noGUI.get() || mc.currentScreen == null)
                    ) {
                        if (reduceYValue.get()) mc.thePlayer.motionY *= reduceY.get()
                        mc.thePlayer.motionZ *= intaveVelocity.get()
                        mc.thePlayer.motionX *= intaveVelocity.get()
                        if (debug.get()) chatPrint("[Velocity] ${intaveVelocity.get()}")
                    }
                }
            }
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val player = mc.thePlayer ?: return
        if (mode.get() == "Jump" && hasReceivedVelocity) {
            if (player.isSprinting && player.onGround && player.hurtTime == 9) {
                player.tryJump()
                limitUntilJump = 0
                if (debug.get()) chatPrint("Jump")
            }
            hasReceivedVelocity = false
            return
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val player = mc.thePlayer ?: return
        when (mode.get()) {
            "PacketSpoofFlag"-> {
                if (sleepTime.hasTimePassed(packetSpoofFlagSleep.get().toLong())) {
                    if (player.hurtTime in packetSpoofFlagMinHurtTime.get()..packetSpoofFlagMaxHurtTime.get()) {
                        if (player.onGround) {
                            mc.gameSettings.keyBindJump.pressed = true
                        } else {
                            mc.timer.timerSpeed = packetSpoofFlagTimer.get()
                            if (packet is C03PacketPlayer) packet.onGround = true
                            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                            sleepTime.reset()
                        }
                    }
                }
            }

            "Jump" -> {
                var packetDirection = 0.0
                when (packet) {
                    is S12PacketEntityVelocity -> {
                        val motionX = packet.motionX.toDouble()
                        val motionZ = packet.motionZ.toDouble()

                        packetDirection = atan2(motionX, motionZ)
                    }

                    is S27PacketExplosion -> {
                        val motionX = player.motionX + packet.x
                        val motionZ = player.motionZ + packet.y

                        packetDirection = atan2(motionX, motionZ)
                    }
                }
                val degreePlayer = getDirection()
                val degreePacket = Math.floorMod(packetDirection.toDegrees().toInt(), 360).toDouble()
                var angle = abs(degreePacket + degreePlayer)
                val threshold = 120.0
                angle = Math.floorMod(angle.toInt(), 360).toDouble()
                val inRange = angle in 180 - threshold / 2..180 + threshold / 2
                if (inRange)
                    hasReceivedVelocity = true
            }

            "Cancel" -> {
                if (packet is S12PacketEntityVelocity) {
                    event.cancelEvent()
                }
            }

            "CustomTimer" -> {
                if (customTimerC03.get() && event.packet is C03PacketPlayer && !(event.packet is C04PacketPlayerPosition || event.packet is C03PacketPlayer.C05PacketPlayerLook || event.packet is C03PacketPlayer.C06PacketPlayerPosLook)) {
                    event.cancelEvent()
                }
            }

            "IntaveTimer" -> {
                if (mc.thePlayer.hurtTime != 0 && event.packet is C03PacketPlayer && !(event.packet is C04PacketPlayerPosition
                            || event.packet is C03PacketPlayer.C05PacketPlayerLook || event.packet is C03PacketPlayer.C06PacketPlayerPosLook)
                ) {
                    event.cancelEvent()
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime != 0) {
            when (mode.get()) {
                "AAC5" -> {
                    if (mc.thePlayer.hurtTime > 1) {
                        mc.thePlayer.motionX *= 0.81
                        mc.thePlayer.motionZ *= 0.81
                    }
                }

                "Custom" -> {
                    if (mc.thePlayer.hurtTime in customMotionMinHurtTime.get()..customMotionMaxHurtTime.get()) {
                        mc.thePlayer.motionX = customX.get().toDouble()
                        mc.thePlayer.motionY = customY.get().toDouble()
                        mc.thePlayer.motionZ = customZ.get().toDouble()
                    }
                    if (mc.thePlayer.hurtTime in customJumpMinHurtTime.get()..customJumpMaxHurtTime.get()
                        && mc.currentScreen == null && (!customJumpSprint.get() || mc.thePlayer.isSprinting)
                    ) {
                        mc.gameSettings.keyBindJump.pressed = true
                    } else {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    }
                }

                "IntaveJump" -> {
                    if (mc.thePlayer.hurtTime == 9) {
                        if (++jumped % 2 == 0 && mc.thePlayer.onGround && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                            mc.gameSettings.keyBindJump.pressed = true
                            jumped = 0
                        }
                    } else {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    }
                }

                "CustomTimer" -> {
                    if (mc.thePlayer.hurtTime > customTimerLowTick.get()) {
                        mc.timer.timerSpeed = customTimerLow.get()
                    } else if (!mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = customTimerMax.get()
                    }
                }

                "IntaveTimer" -> {
                    if (mc.thePlayer.hurtTime > 5) {
                        mc.timer.timerSpeed = 0.789F
                    } else if (!mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 1.321F
                    }
                }
            }
        }
    }

    override val tag: String
        get() = mode.get()

    fun Float.toRadians() = this * 0.017453292f
    fun Double.toRadians() = this * 0.017453292
    fun Double.toDegrees() = this * 57.295779513

    private fun getDirection(): Double {
        var moveYaw = mc.thePlayer.rotationYaw
        if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing == 0f) {
            moveYaw += if (mc.thePlayer.moveForward > 0) 0 else 180
        } else if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing != 0f) {
            if (mc.thePlayer.moveForward > 0) moveYaw += if (mc.thePlayer.moveStrafing > 0) -45 else 45 else moveYaw -= if (mc.thePlayer.moveStrafing > 0) -45 else 45
            moveYaw += if (mc.thePlayer.moveForward > 0) 0 else 180
        } else if (mc.thePlayer.moveStrafing != 0f && mc.thePlayer.moveForward == 0f) {
            moveYaw += if (mc.thePlayer.moveStrafing > 0) -90 else 90
        }
        return Math.floorMod(moveYaw.toInt(), 360).toDouble()
    }

    private fun EntityPlayerSP.tryJump() {
        if (!mc.gameSettings.keyBindJump.isKeyDown) {
            this.jump()
        }
    }

    override fun onDisable() {
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        attack = false
        jump = false
        jumped = 0
        jumped2 = false
    }
}