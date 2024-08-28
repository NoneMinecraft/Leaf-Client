/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.settings.KeyBinding
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "BalanceTimer", category = ModuleCategory.MOVEMENT)
class BalanceTimer : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Intave13"), "Intave13")
    private val MaxTimer = FloatValue("MaxTimer", 1.4f, 1f, 10f)
    private val LowTimer = FloatValue("LowTimer", 0.9f, 0.01f, 1.1f)
    private val LowTicks = FloatValue("LowTicks", 10f, 1f, 20f)
    private val ticks = FloatValue("Ticks", 100f, 1f, 200f)
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
        if (modeValue.get()=="Intave13"){
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
    override val tag: String
        get() = modeValue.get()
}