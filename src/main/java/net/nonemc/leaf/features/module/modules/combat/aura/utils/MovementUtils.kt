package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.nonemc.leaf.utils.mc
import kotlin.math.cos
import kotlin.math.sin

fun moveTo(currentYaw:Float, targetYaw: Float) {
    val settings = mc.gameSettings
    val targetRad = Math.toRadians(targetYaw.toDouble())
    val worldDirX = -sin(targetRad).toFloat()
    val worldDirZ = cos(targetRad).toFloat()
    val playerYawRad = Math.toRadians(currentYaw.toDouble())
    val localDirX = worldDirX * cos(playerYawRad) + worldDirZ * sin(playerYawRad)
    val localDirZ = -worldDirX * sin(playerYawRad) + worldDirZ * cos(playerYawRad)
    val forward = localDirZ
    val strafe = localDirX
    val deadZone = 0.1f
    settings.keyBindForward.pressed = forward > deadZone
    settings.keyBindBack.pressed = forward < -deadZone
    settings.keyBindLeft.pressed = strafe > deadZone
    settings.keyBindRight.pressed = strafe < -deadZone
    if (settings.keyBindForward.pressed) settings.keyBindBack.pressed = false
    if (settings.keyBindLeft.pressed) settings.keyBindRight.pressed = false
}