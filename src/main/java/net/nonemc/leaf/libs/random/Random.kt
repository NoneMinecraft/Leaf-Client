package net.nonemc.leaf.libs.random

import kotlin.random.Random

fun randomDouble(from: Double, until: Double): Double {
    return if (from >= until) from else Random.nextDouble(from, until)
}
fun randomFloat(from: Float, until: Float): Float {
    return if (from >= until) from else Random.nextDouble(from.toDouble(), until.toDouble()).toFloat()
}
fun randomInt(from: Int, until: Int): Int {
    return if (from >= until) from else Random.nextInt(from, until)
}
fun randomLong(from: Long, until: Long): Long {
    return if (from >= until) from else Random.nextLong(from, until)
}
fun randomString(length: Int): String {
    return randomString(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
}
fun randomNumber(length: Int): String {
    return randomString(length, "123456789")
}
fun random(length: Int, chars: String): String {
    return random(length, chars.toCharArray())
}
fun random(length: Int, chars: CharArray): String {
    val stringBuilder = java.lang.StringBuilder()
    for (i in 0 until length) stringBuilder.append(chars[java.util.Random().nextInt(chars.size)])
    return stringBuilder.toString()
}

private fun randomString(length: Int, chars: String): String {
    return randomString(length, chars.toCharArray())
}
private fun randomString(length: Int, chars: CharArray): String {
    val stringBuilder = StringBuilder()
    for (i in 0 until length) stringBuilder.append(chars[Random.nextInt(chars.size)])
    return stringBuilder.toString()
}