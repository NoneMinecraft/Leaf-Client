//by None
package net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils

class TickUtils {
    private var tick = 0

    fun update() {
        tick++
    }

    fun reset() {
        tick = 0
    }

    fun ticks(ticks: Int): Boolean {
        return tick >= ticks
    }
}