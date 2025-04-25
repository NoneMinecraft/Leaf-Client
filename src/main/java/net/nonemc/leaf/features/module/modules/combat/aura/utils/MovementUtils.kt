package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.nonemc.leaf.libs.base.mc
import kotlin.math.cos
import kotlin.math.sin

fun moveTo(currentYaw: Float, targetYaw: Float) {
    val settings = net.nonemc.leaf.libs.base.mc.gameSettings
    val targetRad = Math.toRadians(targetYaw.toDouble())
    val worldDirX = -sin(targetRad).toFloat()
    val worldDirZ = cos(targetRad).toFloat()
    val playerYawRad = Math.toRadians(currentYaw.toDouble())
    val localDirX = worldDirX * cos(playerYawRad) + worldDirZ * sin(playerYawRad)
    val localDirZ = -worldDirX * sin(playerYawRad) + worldDirZ * cos(playerYawRad)
    val deadZone = 0.1f
    settings.keyBindForward.pressed = localDirZ > deadZone
    settings.keyBindBack.pressed = localDirZ < -deadZone
    settings.keyBindLeft.pressed = localDirX > deadZone
    settings.keyBindRight.pressed = localDirX < -deadZone
    if (settings.keyBindForward.pressed) settings.keyBindBack.pressed = false
    if (settings.keyBindLeft.pressed) settings.keyBindRight.pressed = false
}