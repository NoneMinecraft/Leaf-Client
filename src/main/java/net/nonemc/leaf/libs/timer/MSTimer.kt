package net.nonemc.leaf.libs.timer

class MSTimer {
    var time = -1L
    fun hasTimePassed(MS: Long): Boolean {
        return System.currentTimeMillis() >= time + MS
    }
    fun reset() {
        time = System.currentTimeMillis()
    }
}