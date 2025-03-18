/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.rage

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue
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
        val player = mc?.thePlayer ?: return
        val targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && ((it.posX - it.prevPosX) <= velocityThreshold.get().toDouble() && (it.posY - it.prevPosY) <= velocityThreshold.get().toDouble() && (it.posZ - it.prevPosZ <= velocityThreshold.get().toDouble())) && !it.onGround}
            .firstOrNull { true }
        targetPlayer?.let {
            vl ++
            ChatPrint("§bVAC §f${it.name} §ffailed §bBlink §f(vl:$vl)")
        }
    }
}