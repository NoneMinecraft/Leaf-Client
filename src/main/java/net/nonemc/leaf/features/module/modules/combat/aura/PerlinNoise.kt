package net.nonemc.leaf.features.module.modules.combat.aura

import kotlin.math.floor
import kotlin.random.Random

class PerlinNoise(private val seed: Int) {
    private val p: IntArray = IntArray(512)
    init {
        val permutation = Array(256) { it }
        permutation.shuffle(Random(seed))
        for (i in 0 until 256) {
            p[i] = permutation[i]
            p[i + 256] = permutation[i]
        }
    }
    private fun fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)
    private fun lerp(t: Double, a: Double, b: Double): Double = a + t * (b - a)
    private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
        val h = hash and 15
        val u = if (h < 8) x else y
        val v = if (h < 4) y else if (h == 12 || h == 14) x else z
        return ((if (h and 1 == 0) u else -u) + if (h and 2 == 0) v else -v)
    }
    fun noise(x: Double, y: Double, z: Double): Double {
        val X = floor(x).toInt() and 255
        val Y = floor(y).toInt() and 255
        val Z = floor(z).toInt() and 255
        val xf = x - floor(x)
        val yf = y - floor(y)
        val zf = z - floor(z)
        val u = fade(xf)
        val v = fade(yf)
        val w = fade(zf)
        val aaa = p[X + p[Y + p[Z]]]
        val aba = p[X + p[Y + p[Z + 1]]]
        val aab = p[X + p[Y + p[Z + 1]]]
        val abb = p[X + p[Y + p[Z]]]
        val x1 = lerp(u, grad(aaa, xf, yf, zf), grad(aba, xf - 1, yf, zf))
        val x2 = lerp(u, grad(aab, xf, yf - 1, zf), grad(abb, xf - 1, yf - 1, zf))
        val y1 = lerp(v, x1, x2)
        val y2 = lerp(v, lerp(u, grad(aaa, xf, yf, zf), grad(aba, xf - 1, yf, zf)),
            lerp(u, grad(aab, xf, yf - 1, zf), grad(abb, xf - 1, yf - 1, zf)))
        return lerp(w, y1, y2)
    }
}