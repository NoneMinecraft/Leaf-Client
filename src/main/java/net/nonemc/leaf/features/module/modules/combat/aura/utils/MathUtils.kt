package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.minecraft.util.Vec3

fun getRotationToVec(yaw: Float, pitch: Float): Vec3 {
    val radianYaw = Math.toRadians(yaw.toDouble())
    val radianPitch = Math.toRadians(pitch.toDouble())

    val x = -Math.sin(radianYaw.toDouble()) * Math.cos(radianPitch.toDouble())
    val y = -Math.sin(radianPitch.toDouble())
    val z = Math.cos(radianYaw.toDouble()) * Math.cos(radianPitch.toDouble())

    return Vec3(x, y, z)
}