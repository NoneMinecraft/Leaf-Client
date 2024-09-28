package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.passive.EntityCow

@ModuleInfo(name = "NoDeadBody", category = ModuleCategory.COMBAT)
class NoDeadBody : Module() {
    //此模块为了防止尸体(牛)遮挡
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val world = mc.theWorld ?: return

        val entityList = world.loadedEntityList

        val entitiesToRemove = mutableListOf<Entity>()

        for (entity in entityList) {
            if (entity is EntityCow) {
                entitiesToRemove.add(entity)
            }
        }

        for (entity in entitiesToRemove) {
            world.removeEntity(entity)
        }
    }
}