package net.ccbluex.liquidbounce.features.module.modules.combat.aura.utils

import net.ccbluex.liquidbounce.features.module.modules.combat.Aura
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

fun visibility(player: EntityPlayer, target: EntityPlayer): Pair<Boolean, Vec3> {
    val world = mc.theWorld
    val playerVec = Vec3(player.posX,player.posY + player.eyeHeight,player.posZ)
    val minX = target.entityBoundingBox.minX
    val minZ = target.entityBoundingBox.minZ
    val maxX = target.entityBoundingBox.maxX
    val maxZ = target.entityBoundingBox.maxZ
    val minY = target.entityBoundingBox.minY
    val maxY = target.entityBoundingBox.maxY
    val corners = if (Aura.visibilityDetectionEntityBoundingBox.get()) listOf(
        Vec3(target.posX,target.posY,target.posZ),
        Vec3(maxX,minY,maxZ),
        Vec3(minX,minY,minZ),
        Vec3(maxX,minY,minZ),
        Vec3(minX,minY,maxZ),
        Vec3(maxX,maxY,maxZ),
        Vec3(minX,maxY,minZ),
        Vec3(maxX,maxY,minZ),
        Vec3(minX,maxY,maxZ)
    ) else listOf(Vec3(target.posX,target.posY,target.posZ))
    for (corner in corners) {
        val result = world.rayTraceBlocks(playerVec, corner, false, false, false,)
        if (result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) return Pair(true, corner)
    }
    return Pair(false, Vec3(0.0,0.0,0.0))
}