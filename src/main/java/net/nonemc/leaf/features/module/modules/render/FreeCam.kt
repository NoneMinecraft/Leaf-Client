
package net.nonemc.leaf.features.module.modules.render

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.MovementUtils
import net.nonemc.leaf.utils.PacketUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "FreeCam", category = ModuleCategory.RENDER, autoDisable = EnumAutoDisableType.RESPAWN)
class FreeCam : Module() {
    private val speedValue = FloatValue("Speed", 0.8f, 0.1f, 2f)
    private val flyValue = BoolValue("Fly", true)
    private val noClipValue = BoolValue("NoClip", true)
    private val motionValue = BoolValue("RecordMotion", true)
    private val c03SpoofValue = BoolValue("C03Spoof", false)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var packetCount = 0

    override fun onEnable() {
        if (mc.thePlayer == null) return

        if (motionValue.get()) {
            motionX = mc.thePlayer.motionX
            motionY = mc.thePlayer.motionY
            motionZ = mc.thePlayer.motionZ
        } else {
            motionX = 0.0
            motionY = 0.0
            motionZ = 0.0
        }

        packetCount = 0
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        mc.theWorld.addEntityToWorld(-(Math.random() * 10000).toInt(), fakePlayer)
        if (noClipValue.get()) mc.thePlayer.noClip = true
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return
        mc.thePlayer.setPositionAndRotation(fakePlayer!!.posX, fakePlayer!!.posY, fakePlayer!!.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null
        mc.thePlayer.motionX = motionX
        mc.thePlayer.motionY = motionY
        mc.thePlayer.motionZ = motionZ
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noClipValue.get()) mc.thePlayer.noClip = true
        mc.thePlayer.fallDistance = 0f
        if (flyValue.get()) {
            val value = speedValue.get()
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += value.toDouble()
            if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= value.toDouble()
            MovementUtils.strafe(value)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (c03SpoofValue.get()) {
            if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C05PacketPlayerLook || packet is C03PacketPlayer.C06PacketPlayerPosLook) {
                if (packetCount >= 20) {
                    packetCount = 0
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(fakePlayer!!.posX, fakePlayer!!.posY, fakePlayer!!.posZ, fakePlayer!!.rotationYaw, fakePlayer!!.rotationPitch, fakePlayer!!.onGround))
                } else {
                    packetCount++
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer(fakePlayer!!.onGround))
                }
                event.cancelEvent()
            }
        } else if (packet is C03PacketPlayer) {
            event.cancelEvent()
        }

        if (packet is S08PacketPlayerPosLook) {
            fakePlayer!!.setPosition(packet.x, packet.y, packet.z)
            // when teleport,motion reset
            motionX = 0.0
            motionY = 0.0
            motionZ = 0.0
            // apply the flag to bypass some anticheat
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(fakePlayer!!.posX, fakePlayer!!.posY, fakePlayer!!.posZ, fakePlayer!!.rotationYaw, fakePlayer!!.rotationPitch, fakePlayer!!.onGround))

            event.cancelEvent()
        }
    }
}