package net.nonemc.leaf.utils.timer

class TickTimer {
    private var tick = 0

    fun update() {
        tick++
    }

    fun reset() {
        tick = 0
    }

    fun hasTimePassed(ticks: Int): Boolean {
        return tick >= ticks
    }
}