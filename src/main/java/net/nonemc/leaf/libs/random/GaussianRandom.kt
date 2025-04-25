package net.nonemc.leaf.libs.random

import kotlin.math.*
import kotlin.random.Random

private var hasNextGaussian = false
private var nextGaussian = 0.0

fun nextGaussian(mean: Double = 0.0, sigma: Double = 1.0): Double {
    if (hasNextGaussian) {
        hasNextGaussian = false
        return nextGaussian * sigma + mean
    }
    var u1: Double
    var u2: Double
    var s: Double

    do {
        u1 = 1.0 - Random.nextDouble()
        u2 = Random.nextDouble()
        u1 = u1 * 2 - 1
        u2 = u2 * 2 - 1
        s = u1 * u1 + u2 * u2
    } while (s >= 1.0 || s == 0.0)

    val rad = sqrt(-2.0 * ln(s) / s)
    nextGaussian = u2 * rad
    hasNextGaussian = true

    return (u1 * rad) * sigma + mean
}
