package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition

@ModuleInfo(name = "MoreKB", category = ModuleCategory.COMBAT)
class MoreKB : Module() {

    private val mode = ListValue("Modes", arrayOf( "Packet", "DoublePacket", "MorePacket", "W-Tap"), "Legit")
    private val packets = FloatValue("Packets", 5.0f, 3.0f, 10.0f)
    private val intelligent = BoolValue("Intelligent", false)
    private var ticks = 0

    override fun onEnable() {
        super.onEnable()
        resetAll()
    }

    override fun onDisable() {
        super.onDisable()
        resetAll()
    }

    private fun resetAll() {
        ticks = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode.get().equals("W-Tap", ignoreCase = true)) {
            if (ticks == 2) {
                mc.gameSettings.keyBindForward.pressed = false
                ticks = 1
            } else if (ticks == 1) {
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                ticks = 0
            }
        }

        val entity: EntityLivingBase? = if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
            && mc.objectMouseOver.entityHit is EntityLivingBase) {
            mc.objectMouseOver.entityHit as EntityLivingBase
        } else {
            null
        }

        entity?.let {
            val x = mc.thePlayer.posX - it.posX
            val z = mc.thePlayer.posZ - it.posZ
            val calcYaw = MathHelper.atan2(z, x) * 180.0 / Math.PI - 90.0
            val diffYaw = MathHelper.wrapAngleTo180_float(calcYaw.toFloat() - it.rotationYawHead)
            if (!intelligent.get() || diffYaw <= 120.0f) {
                when (mode.get()) {
                    "MorePacket" -> if (it.hurtTime == 10) {
                        repeat(packets.get().toInt()) {
                            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                        }
                        mc.thePlayer.isSprinting = true
                    }
                    "Packet" -> if (it.hurtTime == 10) {
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                        mc.thePlayer.isSprinting = true
                    }
                    "DoublePacket" -> if (it.hurtTime == 10) {
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                        mc.thePlayer.isSprinting = true
                    }
                }
            }
        }
    }

    @EventTarget
    fun onAttackEntity(event: PacketEvent) {
        val entity = event.packet
        if (entity is EntityLivingBase) {
            ticks = 2
        }
    }
}
