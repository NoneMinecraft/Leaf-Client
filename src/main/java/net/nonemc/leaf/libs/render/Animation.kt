﻿package net.nonemc.leaf.libs.render

class Animation(
    val type: EaseUtils.EnumEasingType,
    private val order: EaseUtils.EnumEasingOrder,
    val from: Double,
    val to: Double,
    val duration: Long,
) {
    var state = EnumAnimationState.NOT_STARTED

    private var startTime = 0L

    fun start(): Animation {
        startTime = System.currentTimeMillis()
        state = EnumAnimationState.DURING
        return this
    }

    val value: Double
        get() = when (state) {
            EnumAnimationState.NOT_STARTED -> from
            EnumAnimationState.DURING -> {
                val percent = (System.currentTimeMillis() - startTime) / duration.toDouble()
                if (percent > 1) {
                    state = EnumAnimationState.STOPPED
                    to
                } else {
                    from + ((to - from) * EaseUtils.apply(type, order, percent))
                }
            }

            EnumAnimationState.STOPPED -> to
        }

    enum class EnumAnimationState {
        NOT_STARTED,
        DURING,
        STOPPED
    }
}