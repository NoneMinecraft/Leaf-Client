package net.nonemc.leaf.utils.rotation

import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.nonemc.leaf.data.Rotation
import net.nonemc.leaf.utils.mc

fun fixRotation(current: Float, target: Float): Float {
    val f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F
    val gcd = f * f * f * 1.2F
    var delta = target - current
    delta -= delta % gcd
    return current + delta
}
fun getRotationToVec(yaw: Float, pitch: Float): Vec3 {
    val radianYaw = Math.toRadians(yaw.toDouble())
    val radianPitch = Math.toRadians(pitch.toDouble())
    val x = -Math.sin(radianYaw) * Math.cos(radianPitch)
    val y = -Math.sin(radianPitch)
    val z = Math.cos(radianYaw) * Math.cos(radianPitch)
    return Vec3(x, y, z)
}
fun getRotationTo(from: Vec3, to: Vec3): Rotation {
    val diffX = to.xCoord - from.xCoord
    val diffY = to.yCoord - from.yCoord
    val diffZ = to.zCoord - from.zCoord
    val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)
    val yaw = (MathHelper.atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90.0f
    val pitch = -(MathHelper.atan2(diffY, dist.toDouble()) * 180.0 / Math.PI).toFloat()
    return Rotation(yaw, pitch)
}