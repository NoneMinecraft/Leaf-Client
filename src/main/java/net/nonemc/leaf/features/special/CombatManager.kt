package net.nonemc.leaf.features.special

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.utils.entity.EntityUtils
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.utils.timer.MSTimer

class CombatManager : Listenable, MinecraftInstance() {
    private val lastAttackTimer = MSTimer()

    var inCombat = false
        private set
    var target: EntityLivingBase? = null
        private set
    val attackedEntityList = mutableListOf<EntityLivingBase>()
    val focusedPlayerList = mutableListOf<EntityPlayer>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        MovementUtils.updateBlocksPerSecond()
        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                Leaf.eventManager.callEvent(EntityKilledEvent(it))
                attackedEntityList.remove(it)
            }
        }

        inCombat = false

        if (!lastAttackTimer.hasTimePassed(1000)) {
            inCombat = true
            return
        }

        if (target != null) {
            if (mc.thePlayer.getDistanceToEntity(target) > 7 || !inCombat || target!!.isDead) {
                target = null
            } else {
                inCombat = true
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity

        if (target is EntityLivingBase && EntityUtils.isSelected(target, true)) {
            this.target = target
            if (!attackedEntityList.contains(target)) {
                attackedEntityList.add(target)
            }
        }
        lastAttackTimer.reset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        inCombat = false
        target = null
        attackedEntityList.clear()
        focusedPlayerList.clear()
    }

//    @EventTarget
//    fun onPacket(event: PacketEvent) {
//        val packet = event.packet
//        if(packet is S02PacketChat) {
//            val raw = packet.chatComponent.unformattedText
//            val found = hackerWords.filter { raw.contains(it, true) }
//            if(raw.contains(mc.session.username, true) && found.isNotEmpty()) {
//                LiquidBounce.hud.addNotification(Notification("Someone call you a hacker!", found.joinToString(", "), NotifyType.ERROR))
//            }
//        }
//    }

    fun getNearByEntity(radius: Float): EntityLivingBase? {
        return try {
            mc.theWorld.loadedEntityList
                .filter { mc.thePlayer.getDistanceToEntity(it) < radius && EntityUtils.isSelected(it, true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer) }[0] as EntityLivingBase?
        } catch (e: Exception) {
            null
        }
    }

    fun isFocusEntity(entity: EntityPlayer): Boolean {
        if (focusedPlayerList.isEmpty()) {
            return true // no need 2 focus
        }

        return focusedPlayerList.contains(entity)
    }

    override fun handleEvents() = true
}