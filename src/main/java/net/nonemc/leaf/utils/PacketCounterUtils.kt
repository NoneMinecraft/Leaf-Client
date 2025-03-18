package net.nonemc.leaf.utils

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.TickEvent
import net.nonemc.leaf.utils.timer.MSTimer

object PacketCounterUtils : Listenable {

    init {
        Leaf.eventManager.registerListener(this)
    }

    private var inBound = 0
    private var outBound = 0
    var avgInBound = 0
    var avgOutBound = 0
    private val packetTimer = MSTimer()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.isServerSide()) {
            inBound++
        } else {
            outBound++
        }
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound
            avgOutBound = outBound
            outBound = 0
            inBound = 0
            packetTimer.reset()
        }
    }

    override fun handleEvents() = true
}