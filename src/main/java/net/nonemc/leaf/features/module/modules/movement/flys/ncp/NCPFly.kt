﻿package net.nonemc.leaf.features.module.modules.movement.flys.ncp

import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.movement.flys.FlyMode
import net.nonemc.leaf.libs.entity.EntityMoveLib.strafe
import net.nonemc.leaf.value.FloatValue

class NCPFly : FlyMode("NCP") {
    private val motionValue = FloatValue("${valuePrefix}Motion", 0f, 0f, 1f)

    override fun onEnable() {
        if (!mc.thePlayer.onGround) {
            return
        }

        repeat(65) {
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.049,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY + 0.1,
                mc.thePlayer.posZ,
                true
            )
        )

        mc.thePlayer.motionX *= 0.1
        mc.thePlayer.motionZ *= 0.1
        mc.thePlayer.swingItem()
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.motionY = (-motionValue.get()).toDouble()

        if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.5
        strafe()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            packet.onGround = true
        }
    }
}