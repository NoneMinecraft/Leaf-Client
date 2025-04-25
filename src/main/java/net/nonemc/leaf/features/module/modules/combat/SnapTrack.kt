package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S14PacketEntity
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.extensions.getDistanceToEntityBox
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue


@ModuleInfo(name = "SnapTrack", category = ModuleCategory.COMBAT)
class SnapTrack : Module() {
    private val delay = IntegerValue("Delay", 200, 1, 2000)
    private val speed = FloatValue("Speed", 0.6f, 0.01f, 1f)
    private val maxRange = FloatValue("MaxRange", 5f, 0f, 10f)
    private val minRange = FloatValue("MinRange", 2.8f, 0f, 10f)
    private val attackRange = FloatValue("AttackRange", 3f, 0f, 10f)
    private val predictSize = FloatValue("PredictSize", 1f, 1f, 5f)
    private val onlySwing = BoolValue("OnlySwing", false)
    private val s14 = BoolValue("S14", true)
    private val cancelC03 = BoolValue("CancelC03", false)
    private val cancelC02 = BoolValue("CancelC02", true)
    private var canTP = false
    private var canCancel = false
    private val tpTimer = MSTimer()
    private var targetPlayer: EntityPlayer? = null
    var targetVecX = 0.0
    var targetVecY = 0.0
    var targetVecZ = 0.0
    override fun onDisable() {
        canTP = false
        canCancel = false
        tpTimer.reset()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (targetPlayer != null && canTP) {
            if (tpTimer.hasTimePassed(delay.get().toLong())) {
                canTP = false
                canCancel = false
                tpTimer.reset()
            } else {
                canCancel = true
                targetPlayer!!.setPosition(
                    targetPlayer!!.posX + (targetVecX - targetPlayer!!.posX) * speed.get(),
                    targetPlayer!!.posY + (targetVecY - targetPlayer!!.posY) * speed.get(),
                    targetPlayer!!.posZ + (targetVecZ - targetPlayer!!.posZ) * speed.get()
                )
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return
        val packet = event.packet
        if (packet is S14PacketEntity) {
            if (canCancel && packet.getEntity(world)!! == targetPlayer!! && s14.get()) event.cancelEvent()
        }
        for (target in world.playerEntities) {
            if (target !== player) {
                val distance = player.getDistanceToEntityBox(target)
                if (distance in minRange.get()..maxRange.get()) {
                    targetVecX = target.posX + (target.posX - target.prevPosX) * predictSize.get()
                    targetVecY = target.posY + (target.posY - target.prevPosY) * predictSize.get()
                    targetVecZ = target.posZ + (target.posZ - target.prevPosZ) * predictSize.get()
                    if (player.getDistance(targetVecX, targetVecY, targetVecZ) <= attackRange.get()) {
                        if (!onlySwing.get() || player.isSwingInProgress) {
                            if (packet is C03PacketPlayer && cancelC03.get()) {
                                event.cancelEvent()
                            }
                            if (packet is C02PacketUseEntity && cancelC02.get()) {
                                event.cancelEvent()
                            }
                            tpTimer.reset()
                            targetPlayer = target

                            canCancel = true
                            canTP = true
                        }
                    }
                }
            }
        }
    }
}
