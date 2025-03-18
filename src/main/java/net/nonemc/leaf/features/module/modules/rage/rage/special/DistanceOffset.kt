package net.nonemc.leaf.features.module.modules.rage.rage.special

fun distanceOffset(range: Double,multiplier:Double , maxRange:Double): Double {
    return if (range <= maxRange) (range / 15) * multiplier else 0.0
}