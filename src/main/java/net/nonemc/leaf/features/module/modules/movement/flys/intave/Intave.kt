package net.nonemc.leaf.features.module.modules.movement.flys.intave

import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.movement.flys.FlyMode
import net.nonemc.leaf.utils.entity.MovementUtils

class Intave : FlyMode("Intave") {
    private var ticks = 0
    private var modifyTicks = 0
    private var stage = FlyStage.WAITING
    private var flags = 0
    private var groundX = 0.0
    private var groundY = 0.0
    private var groundZ = 0.0

    override fun onEnable() {
        ticks = 0
        modifyTicks = 0
        flags = 0
        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY * 2).toDouble() / 2, mc.thePlayer.posZ)
        stage = FlyStage.WAITING
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
    }

    override fun onUpdate(event: UpdateEvent) {
        ticks++
        modifyTicks++
        mc.gameSettings.keyBindJump.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        when (stage) {
            FlyStage.FLYING, FlyStage.WAITING -> {
                if (ticks == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && modifyTicks >= 6 && mc.theWorld.getCollisionBoxes(
                        mc.thePlayer.entityBoundingBox.offset(0.0, 0.5, 0.0)
                    ).isEmpty()
                ) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ)
                    modifyTicks = 0
                }
                if (!MovementUtils.isMoving() && ticks == 1 && (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || GameSettings.isKeyDown(
                        mc.gameSettings.keyBindJump
                    )) && modifyTicks >= 5
                ) {
                    var playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                    mc.thePlayer.setPosition(
                        mc.thePlayer.posX + 0.05 * -Math.sin(playerYaw),
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 0.05 * Math.cos(playerYaw)
                    )
                }
                if (ticks == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && modifyTicks >= 6 && mc.theWorld.getCollisionBoxes(
                        mc.thePlayer.entityBoundingBox.offset(0.0, -0.5, 0.0)
                    ).isEmpty()
                ) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)
                    modifyTicks = 0
                }
                mc.thePlayer.onGround = true
                mc.thePlayer.motionY = 0.0
            }

            FlyStage.WAIT_APPLY -> {
                mc.timer.timerSpeed = 1.0f
                MovementUtils.resetMotion(true)
                mc.thePlayer.jumpMovementFactor = 0.0f
                if (modifyTicks >= 10) {
                    var playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                    if (!(modifyTicks % 2 == 0)) {
                        mc.thePlayer.setPosition(
                            mc.thePlayer.posX + 0.1 * -Math.sin(playerYaw),
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ + 0.1 * Math.cos(playerYaw)
                        )
                    } else {
                        mc.thePlayer.setPosition(
                            mc.thePlayer.posX - 0.1 * -Math.sin(playerYaw),
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ - 0.1 * Math.cos(playerYaw)
                        )
                        if (modifyTicks >= 16 && ticks == 2) {
                            modifyTicks = 16
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            if (ticks > 2) {
                ticks = 0
                packet.y += 0.5
            }
            packet.onGround = true
        }
    }

    enum class FlyStage {
        WAITING,
        FLYING,
        WAIT_APPLY
    }
}
