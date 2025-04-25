package net.nonemc.leaf.features.module.modules.combat.aura.invoke

import net.minecraft.entity.Entity
import net.nonemc.leaf.libs.random.PerlinNoise
import net.nonemc.leaf.libs.raycast.Raycast
import net.nonemc.leaf.libs.base.mc
import kotlin.random.Random

fun probability(probability: Int): Boolean {
    if (probability !in 0..100) return true
    return Random.nextInt(0, 100) < probability
}

fun isPlayerMoving(): Boolean {
    return net.nonemc.leaf.libs.base.mc.thePlayer != null && (net.nonemc.leaf.libs.base.mc.thePlayer.movementInput.moveForward != 0f || net.nonemc.leaf.libs.base.mc.thePlayer.movementInput.moveStrafe != 0f)
}

fun perlinNoise(x: Double, y: Double, z: Double, seed: Int): Double {
    val perlin = PerlinNoise(seed)
    return perlin.noise(x, y, z)
}

fun hitable(targetEntity: Entity, blockReachDistance: Double): Boolean {
    return Raycast.raycastEntity(
        blockReachDistance
    ) { entity: Entity -> entity === targetEntity } != null
}

fun isMove(it: Entity): Boolean {
    return (it.posX - it.prevPosX) != 0.0 || (it.posY - it.prevPosY) != 0.0 || (it.posZ - it.prevPosZ) != 0.0
}