package net.nonemc.leaf.features.module.modules.combat.aura.utils

fun findClosestValue(values: List<Double>, target: Double): Double {
    return values.minByOrNull { Math.abs(it - target) } ?: values[0]
}