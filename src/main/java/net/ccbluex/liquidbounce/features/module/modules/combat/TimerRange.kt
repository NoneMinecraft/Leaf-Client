/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

@ModuleInfo(name = "TimerRange", category = ModuleCategory.COMBAT)
object TimerRange : Module() {
    private val maxRange = IntegerValue("MaxRange", 3, 1, 10)
    private val minRange = IntegerValue("MinRange", 1, 1, 10)
    private val invalidDistance = IntegerValue("InvalidDistance", 3, 1, 30)
    private val timing = ListValue("Timing", arrayOf("All","Pre","Post"),"All")
    private val Low = FloatValue("LowTimer", 0.1F, 0.05F, 1F)
    private val LowTimerTicks = IntegerValue("LowTimerTicks", 1, 1, 20)
    private val Max = FloatValue("MaxTimer", 100F, 1F, 1000F)
    private var prev = 0.0F
    private var lowtick = 0
    var invalid = false
    var invalid2 = false
    var isres = false

    override fun onDisable() {
        isres = false
        invalid = false
        invalid2 = false
        mc.timer.timerSpeed = 1.0f
        prev = 0.0F
        lowtick = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
      when (timing.get()){
          "All" -> prev = 0.0F
          "Pre" -> prev = 5.0F
          "Post" -> prev = -1.0F
      }

        val  targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != mc.thePlayer && EntityUtils.isSelected(it, true) }
            .firstOrNull { canSeePlayer(mc.thePlayer, it) }
        targetPlayer?.let {
            if (it.getDistanceToEntityBox(mc.thePlayer) <= maxRange.get()) {

                if (lowtick < LowTimerTicks.get()) {
                    if (it.getDistanceToEntityBox(mc.thePlayer) > minRange.get() && !invalid) {
                        mc.timer.timerSpeed = Low.get()
                        lowtick++
                    }
                } else {
                    if (it.getDistanceToEntityBox(mc.thePlayer) <= minRange.get() && !invalid) {
                        mc.timer.timerSpeed = 1F
                        lowtick = 0
                    } else {
                        mc.timer.timerSpeed = Max.get()
                        invalid2 = true
                    }
                }
            }else{
                mc.timer.timerSpeed = 1F
                lowtick = 0
            }
            if (it.getDistanceToEntityBox(mc.thePlayer) <= invalidDistance.get() && !invalid2) {
                invalid = true
            } else {
                invalid = false
            }
        }
        if (targetPlayer == null){

            mc.timer.timerSpeed = 1F
            lowtick = 0
        }
    }
    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
        val targetVec = Vec3(target.posX+(target.posX - target.prevPosX) * prev,
            (target.posY + target.eyeHeight*0.8)+target.posY-target.prevPosY,
            target.posZ+(target.posZ-target.prevPosZ)*prev)
        val result = world.rayTraceBlocks(playerVec, targetVec, false, true, false)
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
}
