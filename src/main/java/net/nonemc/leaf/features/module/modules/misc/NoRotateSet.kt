package net.nonemc.leaf.features.module.modules.misc

import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.value.BoolValue

@ModuleInfo(name = "NoRotateSet", category = ModuleCategory.MISC)
class NoRotateSet : Module() {

    private val noLoadingValue = BoolValue("NoLoading", true)
    private val confirmValue = BoolValue("Confirm", false)
    private val overwriteTeleportValue = BoolValue("OverwriteTeleport", false)
    private val illegalRotationValue = BoolValue("ConfirmIllegalRotation", false)
    private val noZeroValue = BoolValue("NoZero", false)

    private var lastRotation: Rotation? = null

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S08PacketPlayerPosLook) {
            if ((noZeroValue.get() && packet.getYaw() == 0F && packet.getPitch() == 0F) ||
                (noLoadingValue.get() && mc.netHandler?.doneLoadingTerrain == false)
            ) {
                return
            }

            if (illegalRotationValue.get() || packet.getPitch() <= 90 && packet.getPitch() >= -90 &&
                RotationBaseLib.serverRotation != null && packet.getYaw() != RotationBaseLib.serverRotation.yaw &&
                packet.getPitch() != RotationBaseLib.serverRotation.pitch
            ) {

                if (confirmValue.get()) {
                    mc.netHandler.addToSendQueue(
                        C05PacketPlayerLook(
                            packet.getYaw(),
                            packet.getPitch(),
                            mc.thePlayer.onGround
                        )
                    )
                }
            }

            if (!overwriteTeleportValue.get()) {
                lastRotation = Rotation(packet.getYaw(), packet.getPitch())
            }
            packet.yaw = mc.thePlayer.rotationYaw
            packet.pitch = mc.thePlayer.rotationPitch
        } else if (lastRotation != null && packet is C03PacketPlayer && packet.rotating) {
            packet.yaw = lastRotation!!.yaw
            packet.pitch = lastRotation!!.pitch
            lastRotation = null
        }
    }
}