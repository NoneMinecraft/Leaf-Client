﻿package net.nonemc.leaf.libs.entity

import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.potion.Potion
import net.minecraft.util.AxisAlignedBB
import net.nonemc.leaf.libs.base.MinecraftInstance
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object EntityMoveLib : MinecraftInstance() {
    fun resetMotion(y: Boolean) {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        if (y) mc.thePlayer.motionY = 0.0
    }

    fun getSpeed(): Float {
        return sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ).toFloat()
    }

    fun strafe() {
        strafe(getSpeed())
    }

    fun move() {
        move(getSpeed())
    }

    fun isMoving(): Boolean {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f)
    }

    fun hasMotion(): Boolean {
        return mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0 && mc.thePlayer.motionY != 0.0
    }

    fun strafe(speed: Float) {
        if (!isMoving()) return
        mc.thePlayer.motionX = -sin(direction) * speed
        mc.thePlayer.motionZ = cos(direction) * speed
    }

    fun move(speed: Float) {
        if (!isMoving()) return
        val yaw = direction
        mc.thePlayer.motionX += -sin(yaw) * speed
        mc.thePlayer.motionZ += cos(yaw) * speed
    }

    fun limitSpeedByPercent(percent: Float) {
        mc.thePlayer.motionX *= percent
        mc.thePlayer.motionZ *= percent
    }

    fun forward(length: Double) {
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        mc.thePlayer.setPosition(
            mc.thePlayer.posX + -sin(yaw) * length,
            mc.thePlayer.posY,
            mc.thePlayer.posZ + cos(yaw) * length
        )
    }

    val direction: Double
        get() {
            var rotationYaw = mc.thePlayer.rotationYaw
            if (mc.thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (mc.thePlayer.moveForward < 0f) forward = -0.5f else if (mc.thePlayer.moveForward > 0f) forward = 0.5f
            if (mc.thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (mc.thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }

    val jumpMotion: Float
        get() {
            var mot = 0.42f
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                mot += (mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1).toFloat() * 0.1f
            }
            return mot
        }

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()

    var bps = 0.0
        private set
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0

    fun setMotion(speed: Double) {
        var forward = mc.thePlayer.movementInput.moveForward.toDouble()
        var strafe = mc.thePlayer.movementInput.moveStrafe.toDouble()
        var yaw = mc.thePlayer.rotationYaw
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = cos(Math.toRadians((yaw + 90.0f).toDouble()))
            val sin = sin(Math.toRadians((yaw + 90.0f).toDouble()))
            mc.thePlayer.motionX = (forward * speed * cos +
                    strafe * speed * sin)
            mc.thePlayer.motionZ = (forward * speed * sin -
                    strafe * speed * cos)
        }
    }

    fun updateBlocksPerSecond() {
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) {
            bps = 0.0
        }
        val distance = mc.thePlayer.getDistance(lastX, lastY, lastZ)
        lastX = mc.thePlayer.posX
        lastY = mc.thePlayer.posY
        lastZ = mc.thePlayer.posZ
        bps = distance * (20 * mc.timer.timerSpeed)
    }

    private fun calculateGround(): Double {
        val playerBoundingBox = mc.thePlayer.entityBoundingBox
        var blockHeight = 1.0
        var ground = mc.thePlayer.posY
        while (ground > 0.0) {
            val customBox = AxisAlignedBB(
                playerBoundingBox.maxX,
                ground + blockHeight,
                playerBoundingBox.maxZ,
                playerBoundingBox.minX,
                ground,
                playerBoundingBox.minZ
            )
            if (mc.theWorld.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }
        return 0.0
    }

    fun handleVanillaKickBypass() {
        val ground = calculateGround()
        run {
            var posY = mc.thePlayer.posY
            while (posY > ground) {
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
                if (posY - 8.0 < ground) break // Prevent next step
                posY -= 8.0
            }
        }
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true))
        var posY = ground
        while (posY < mc.thePlayer.posY) {
            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
            if (posY + 8.0 > mc.thePlayer.posY) break // Prevent next step
            posY += 8.0
        }
        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
    }
}
