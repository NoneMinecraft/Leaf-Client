/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils2.PacketUtils.sendPacket
import net.ccbluex.liquidbounce.utils2.RotationUtils.currentRotation
import net.ccbluex.liquidbounce.utils2.extensions.rotation
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "FastBow", category = ModuleCategory.COMBAT)
object FastBow : Module() {
    private val packets = IntegerValue("Packets", 20, 3,20)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!thePlayer.isUsingItem)
            return

        val currentItem = thePlayer.inventory.getCurrentItem()

        if (currentItem != null && currentItem.item is ItemBow) {
            sendPacket(
                C08PacketPlayerBlockPlacement(
                    BlockPos.ORIGIN,
                    255,
                    mc.thePlayer.currentEquippedItem,
                    0F,
                    0F,
                    0F
                )
            )

            val (yaw, pitch) = currentRotation ?: thePlayer.rotation

            repeat(packets.get()) {
                sendPacket(C05PacketPlayerLook(yaw, pitch, true))
            }

            sendPacket(C07PacketPlayerDigging(RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            thePlayer.itemInUseCount = currentItem.maxItemUseDuration - 1
        }
    }
}