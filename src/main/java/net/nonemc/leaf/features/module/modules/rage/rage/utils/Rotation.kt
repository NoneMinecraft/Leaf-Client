package net.nonemc.leaf.features.module.modules.rage.rage.utils

import net.nonemc.leaf.utils.Rotation
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3

fun getRotationTo(from: Vec3, to: Vec3): Rotation {
    val diffX = to.xCoord - from.xCoord
    val diffY = to.yCoord - from.yCoord
    val diffZ = to.zCoord - from.zCoord
    val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)
    val yaw = (MathHelper.atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90.0f
    val pitch = -(MathHelper.atan2(diffY, dist.toDouble()) * 180.0 / Math.PI).toFloat()
    return Rotation(yaw, pitch)
}