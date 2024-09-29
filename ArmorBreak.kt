/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import com.sun.jdi.BooleanValue
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.MainLib.FindItems
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword

@ModuleInfo(name = "ArmorBreak", category = ModuleCategory.COMBAT)
class ArmorBreak : Module() {
    private val mode = ListValue ("Mode", arrayOf("Current"),"Current")
    var type = 0
    var tick = 0
    var attack = false
    override fun onDisable() {
        type = 0
        tick = 0
        attack = false
    }
    @EventTarget
    fun AttackEvent(event: AttackEvent) {
        attack = true
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (attack) {

            when (type) {
                0 -> {
                    mc.gameSettings.keyBindJump.pressed = false
                                        for (i in 0..8) { // 检查玩家快捷栏中的每个槽位
                        val itemStack: ItemStack? = mc.thePlayer.inventory.getStackInSlot(i)

                        if (itemStack == null &&mode.get() == "Current") { // 如果发现空槽位
                            mc.thePlayer.inventory.currentItem = i // 切换主手到空槽位
                            type++
                            break
                        }
                    }
                }
                1 -> {

                    for (i in 0..8) {
                        val itemStack: ItemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue

                        if (itemStack.item == Items.wooden_sword&&mode.get() == "Current") {
                            mc.thePlayer.inventory.currentItem = i
                            type++
                            break
                        }
                    }

                }

                2 -> {

                    for (i in 0..8) {
                        val itemStack: ItemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue

                        if (itemStack.item == Items.stone_sword&&mode.get() == "Current") {
                            mc.thePlayer.inventory.currentItem = i
                            type++

                            break
                        }
                    }

                }

                3 -> {

                    for (i in 0..8) {
                        val itemStack: ItemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue

                        if (itemStack.item == Items.iron_sword&&mode.get() == "Current") {
                            mc.thePlayer.inventory.currentItem = i
                            type++

                            break
                        }
                    }

                }

                4 -> {

                    for (i in 0..8) {
                        val itemStack: ItemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue

                        if (itemStack.item == Items.diamond_sword&&mode.get() == "Current") {
                            mc.thePlayer.inventory.currentItem = i
                            type = 0
                            attack = false
                            mc.gameSettings.keyBindJump.pressed = true
                            break

                        }
                    }

                }

            }
        }

    }
}
