package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.extensions.getDistanceToEntityBox
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue


@ModuleInfo(name = "SnapTrack", category = ModuleCategory.COMBAT)
class SnapTrack : Module() {
    private val maxRange = FloatValue("MaxRange",5f,0f,10f)
    private val attackRange = FloatValue("AttackRange",3f,0f,10f)
    private val predictSize = FloatValue("PredictSize", 1f, 1f, 5f)
    private val posXZOffset = FloatValue("PosXZOffset", 0.1f, -1f, 1f)
    private val posYOffset = FloatValue("PosYOffset", 0.0f, -1f, 1f)
    private val ignoreSmoothing = BoolValue("IgnoreSmoothing", false)
    private val onlySwing = BoolValue("OnlySwing", false)
    private val positionProcessingMode = ListValue("PositionProcessingMode", arrayOf("Pos", "ServerPos","MiddlePos","MixPos","CustomPosOffset"), "MiddlePos")
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
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
                    val targetVecX = target.posX + (target.posX - target.prevPosX) * predictSize.get()
                    val targetVecY = target.posY + (target.posY - target.prevPosY) * predictSize.get()
                    val targetVecZ = target.posZ + (target.posZ - target.prevPosZ) * predictSize.get()
                    if (player.getDistance(targetVecX, targetVecY, targetVecZ) <= attackRange.get()) {
                      if (!onlySwing.get() || mc.thePlayer.isSwingInProgress)  target.setPositionAndUpdate(targetVecX, targetVecY, targetVecZ)
                    } else {
                        val posX = target.posX
                        val posY = target.posY
                        val posZ = target.posZ
                        val sPosX = target.serverPosX.toDouble() / 32
                        val sPosY = target.serverPosY.toDouble() / 32
                        val sPosZ = target.serverPosZ.toDouble() / 32
                        when(positionProcessingMode.get()){
                            "ServerPos" -> target.setPositionAndUpdate(sPosX, sPosY, sPosZ)
                            "MiddlePos" -> {
                                val middlePosX = (posX + sPosX) / 2
                                val middlePosY = (posY + sPosY) / 2
                                val middlePosZ = (posZ + sPosZ) / 2
                                target.setPositionAndUpdate(middlePosX, middlePosY, middlePosZ)
                            }
                            "MixPos"->{
                                val mixPosX = posX + (sPosX - posX)
                                val mixPosY = posY + (sPosY - posY)
                                val mixPosZ = posZ + (sPosZ - posZ)
                                target.setPositionAndUpdate(mixPosX, mixPosY, mixPosZ)
                            }
                            "CustomPosOffset"->{
                                val middlePosX = (posX + sPosX) / 2
                                val middlePosY = (posY + sPosY) / 2
                                val middlePosZ = (posZ + sPosZ) / 2
                                target.setPositionAndUpdate(
                                    middlePosX+posXZOffset.get(),
                                    middlePosY+posYOffset.get(),
                                    middlePosZ+posXZOffset.get()
                                )
                                //Pos直接跳过
                            }
                        }
                    }
                }
            }
        }
    }

}