package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "HealthDebug", category = ModuleCategory.COMBAT)
class HealthDebug : Module() {
    private var LastRegen = 0
    private var ticks = 0
    override fun onDisable() {
        LastRegen = 0
        ticks = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer
        val hungerLevel = player.foodStats.foodLevel
        val saturationLevel = player.foodStats.saturationLevel
        val currentHealth = player.health
        val maxHealth = player.maxHealth
        ticks = updateRegenTimer(hungerLevel, saturationLevel, currentHealth, maxHealth)
        if (hungerLevel > 18) {
            ChatPrint("§b[Leaf] RegenTicks: §7$ticks")
        }
    }

    private fun updateRegenTimer(hunger: Int, saturation: Float, currentHealth: Float, maxHealth: Float): Int {
        if (currentHealth < maxHealth) {
            if (hunger >= 18 && saturation > 0) {
                LastRegen++
                if (LastRegen >= 80) {
                    LastRegen = 0
                }
            } else if (hunger >= 20) {
                LastRegen++
                if (LastRegen >= 80) {
                    LastRegen = 0
                }
            } else {
                LastRegen++
            }
        } else {
            LastRegen = 0
        }
        return LastRegen
    }
}