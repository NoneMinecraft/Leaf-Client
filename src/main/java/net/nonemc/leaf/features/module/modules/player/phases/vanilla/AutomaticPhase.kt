﻿package net.nonemc.leaf.features.module.modules.player.phases.vanilla

import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue

class AutomaticPhase : PhaseMode("Automatic") {
    private val timer = MSTimer()
    private val offSetValue = FloatValue("${valuePrefix}Offset", 4.0f, -8.0f, 8.0f)
    private val timerValue = IntegerValue("${valuePrefix}PhaseDelay", 1000, 500, 5000)
    private val freezeMotionValue = BoolValue("${valuePrefix}FreezeMotion", true)
    private var aClip = true
    override fun onEnable() {
        timer.reset()
        aClip = true
    }

    override fun onUpdate(event: UpdateEvent) {
        if (timer.hasTimePassed(timerValue.get().toLong())) {
            if (aClip) {
                aClip = false
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - offSetValue.get(), mc.thePlayer.posZ)
            }
        } else if (freezeMotionValue.get()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            if (!mc.netHandler.doneLoadingTerrain) {
                timer.reset()
                aClip = true
            }
        }
    }
}