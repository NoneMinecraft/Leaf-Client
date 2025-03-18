/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.event.AttackEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.item.ItemUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C09PacketHeldItemChange

@ModuleInfo(name = "AutoWeapon", category = ModuleCategory.COMBAT)
class AutoWeapon : Module() {
    private val onlySwordValue = BoolValue("OnlySword", false)
    private val silentValue = BoolValue("SpoofItem", false)
    private val ticksValue = IntegerValue("SpoofTicks", 10, 1, 20)

    private var attackEnemy = false
    private var spoofedSlot = 0

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attackEnemy = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C02PacketUseEntity && event.packet.action == C02PacketUseEntity.Action.ATTACK &&
                attackEnemy) {
            attackEnemy = false
            val (slot, _) = (0..8)
                .map { Pair(it, mc.thePlayer.inventory.getStackInSlot(it)) }
                .filter { it.second != null && (it.second.item is ItemSword || (it.second.item is ItemTool && !onlySwordValue.get())) }
                .maxByOrNull {
                    (it.second.attributeModifiers["generic.attackDamage"].first()?.amount
                        ?: 0.0) + 1.25 * ItemUtils.getEnchantment(it.second, Enchantment.sharpness)
                } ?: return

            if (slot == mc.thePlayer.inventory.currentItem) { // If in hand no need to swap
                return
            }

            // Switch to best weapon
            if (silentValue.get()) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(slot))
                spoofedSlot = ticksValue.get()
            } else {
                mc.thePlayer.inventory.currentItem = slot
                mc.playerController.updateController()
            }

            mc.netHandler.addToSendQueue(event.packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
            spoofedSlot--
        }
    }
}
