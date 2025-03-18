package net.nonemc.leaf.features.module.modules.combat.aura.utils

import kotlin.random.Random

fun randomDouble(from:Double,until:Double):Double{
    return if (from >= until) from else Random.nextDouble(from,until)
}
fun randomInt(from:Int,until:Int):Int{
    return if (from >= until) from else Random.nextInt(from,until)
}
fun randomLong(from:Long,until:Long):Long{
    return if (from >= until) from else Random.nextLong(from,until)
}