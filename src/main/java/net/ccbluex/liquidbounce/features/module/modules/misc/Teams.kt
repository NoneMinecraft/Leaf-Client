/*
 * Leaf Hacked Client
   code by None
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor

@ModuleInfo(name = "Teams", category = ModuleCategory.MISC)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", true)
    private val gommeSWValue = BoolValue("GommeSW", false)
    private val MCGO = BoolValue("MCGO", false)
    private val armorcolor = BoolValue("ArmorColor", false)
    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: EntityLivingBase): Boolean {
        mc.thePlayer ?: return false

        if (scoreboardValue.get() && mc.thePlayer.team != null && entity.team != null &&
                mc.thePlayer.team.isSameTeam(entity.team)) {
            return true
        }
        if (gommeSWValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T")) {
                if (targetName[1].isDigit() && clientName[1].isDigit()) {
                    return targetName[1] == clientName[1]
                }
            }
        }
        //code by none
        if (MCGO.get()) {
            val entityPlayer = entity as EntityPlayer
            val myArmor = mc.thePlayer.inventory.armorInventory[3]
            val entityArmor = entityPlayer.inventory.armorInventory[3]

            if (myArmor != null && entityArmor != null) {
                val myItemArmor = myArmor.item as ItemArmor
                val entityItemArmor = entityArmor.item as ItemArmor

                // 判断材质相同且材质不为皮革
                if (myItemArmor.armorMaterial == entityItemArmor.armorMaterial && myItemArmor.armorMaterial != ItemArmor.ArmorMaterial.LEATHER) {
                    return true
                }

                    if (myItemArmor.getColor(myArmor) == entityItemArmor.getColor(entityArmor)) {
                        return true
                }

                // 判断材质为皮革且颜色为蓝色 对方材质为铁甲
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && myItemArmor.getColor(myArmor) == 0x0000FF) {
                    if (entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON) {
                        return true
                    }
                }

                // 判断材质为铁甲且对方为皮革且颜色为蓝色
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON && entityItemArmor.getColor(entityArmor) == 0x0000FF) { // 蓝色
                    return true
                }

                // 判断材质为皮革且颜色为红色 对方材质为锁链甲
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && myItemArmor.getColor(myArmor) == 0xFF0000) {
                    if (entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN) {
                        return true
                    }
                }

                // 判断材质为锁链甲且对方为皮革且颜色为红色
                if (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN && entityItemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER && entityItemArmor.getColor(entityArmor) == 0xFF0000) { // 红色
                    return true
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
