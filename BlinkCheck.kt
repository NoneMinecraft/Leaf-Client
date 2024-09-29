/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.math.Vec4
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos

@ModuleInfo(name = "BlinkCheck", category = ModuleCategory.COMBAT)
class BlinkCheck : Module() {
    private val xValue = FloatValue("VelocityX",0.0F,0.0F,1.0F)
    private val yValue = FloatValue("VelocityY",0.0F,0.0F,1.0F)
    private val zValue = FloatValue("VelocityZ",0.0F,0.0F,1.0F)
    private var vlTick = 0

    override fun onDisable() {
        vlTick = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != mc.thePlayer && ((it.posX - it.prevPosX) == xValue.get().toDouble() && (it.posY - it.prevPosY) == yValue.get().toDouble() && (it.posZ - it.prevPosZ == zValue.get().toDouble())) && !it.onGround}
            .firstOrNull { true }
        targetPlayer?.let {
            vlTick ++
            ChatPrint("§bVAC §f${it.name} §ffailed §bBlink §f(vl:$vlTick)")
        }
    }
}
