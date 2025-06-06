﻿/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.nonemc.leaf.event.BlockBBEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Spider", category = ModuleCategory.MOVEMENT)
class Spider : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Collide", "Motion", "AAC4"), "Collide")
    private val heightValue = IntegerValue("Height", 2, 0, 10)
    private val motionValue = FloatValue("Motion", 0.42F, 0.1F, 1F).displayable { modeValue.equals("Motion") }

    private var startHeight = 0.0
    private var groundHeight = 0.0
    private var modifyBB = false
    private var glitch = false
    private var wasTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (wasTimer) {
            mc.timer.timerSpeed = 1.0f
        }
        if (!mc.thePlayer.isCollidedHorizontally || !mc.gameSettings.keyBindForward.pressed || (mc.thePlayer.posY - heightValue.get() > startHeight && heightValue.get() > 0)) {
            if (mc.thePlayer.onGround) {
                startHeight = mc.thePlayer.posY
                groundHeight = mc.thePlayer.posY
            }
            modifyBB = false
            return
        }
        if (modeValue.get() == "AAC4" && (mc.thePlayer.motionY < 0.0 || mc.thePlayer.onGround)) {
            glitch = true
        }

        modifyBB = true

        when (modeValue.get().lowercase()) {
            "collide", "aac4" -> {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    groundHeight = mc.thePlayer.posY
                    if (modeValue.get() == "AAC4") {
                        wasTimer = true
                        mc.timer.timerSpeed = 0.4f
                    }
                }
            }

            "motion" -> {
                mc.thePlayer.motionY = motionValue.get().toDouble()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer && glitch) {
            glitch = false
            val yaw = EntityMoveLib.direction.toFloat()
            packet.x = packet.x - sin(yaw) * 0.00000001
            packet.z = packet.z + cos(yaw) * 0.00000001
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        wasTimer = false
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (!mc.thePlayer.isCollidedHorizontally || !mc.gameSettings.keyBindForward.pressed || (mc.thePlayer.posY - heightValue.get() > startHeight && heightValue.get() > 0)) {
            return
        }
        if (!modifyBB || mc.thePlayer.motionY > 0.0) return

        when (modeValue.get().lowercase()) {
            "collide", "aac4" -> {
                if (event.block is BlockAir && event.y <= mc.thePlayer.posY && event.y > groundHeight - 0.0625 && event.y < groundHeight + 0.0625) {
                    event.boundingBox = AxisAlignedBB.fromBounds(
                        event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                        event.x + 1.0, event.y + 1.0, event.z + 1.0
                    )
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()

}
