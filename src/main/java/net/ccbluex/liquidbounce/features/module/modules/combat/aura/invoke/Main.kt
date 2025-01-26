package net.ccbluex.liquidbounce.features.module.modules.combat.aura.invoke

import net.ccbluex.liquidbounce.features.module.modules.combat.aura.PerlinNoise
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.RaycastUtils
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import kotlin.random.Random

 fun probability(probability: Int): Boolean {
    if (probability !in 0..100) return true
    return Random.nextInt(0, 100) < probability
}
 fun isPlayerMoving(): Boolean {
    return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f)
}
 fun perlinNoise(x: Double, y: Double, z: Double, seed: Int): Double {
    val perlin = PerlinNoise(seed)
    return perlin.noise(x, y, z)
}
 fun hitable(targetEntity: Entity, blockReachDistance: Double): Boolean {
    return RaycastUtils.raycastEntity(
        blockReachDistance
    ) { entity: Entity -> entity === targetEntity } != null
}
fun isMove(it: EntityPlayer):Boolean{
    return  (it.posX - it.prevPosX) != 0.0 || (it.posY - it.prevPosY) != 0.0 || (it.posZ - it.prevPosZ) != 0.0
}