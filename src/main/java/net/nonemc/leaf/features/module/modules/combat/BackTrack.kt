//All the code was written by N0ne.
package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S18PacketEntityTeleport
import net.minecraft.util.Vec3
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import java.util.concurrent.ConcurrentHashMap

@ModuleInfo(name = "BackTrack", category = ModuleCategory.COMBAT)
class BackTrack : Module() {
    private val delay = IntegerValue("Delay", 100, 1, 1000)
    private val sleepDelay = IntegerValue("SleepDelay", 50, 0, 100)
    private val maxClientDistance: FloatValue = FloatValue("MaxClientSideDistance", 3f, 0.0f, 6f)
    private val minClientDistance = FloatValue("MinClientSideDistance", 1.0f, 0.0f, 6f)
    private val maxServerDistance: FloatValue = FloatValue("MaxServerSideDistance", 3f, 0.0f, 6f)
    private val minServerDistance = FloatValue("MinServerSideDistance", 1.0f, 0.0f, 6f)
    private val ignoreSmoothing = BoolValue("IgnoreSmoothing", true)
    private val onlyPlayerMove = BoolValue("OnlyPlayerMove", false)
    private val playerMovementIncludesJumping = BoolValue("PlayerMovementIncludesJumping", false)
    private val onlyTargetMove = BoolValue("OnlyTargetMove", false)
    private val targetMovementIncludesJumping = BoolValue("TargetMovementIncludesJumping", false)
    private val minPlayerHurtTime = IntegerValue("MinPlayerHurtTime", 0, 0, 10)
    private val maxPlayerHurtTime = IntegerValue("MaxPlayerHurtTime", 10, 0, 10)
    private val minTargetHurtTime = IntegerValue("MinTargetHurtTime", 0, 0, 10)
    private val maxTargetHurtTime = IntegerValue("MaxTargetHurtTime", 10, 0, 10)
    private val predictTrack = BoolValue("PredictTrack", false)
    private val predictSize = FloatValue("PredictSize", 1f, 1f, 5f).displayable { predictTrack.get() }
    private val s14 = BoolValue("S14", false)
    private val lastPositions = ConcurrentHashMap<EntityPlayer, Vec3>()
    private val delayTimers = ConcurrentHashMap<EntityPlayer, MSTimer>()
    private val freeze = ConcurrentHashMap<EntityPlayer, Boolean>()
    private var keepPos = false
    private var targetPlayer: EntityPlayer? = null
    private var targetVelocityX = 0.0
    private var targetVelocityY = 0.0
    private var targetVelocityZ = 0.0
    private val sleepTimer = MSTimer()
    private var sleep = false
    override fun onDisable() {
        sleep = false
        keepPos = false
        delayTimers.clear()
        freeze.clear()
        lastPositions.clear()
        targetPlayer = null
    }

    var target: EntityPlayer? = null

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val player = mc.thePlayer ?: return
        target = event.targetEntity as EntityPlayer
        targetVelocityX = (target!!.posX - target!!.prevPosX) * predictSize.get()
        targetVelocityY = (target!!.posY - target!!.prevPosY) * predictSize.get()
        targetVelocityZ = (target!!.posZ - target!!.prevPosZ) * predictSize.get()
        val clientDistance = player.getDistanceToEntity(target)
        val serverDistance = player.getDistance(
            target!!.serverPosX.toDouble() / 32,
            target!!.serverPosY.toDouble() / 32, target!!.serverPosZ.toDouble() / 32
        )
        if (clientDistance in minClientDistance.get()..maxClientDistance.get()
            && serverDistance in minServerDistance.get()..maxServerDistance.get() && !freeze.getOrDefault(target, false)
        ) {
            lastPositions[target!!] = Vec3(target!!.posX, target!!.posY, target!!.posZ)
            if (!delayTimers.containsKey(target)) delayTimers[target!!] = MSTimer()
            delayTimers[target]?.reset()
            freeze[target!!] = true
            keepPos = true
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packets = event.packet
        if (packets is S14PacketEntity && s14.get() && packets.getEntity(mc.theWorld) == target && keepPos) event.cancelEvent()
        if (packets is S18PacketEntityTeleport && s14.get() && packets.entityId == target!!.entityId && keepPos) event.cancelEvent()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        val iterator = lastPositions.iterator()
        while (iterator.hasNext()) {
            val (target, lastPos) = iterator.next()
            val distance = player.getDistanceToEntity(target)
            val serverDistance = player.getDistance(
                target.serverPosX.toDouble() / 32,
                target.serverPosY.toDouble() / 32, target.serverPosZ.toDouble() / 32
            )
            if (distance !in minClientDistance.get()..maxClientDistance.get() && serverDistance !in minServerDistance.get()..maxServerDistance.get()) {
                freeze.remove(target)
                lastPositions.clear()
                keepPos = false
                continue
            }

            val timer = delayTimers[target]
            if (timer != null && timer.hasTimePassed(delay.get().toLong())) {
                sleepTimer.reset()
                sleep = true
                freeze.clear()
                lastPositions.clear()
                targetPlayer = mc.thePlayer
                keepPos = false
                continue
            } else {
                if (!sleep || sleepTimer.hasTimePassed(sleepDelay.get().toLong())) {
                    keepPos = true
                    if (allow(target)) if (predictTrack.get()) target.setPositionAndUpdate(
                        lastPos.xCoord + targetVelocityX,
                        lastPos.yCoord + targetVelocityY,
                        lastPos.zCoord + targetVelocityZ
                    ) else target.setPositionAndUpdate(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord)
                    targetPlayer = target
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (ignoreSmoothing.get()) runSetTargetPos()
    }

    private fun allow(it: EntityPlayer): Boolean {
        return (!onlyPlayerMove.get() || isPlayerMoving()) && (!onlyTargetMove.get() || isTargetMoving(it)) && targetInHurtTime(
            it
        ) && playerInHurtTime()
    }

    private fun targetInHurtTime(it: EntityPlayer): Boolean {
        return it.hurtTime in minTargetHurtTime.get()..maxTargetHurtTime.get()
    }

    private fun playerInHurtTime(): Boolean {
        return mc.thePlayer != null && mc.thePlayer.hurtTime in minPlayerHurtTime.get()..maxPlayerHurtTime.get()
    }

    private fun isPlayerMoving(): Boolean {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f || (!playerMovementIncludesJumping.get() || mc.thePlayer.movementInput.jump))
    }

    private fun isTargetMoving(it: EntityPlayer): Boolean {
        return it.posX - it.prevPosX != 0.0 || it.posZ - it.prevPosZ != 0.0 || (!targetMovementIncludesJumping.get() || it.posY - it.prevPosY != 0.0)
    }

    private fun runSetTargetPos() {
        val iterator = lastPositions.iterator()
        while (iterator.hasNext()) {
            val (target, lastPos) = iterator.next()
            if (keepPos && allow(target)) if (predictTrack.get()) target.setPositionAndUpdate(
                lastPos.xCoord + targetVelocityX,
                lastPos.yCoord + targetVelocityY,
                lastPos.zCoord + targetVelocityZ
            ) else target.setPositionAndUpdate(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord) else continue
        }
    }
}
