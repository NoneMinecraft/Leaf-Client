/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.minecraft.client.settings.KeyBinding

@ModuleInfo(name = "BalanceTimer", category = ModuleCategory.MOVEMENT)
class BalanceTimer : Module() {
    private val MaxTimer = FloatValue("MaxTimer", 1.4f, 1f, 10f)
    private val LowTimer = FloatValue("LowTimer", 0.9f, 0.01f, 1.1f)
    private val LowTicks = FloatValue("LowTicks", 10f, 1f, 20f)
    private val ticks = FloatValue("MaxTicks", 100f, 1f, 200f)
    private val AutoJump = BoolValue("AutoJump", false)
    private var tickCounter = 0
    private var phase = 1
    var mat = 0
    var rat = 0
    private var isEnabled = false
    override fun onEnable() {
        mat = 0
        rat = 0
        phase = 1
        isEnabled = true
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tickCounter++
        when (phase) {
            0 -> {
                mc.timer.timerSpeed = MaxTimer.get()
                if (tickCounter >= ticks.get()) {
                    tickCounter = 0
                    phase = 1
                    if (AutoJump.get()) {
                        Jump()
                    }
                }
            }
            1 -> {
                mc.timer.timerSpeed = LowTimer.get()
                if (tickCounter >= LowTicks.get()) {
                    tickCounter = 0
                    phase = 0

                }
            }
        }
    }

    private fun Jump() {
        val jumpKey = mc.gameSettings.keyBindJump
        KeyBinding.setKeyBindState(jumpKey.keyCode, true)
        KeyBinding.onTick(jumpKey.keyCode)
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        mat = 0
        resetJumpKey()
        rat = 0
        isEnabled = false
    }

    private fun resetJumpKey() {
        val jumpKey = mc.gameSettings.keyBindJump
        KeyBinding.setKeyBindState(jumpKey.keyCode, false)
    }
}