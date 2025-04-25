package net.nonemc.leaf.libs.system

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Time {
    private var lastTime: String? = null
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    fun hasTimeChanged(): Boolean {
        val currentTime = LocalTime.now().format(formatter)
        if (currentTime != lastTime) {
            lastTime = currentTime
            return true
        }
        return false
    }
}