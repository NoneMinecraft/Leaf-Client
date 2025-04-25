package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

import net.nonemc.leaf.value.FloatValue

@ModuleInfo(name = "BalanceTimer", category = ModuleCategory.MOVEMENT)
class BalanceTimer : Module() {
    private val highTicks = FloatValue("HighTicks", 100f, 1f, 200f)
    private val highTimer = FloatValue("HighTimer", 1.4f, 1f, 10f)
    private val lowTicks = FloatValue("LowTicks", 10f, 1f, 20f)
    private val lowTimer = FloatValue("LowTimer", 0.9f, 0.01f, 1.1f)

    private var tickCounter = 0
    private var phase = 1

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tickCounter++
        when (phase) {
            0 -> {
                mc.timer.timerSpeed = highTimer.get()
                if (tickCounter >= highTicks.get()) {
                    tickCounter = 0
                    phase = 1
                }
            }
            1 -> {
                mc.timer.timerSpeed = lowTimer.get()
                if (tickCounter >= lowTicks.get()) {
                    tickCounter = 0
                    phase = 0
                }
            }
        }
    }

    override fun onEnable() {
        phase = 1
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
    }
}