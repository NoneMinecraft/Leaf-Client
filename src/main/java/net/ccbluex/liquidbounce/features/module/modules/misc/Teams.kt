/*
 * Leaf Hacked Client
   code by None
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor

@ModuleInfo(name = "Teams", category = ModuleCategory.MISC)
class Teams : Module() {
    private val scoreboardValue = BoolValue("Scoreboard", false)
    private val colorValue = BoolValue("Color", true)
    private val swValue = BoolValue("SW", false)
    private val mcgo = BoolValue("MCGO", false)
    private val armorcolor = BoolValue("ArmorColor", false)

    fun isInYourTeam(entity: EntityLivingBase): Boolean {
        mc.thePlayer ?: return false
        if (scoreboardValue.get() && mc.thePlayer.team != null && entity.team != null &&
                mc.thePlayer.team.isSameTeam(entity.team)) {
            return true
        }
        if (swValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T")) {
                if (targetName[1].isDigit() && clientName[1].isDigit()) {
                    return targetName[1] == clientName[1]
                }
            }
        }

        if (mcgo.get()) {
            val entityPlayer = entity as EntityPlayer
            val myArmor = mc.thePlayer.inventory.armorInventory[2]
            val entityArmor = entityPlayer.inventory.armorInventory[2]

            if (myArmor != null && entityArmor != null) {
                val myItemArmor = myArmor.item as ItemArmor
                val entityItemArmor = entityArmor.item as ItemArmor
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN && entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && entityItemArmor.getColor(entityArmor) == 0xFF0000) {
                    return true
                }
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON && entityItemArmor.getColor(entityArmor) == 0x0000FF) {
                    return true
                }

                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON && entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON) {
                    return true
                }

                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN && entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN) {
                    return true
                }

                if (myItemArmor.getColor(myArmor) == entityItemArmor.getColor(entityArmor) && entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER) {
                        return true
                }
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && myItemArmor.getColor(myArmor) == 0x0000FF) {
                    if (entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON) {
                        return true
                    }
                }
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && myItemArmor.getColor(myArmor) == 0xFF0000) {
                    if (entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN) {
                        return true
                    }
                }


            }
        }

        if (armorcolor.get()) {
            val entityPlayer = entity as EntityPlayer
            if (mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
                val myHead = mc.thePlayer.inventory.armorInventory[3]
                val myItemArmor = myHead.item as ItemArmor

                val entityHead = entityPlayer.inventory.armorInventory[3]
                var entityItemArmor = myHead.item as ItemArmor

                if (myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead)) {
                    return true
                }
            }
        }
        if (colorValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }

}
