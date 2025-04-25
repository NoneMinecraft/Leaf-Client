package net.nonemc.leaf.libs.item

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack

object ItemEnchantment {
    fun enchantment(itemStack: ItemStack, enchantment: Enchantment): Int {
        if (itemStack.enchantmentTagList == null || itemStack.enchantmentTagList.hasNoTags()) return 0
        for (i in 0 until itemStack.enchantmentTagList.tagCount()) {
            val tagCompound = itemStack.enchantmentTagList.getCompoundTagAt(i)
            if (tagCompound.hasKey("ench") && tagCompound.getShort("ench").toInt() == enchantment.effectId ||
                tagCompound.hasKey("id") && tagCompound.getShort("id").toInt() == enchantment.effectId
            ) {
                return tagCompound.getShort("lvl").toInt()
            }
        }
        return 0
    }
}