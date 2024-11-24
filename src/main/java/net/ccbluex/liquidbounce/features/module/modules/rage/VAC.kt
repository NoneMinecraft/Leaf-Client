/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.entity.player.EntityPlayer

@ModuleInfo(name = "VAC", category = ModuleCategory.Rage)
class VAC : Module() {
    private val velocityThreshold = FloatValue("Blink-VelocityThreshold",0.0F,0.0F,1.0F)
    private var vl = 0
    override fun onDisable() {
        vl = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != mc.thePlayer && ((it.posX - it.prevPosX) <= velocityThreshold.get().toDouble() && (it.posY - it.prevPosY) <= velocityThreshold.get().toDouble() && (it.posZ - it.prevPosZ <= velocityThreshold.get().toDouble())) && !it.onGround}
            .firstOrNull { true }
        targetPlayer?.let {
            vl ++
            ChatPrint("§bVAC §f${it.name} §ffailed §bBlink §f(vl:$vl)")
        }
    }
}