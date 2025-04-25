package net.nonemc.leaf.font

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.TickEvent

object FontsGC : Listenable {
    private val fontRenderers = mutableListOf<GameFontRenderer>()

    private var gcTicks = 0
    const val GC_TICKS = 200
    const val CACHED_FONT_REMOVAL_TIME = 30000

    @EventTarget
    fun onTick(event: TickEvent) {
        if (gcTicks++ > GC_TICKS) {
            fontRenderers.forEach { it.collectGarbage() }
            gcTicks = 0
        }
    }

    fun register(fontRender: GameFontRenderer) {
        fontRenderers.add(fontRender)
    }

    fun closeAll() {
        fontRenderers.clear()
    }

    override fun handleEvents() = true
}