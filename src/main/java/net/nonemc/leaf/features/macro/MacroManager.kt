package net.nonemc.leaf.features.macro

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.KeyEvent
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.utils.MinecraftInstance

class MacroManager : Listenable, MinecraftInstance() {
    val macros = ArrayList<Macro>()

    @EventTarget
    fun onKey(event: KeyEvent) {
        macros.filter { it.key == event.key }.forEach { it.exec() }
    }

    override fun handleEvents() = true
}