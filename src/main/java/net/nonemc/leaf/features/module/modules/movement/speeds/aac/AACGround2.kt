﻿package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.value.FloatValue

class AACGround2 : SpeedMode("AACGround2") {
    private val timerValue = FloatValue("${valuePrefix}Timer", 3f, 1.1f, 10f)

    override fun onUpdate() {
        if (!MovementUtils.isMoving()) return

        mc.timer.timerSpeed = timerValue.get()

        MovementUtils.strafe(0.02f)
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}