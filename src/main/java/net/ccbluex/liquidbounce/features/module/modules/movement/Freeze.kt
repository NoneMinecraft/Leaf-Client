/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.EnumAutoDisableType
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "Freeze", category = ModuleCategory.MOVEMENT, autoDisable = EnumAutoDisableType.RESPAWN)
class Freeze : Module() {
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    override fun onEnable() {
        if (mc.thePlayer == null) return
        x = mc.thePlayer.posX
        y = mc.thePlayer.posY
        z = mc.thePlayer.posZ
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.setPositionAndRotation(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer) {
            event.cancelEvent()
        }
    }
}
