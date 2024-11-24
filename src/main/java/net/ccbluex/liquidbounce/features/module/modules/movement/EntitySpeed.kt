package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.util.AxisAlignedBB
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "EntitySpeed", category = ModuleCategory.MOVEMENT)
class EntitySpeed : Module() {

    @EventTarget
    fun onTick(event: TickEvent) {
        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return

        var collisions = 0
        val playerBox = player.entityBoundingBox.expand(1.0, 1.0, 1.0)

        for (entity in world.loadedEntityList) {
            if (entity != player && entity is EntityLivingBase && entity !is EntityArmorStand) {
                val entityBox = entity.entityBoundingBox
                if (playerBox.intersectsWith(entityBox)) {
                    collisions++
                }
            }
        }

        val yaw = Math.toRadians(player.rotationYaw.toDouble())
        val boost = 0.08 * collisions
        player.motionX += -sin(yaw) * boost
        player.motionZ += cos(yaw) * boost
    }
}
