package net.ccbluex.liquidbounce.features.module.modules.rage.rage.math

import net.ccbluex.liquidbounce.features.module.modules.rage.rage.data.Value

fun doubleToInt(start:Double, end:Double, step:Double): Value {
    return Value(start.toInt(),((end - start) / step).toInt())
}
fun doubleToInt(start:Double, result:Int, step:Double): Double {
    return start + result * step
}