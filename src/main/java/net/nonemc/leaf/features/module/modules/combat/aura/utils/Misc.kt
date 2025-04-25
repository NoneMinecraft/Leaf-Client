package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.minecraft.util.MathHelper
import net.nonemc.leaf.file.storageRotationDir
import java.io.File

fun findClosestValue(values: List<Double>, target: Double): Double {
    return values.minByOrNull { Math.abs(it - target) } ?: values[0]
}
fun getYawList(file: File): List<Float> {
    val content = file.readText()
    val regex = Regex("""\[([^,\]]+),""")
    return regex.findAll(content)
        .mapNotNull { match ->
            match.groupValues[1]
                .trim()
                .toFloatOrNull()
        }
        .toList()
}
fun outputYaw(yaw: Float):Float {
    val yawList = getYawList(File(storageRotationDir,"rotation-data.txt"))
    val normalized = MathHelper.wrapAngleTo180_float(yaw)
    val closestResult = findClosestPoint(normalized, yawList)
    return yaw + closestResult.second
}
fun get(yaw: Float):Float {
    val yawList = getYawList((File(storageRotationDir,"rotation-data.txt")))
    val normalized = MathHelper.wrapAngleTo180_float(yaw)
    val closestResult = findClosestPoint(normalized, yawList)
    return yaw + closestResult.second
}
fun findClosestPoint(target: Float, list: List<Float>): Pair<Float, Float> {
    val closest = list.minByOrNull { Math.abs(it - target) }!!
    var difference = target - closest
    if (difference > 180f) difference -= 360f
    if (difference < -180f) difference += 360f
    return Pair(closest, difference)
}

fun findNextLargerPoint(target: Float, list: List<Float>):Pair<Float, Float>? {
    var c = list.filter { it > target && it != target }.minByOrNull { kotlin.math.abs(it - target) } ?: return null
    var d = c.minus(target) ?: return null
    return try {
        Pair(c,d)
    } catch (e: Exception) {
        null
    }
}
fun findNextSmallerPoint(target: Float, list: List<Float>):Pair<Float, Float>? {
    var c = list.filter { it < target && it != target }.minByOrNull { kotlin.math.abs(it - target) } ?: return null
    var d = c.minus(target) ?: return null
    return try {
        Pair(c,d)
    } catch (e: Exception) {
        null
    }
}