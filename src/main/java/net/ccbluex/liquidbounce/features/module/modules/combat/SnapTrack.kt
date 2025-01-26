package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue


@ModuleInfo(name = "SnapTrack", category = ModuleCategory.COMBAT)
class SnapTrack : Module() {
    private val maxRange = FloatValue("MaxRange",5f,0f,10f)
    private val attackRange = FloatValue("AttackRange",3f,0f,10f)
    private val predictSize = FloatValue("PredictSize", 1f, 1f, 5f)
    private val serverPos = BoolValue("ServerPos", true)
    private val ignoreSmoothing = BoolValue("IgnoreSmoothing", true)
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        event.partialTicks
        if (ignoreSmoothing.get()) runSetTargetSPos()
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        runSetTargetSPos()
    }
    private fun runSetTargetSPos() {
        val player = mc.thePlayer?:return
        val world = mc.theWorld?:return
        for (target in world.playerEntities) {
            if (target !== mc.thePlayer) {
                val distance = player.getDistanceToEntityBox(target)
                if (distance <= maxRange.get()) {
                    val targetVecX = target.posX + (target.posX - target.prevPosX)*predictSize.get()
                    val targetVecY = target.posY + (target.posY - target.prevPosY)*predictSize.get()
                    val targetVecZ = target.posZ + (target.posZ - target.prevPosZ)*predictSize.get()
                    if (player.getDistance(targetVecX,targetVecY,targetVecZ) <= attackRange.get()){
                        target.setPositionAndUpdate(targetVecX,targetVecY,targetVecZ)
                    }else if (serverPos.get()){
                        target.setPositionAndUpdate(target.serverPosX.toDouble()/32
                            ,target.serverPosY.toDouble()/32
                            ,target.serverPosZ.toDouble()/32)
                    }
                }
            }
        }
    }

}