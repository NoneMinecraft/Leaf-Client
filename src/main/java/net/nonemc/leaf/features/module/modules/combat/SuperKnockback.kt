package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.event.AttackEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.MovementUtils
import net.nonemc.leaf.utils.Rotation
import net.nonemc.leaf.utils.RotationUtils
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "SuperKnockback", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Wtap", "Stap", "WtapStopMotion", "Legit", "LegitSneak", "Silent", "SprintReset", "SneakPacket"), "Legit")
    private val onlyMoveValue = BoolValue("OnlyMove", true)
    private val onlyMoveForwardValue = BoolValue("OnlyMoveForward", true). displayable { onlyMoveValue.get() }
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    private var ticks = 0

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delayValue.get().toLong()) ||
                (!MovementUtils.isMoving() && onlyMoveValue.get()) || (!mc.thePlayer.onGround && onlyGroundValue.get())) {
                return
            }

            if (onlyMoveForwardValue.get() && RotationUtils.getRotationDifference(Rotation(MovementUtils.movingYaw, mc.thePlayer.rotationPitch), Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 35) {
                return
            }

            when (modeValue.get().lowercase()) {

                "wtap", "stap", "wtapstopmotion", "legit", "legitsneak" ->  ticks = 2

                "sprintreset" -> {
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                }

                "sneakpacket" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    mc.thePlayer.serverSprintState = true
                }
            }
            timer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().lowercase()) {
            "wtap", "wtapstopmotion" -> {
                if (ticks == 2) {
                    mc.gameSettings.keyBindForward.pressed = false
                    if (modeValue.equals("WtapStopMotion")) MovementUtils.resetMotion(false)
                    ticks = 1
                } else if (ticks == 1) {
                    mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                    ticks = 0
                }
            }
            "stap" -> {
                if (ticks == 2) {
                    mc.gameSettings.keyBindForward.pressed = false
                    mc.gameSettings.keyBindBack.pressed = true
                    ticks = 1
                } else if (ticks == 1) {
                    mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                    mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
                    ticks = 0
                }
            }
            "legit" -> {
                if (ticks == 2) {
                    mc.thePlayer.isSprinting = false
                    ticks = 1
                } else if (ticks == 1) {
                    mc.thePlayer.isSprinting = true
                    ticks = 0
                }
            }
            "legitsneak" -> {
                if (ticks == 2) {
                    mc.gameSettings.keyBindSneak.pressed = true
                    ticks = 1
                } else if (ticks == 1) {
                    mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
                    ticks = 0
                }
            }
            "silent" -> {
                if (ticks == 1) {
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    ticks = 2
                } else if (ticks == 2) {
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    ticks = 0
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}
