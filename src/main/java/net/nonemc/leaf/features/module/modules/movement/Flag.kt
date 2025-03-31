/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.event.WorldEvent
import net.nonemc.leaf.features.Util.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.timer.MSTimer

@ModuleInfo(name = "Flag", category = ModuleCategory.MOVEMENT)
class Flag : Module() {
    private val timer = MSTimer()
    private val reduceTimer = MSTimer()
    private var flagsTime = 0
    private var stuck = false
    var flag = 0
    private fun reset() {
        stuck = false
        flagsTime = 0
        timer.reset()
        reduceTimer.reset()
    }

    override fun onEnable() {
        reset()
        flag = 0
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook) {
            flagsTime++
            reduceTimer.reset()
            if (!stuck) {
                timer.reset()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (stuck) {

            if (timer.hasTimePassed(1500)) {
                stuck = false
                flagsTime = 0
                timer.reset()
                reduceTimer.reset()
            }
        } else {
            if (flagsTime > 0) {
                flag++
                timer.reset()
                reduceTimer.reset()
                flagsTime = 0
                stuck = true
                ChatPrint("§7[§bL§be§ba§bf§7] §6F§6l§6a§6g§6: $flag")
            }
            if (timer.hasTimePassed(1500) && reduceTimer.hasTimePassed(500) && flagsTime > 0) {
                flagsTime -= 1
                reduceTimer.reset()
            }
        }
    }
}