package net.nonemc.leaf.libs.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.client.Target.animalValue
import net.nonemc.leaf.features.module.modules.client.Target.deadValue
import net.nonemc.leaf.features.module.modules.client.Target.invisibleValue
import net.nonemc.leaf.features.module.modules.client.Target.mobValue
import net.nonemc.leaf.features.module.modules.client.Target.playerValue
import net.nonemc.leaf.features.module.modules.misc.AntiBot.isBot
import net.nonemc.leaf.features.module.modules.misc.Teams
import net.nonemc.leaf.file.friendsConfig
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.libs.render.ColorUtils.stripColor

object EntityTypeLib : MinecraftInstance() {
    fun isSelected(entity: Entity, canAttackCheck: Boolean): Boolean {
        if (entity is EntityLivingBase && (deadValue.get() || entity.isEntityAlive()) && entity !== mc.thePlayer) {
            if (invisibleValue.get() || !entity.isInvisible()) {
                if (playerValue.get() && entity is EntityPlayer) {
                    if (canAttackCheck) {
                        if (isBot(entity)) {
                            return false
                        }

                        if (isFriend(entity)) {
                            return false
                        }

                        if (entity.isSpectator) {
                            return false
                        }

                        if (entity.isPlayerSleeping) {
                            return false
                        }

                        if (!Leaf.combatManager.isFocusEntity(entity)) {
                            return false
                        }

                        val teams = Leaf.moduleManager.getModule(Teams::class.java)
                        return !teams!!.state || !teams.isInYourTeam(entity)
                    }

                    return true
                }
                return mobValue.get() && isMob(entity) || animalValue.get() && isAnimal(entity)
            }
        }
        return false
    }

    fun isFriend(entity: Entity): Boolean {
        return entity is EntityPlayer && entity.getName() != null && friendsConfig.isFriend(
            stripColor(
                entity.getName()
            )
        )
    }

    private fun isAnimal(entity: Entity): Boolean {
        return entity is EntityAnimal || entity is EntitySquid || entity is EntityGolem || entity is EntityVillager || entity is EntityBat
    }

    private fun isMob(entity: Entity): Boolean {
        return entity is EntityMob || entity is EntitySlime || entity is EntityGhast || entity is EntityDragon
    }
}