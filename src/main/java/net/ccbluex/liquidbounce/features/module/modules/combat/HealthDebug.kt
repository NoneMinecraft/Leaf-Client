package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.entity.player.EntityPlayer

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
        if (hungerLevel > 18){
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