package net.nonemc.leaf.features.module.modules.player

import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.potion.Potion
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.injection.access.IItemStack
import net.nonemc.leaf.libs.item.InventoryItem
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.libs.item.ItemEnchantment.enchantment
import net.nonemc.leaf.libs.random.randomInt
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.libs.timer.TimeUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.stream.Collectors
import java.util.stream.IntStream

@ModuleInfo(name = "InvManager", category = ModuleCategory.PLAYER)
class InvManager : Module() {
    class ArmorPiece(val itemStack: ItemStack, val slot: Int) {
        val armorType = (itemStack.item as ItemArmor).armorType
    }
    private fun getArmorDamageReduction(defensePoints: Int, toughness: Int): Float {
        return 1 - 20.0f.coerceAtMost((defensePoints / 5.0f).coerceAtLeast(defensePoints - 1 / (2 + toughness / 4.0f))) / 25.0f
    }

    private fun getArmorThresholdedEnchantmentDamageReduction(itemStack: ItemStack): Float {
        var sum = 0.0f
        for (i in armorDamageReduceEnchantments.indices) {
            sum += enchantment(
                itemStack,
                armorDamageReduceEnchantments[i].enchantment
            ) * armorDamageReduceEnchantments[i].factor
        }
        return sum
    }
    enum class EnumNBTPriorityType {
        HAS_NAME,
        HAS_LORE,
        HAS_DISPLAY_TAG,
        NONE
    }
    private fun getArmorEnchantmentThreshold(itemStack: ItemStack): Float {
        var sum = 0.0f
        for (i in otherArmorEnchantments.indices) {
            sum += enchantment(itemStack, otherArmorEnchantments[i].enchantment) * otherArmorEnchantments[i].factor
        }
        return sum
    }
    private fun getEnchantmentCount(itemStack: ItemStack): Int {
        if (itemStack.enchantmentTagList == null || itemStack.enchantmentTagList.hasNoTags()) return 0
        var c = 0
        for (i in 0 until itemStack.enchantmentTagList.tagCount()) {
            val tagCompound = itemStack.enchantmentTagList.getCompoundTagAt(i)
            if (tagCompound.hasKey("ench") || tagCompound.hasKey("id")) {
                c++
            }
        }
        return c
    }
    private val armorDamageReduceEnchantments = arrayOf(
        Enchant(Enchantment.protection, 0.06f),
        Enchant(Enchantment.projectileProtection, 0.032f),
        Enchant(Enchantment.fireProtection, 0.0585f),
        Enchant(Enchantment.blastProtection, 0.0304f)
    )
    private val otherArmorEnchantments = arrayOf(
        Enchant(Enchantment.featherFalling, 3.0f),
        Enchant(Enchantment.thorns, 1.0f),
        Enchant(Enchantment.respiration, 0.1f),
        Enchant(Enchantment.aquaAffinity, 0.05f),
        Enchant(Enchantment.unbreaking, 0.01f)
    )

    class Enchant(val enchantment: Enchantment, val factor: Float)
    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }
    fun hasNBTGoal(stack: ItemStack, goal: EnumNBTPriorityType): Boolean {
        if (stack.hasTagCompound() && stack.tagCompound.hasKey("display", 10)) {
            val display = stack.tagCompound.getCompoundTag("display")

            if (goal == EnumNBTPriorityType.HAS_DISPLAY_TAG) {
                return true
            } else if (goal == EnumNBTPriorityType.HAS_NAME) {
                return display.hasKey("Name")
            } else if (goal == EnumNBTPriorityType.HAS_LORE) {
                return display.hasKey("Lore") && display.getTagList("Lore", 8).tagCount() > 0
            }
        }

        return false
    }
    private fun compareArmor(
        o1: ArmorPiece,
        o2: ArmorPiece,
        nbtedPriority: Float = 0f,
        goal: EnumNBTPriorityType = EnumNBTPriorityType.NONE,
    ): Int {
        val compare = round(
            getArmorThresholdedDamageReduction(o2.itemStack).toDouble() - if (hasNBTGoal(
                    o2.itemStack,
                    goal
                )
            ) {
                nbtedPriority / 5f
            } else {
                0f
            }, 3
        )
            .compareTo(
                round(
                    getArmorThresholdedDamageReduction(o1.itemStack).toDouble() - if (hasNBTGoal(
                            o1.itemStack,
                            goal
                        )
                    ) {
                        nbtedPriority / 5f
                    } else {
                        0f
                    }, 3
                )
            )

        if (compare == 0) {
            val otherEnchantmentCmp = round(getArmorEnchantmentThreshold(o1.itemStack).toDouble(), 3)
                .compareTo(round(getArmorEnchantmentThreshold(o2.itemStack).toDouble(), 3))
            if (otherEnchantmentCmp == 0) {
                val enchantmentCountCmp = getEnchantmentCount(o1.itemStack)
                    .compareTo(getEnchantmentCount(o2.itemStack))
                if (enchantmentCountCmp != 0) {
                    return enchantmentCountCmp
                }
                val o1a = o1.itemStack.item as ItemArmor
                val o2a = o2.itemStack.item as ItemArmor
                val durabilityCmp = o1a.armorMaterial.getDurability(o1a.armorType)
                    .compareTo(o2a.armorMaterial.getDurability(o2a.armorType))

                return if (durabilityCmp != 0) {
                    durabilityCmp
                } else {
                    // last compare: enchantability...
                    o1a.armorMaterial.enchantability.compareTo(o2a.armorMaterial.enchantability)
                }
            }
            return otherEnchantmentCmp
        }
        return compare
    }
    private fun getArmorThresholdedDamageReduction(itemStack: ItemStack): Float {
        val item = itemStack.item as ItemArmor
        return getArmorDamageReduction(
            item.armorMaterial.getDamageReductionAmount(item.armorType),
            0
        ) * (1 - getArmorThresholdedEnchantmentDamageReduction(itemStack))
    }
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 600, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minDelayValue.get()
            if (minCPS > newValue) set(minCPS)
        }
    }

    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 400, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelay = maxDelayValue.get()
            if (maxDelay < newValue) set(maxDelay)
        }
    }

    private val invOpenValue = BoolValue("InvOpen", false)
    private val simulateInventory = BoolValue("SimulateInventory", true)
    private val simulateDelayValue =
        IntegerValue("SimulateInventoryDelay", 0, 0, 1000).displayable { simulateInventory.get() }
    private val noMoveValue = BoolValue("NoMove", false)
    private val hotbarValue = BoolValue("Hotbar", true)
    private val randomSlotValue = BoolValue("RandomSlot", false)
    private val sortValue = BoolValue("Sort", true)
    private val throwValue = BoolValue("ThrowGarbage", true)
    private val armorValue = BoolValue("Armor", true)
    private val noCombatValue = BoolValue("NoCombat", false)
    private val itemDelayValue = IntegerValue("ItemDelay", 0, 0, 5000)
    private val onlySwordDamage = BoolValue("OnlySwordWeapon", true)
    private val nbtGoalValue =
        ListValue("NBTGoal", EnumNBTPriorityType.values().map { it.toString() }.toTypedArray(), "NONE")
    private val nbtItemNotGarbage = BoolValue("NBTItemNotGarbage", true).displayable { !nbtGoalValue.equals("NONE") }
    private val nbtArmorPriority =
        FloatValue("NBTArmorPriority", 0f, 0f, 5f).displayable { !nbtGoalValue.equals("NONE") }
    private val nbtWeaponPriority =
        FloatValue("NBTWeaponPriority", 0f, 0f, 5f).displayable { !nbtGoalValue.equals("NONE") }
    private val ignoreVehiclesValue = BoolValue("IgnoreVehicles", false)
    private val onlyPositivePotionValue = BoolValue("OnlyPositivePotion", false)
    private val items = arrayOf(
        "None",
        "Ignore",
        "Sword",
        "Bow",
        "Pickaxe",
        "Axe",
        "Food",
        "Block",
        "Water",
        "Gapple",
        "Pearl",
        "Potion"
    )
    private val sortSlot1Value = ListValue("SortSlot-1", items, "Sword").displayable { sortValue.get() }
    private val sortSlot2Value = ListValue("SortSlot-2", items, "Gapple").displayable { sortValue.get() }
    private val sortSlot3Value = ListValue("SortSlot-3", items, "Potion").displayable { sortValue.get() }
    private val sortSlot4Value = ListValue("SortSlot-4", items, "Pickaxe").displayable { sortValue.get() }
    private val sortSlot5Value = ListValue("SortSlot-5", items, "Axe").displayable { sortValue.get() }
    private val sortSlot6Value = ListValue("SortSlot-6", items, "None").displayable { sortValue.get() }
    private val sortSlot7Value = ListValue("SortSlot-7", items, "Block").displayable { sortValue.get() }
    private val sortSlot8Value = ListValue("SortSlot-8", items, "Pearl").displayable { sortValue.get() }
    private val sortSlot9Value = ListValue("SortSlot-9", items, "Food").displayable { sortValue.get() }

    private val openInventory: Boolean
        get() = mc.currentScreen !is GuiInventory && simulateInventory.get()
    private var invOpened = false
        set(value) {
            if (value != field) {
                if (value) {
                    openPacket()
                } else {
                    closePacket()
                }
            }
            field = value
        }

    private val goal: EnumNBTPriorityType
        get() = EnumNBTPriorityType.valueOf(nbtGoalValue.get())

    private var delay = 0L
    private val simDelayTimer = MSTimer()

    override fun onDisable() {
        invOpened = false
    }

    private fun checkOpen(): Boolean {
        if (!invOpened && openInventory) {
            invOpened = true
            simDelayTimer.reset()
            return true
        }
        return !simDelayTimer.hasTimePassed(simulateDelayValue.get().toLong())
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noMoveValue.get() && EntityMoveLib.isMoving() ||
            mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0 ||
            (Leaf.combatManager.inCombat && noCombatValue.get())
        ) {
            if (InventoryItem.CLICK_TIMER.hasTimePassed(simulateDelayValue.get().toLong())) {
                invOpened = false
            }
            return
        }

        if (!InventoryItem.CLICK_TIMER.hasTimePassed(delay) || (mc.currentScreen !is GuiInventory && invOpenValue.get())) {
            return
        }

        if (armorValue.get()) {
            // Find best armor
            val bestArmor = findBestArmor()

            // Swap armor
            for (i in 0..3) {
                val armorPiece = bestArmor[i] ?: continue
                val armorSlot = 3 - i
                val oldArmor: ItemStack? = mc.thePlayer.inventory.armorItemInSlot(armorSlot)
                if (oldArmor == null || oldArmor.item !is ItemArmor || compareArmor(
                        ArmorPiece(oldArmor, -1),
                        armorPiece,
                        nbtArmorPriority.get(),
                        goal
                    ) < 0
                ) {
                    if (oldArmor != null && move(8 - armorSlot, true)) {
                        return
                    }
                    if (mc.thePlayer.inventory.armorItemInSlot(armorSlot) == null && move(armorPiece.slot, false)) {
                        return
                    }
                }
            }
        }

        if (sortValue.get()) {
            for (index in 0..8) {
                val bestItem = findBetterItem(index, mc.thePlayer.inventory.getStackInSlot(index)) ?: continue

                if (bestItem != index) {
                    if (checkOpen()) {
                        return
                    }

                    mc.playerController.windowClick(
                        0,
                        if (bestItem < 9) bestItem + 36 else bestItem,
                        index,
                        2,
                        mc.thePlayer
                    )

                    delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
                    return
                }
            }
        }

        if (throwValue.get()) {
            val garbageItems = items(5, if (hotbarValue.get()) 45 else 36)
                .filter { !isUseful(it.value, it.key) }
                .keys

            val garbageItem = if (garbageItems.isNotEmpty()) {
                if (randomSlotValue.get()) {
                    // pick random one
                    garbageItems.toList()[randomInt(0, garbageItems.size)]
                } else {
                    garbageItems.first()
                }
            } else {
                null
            }
            if (garbageItem != null) {
                // Drop all useless items
                if (checkOpen()) {
                    return
                }

                mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, garbageItem, 4, 4, mc.thePlayer)

                delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

                return
            }
        }

        if (InventoryItem.CLICK_TIMER.hasTimePassed(simulateDelayValue.get().toLong())) {
            invOpened = false
        }
    }

    private fun openPacket() {
        mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    private fun closePacket() {
        mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
    }

    fun isUseful(itemStack: ItemStack, slot: Int): Boolean {
        return try {
            val item = itemStack.item

            if (item is ItemSword || (item is ItemTool && !onlySwordDamage.get())) {

                val damage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                    ?: 0.0) + getWeaponEnchantFactor(itemStack, nbtWeaponPriority.get(), goal)

                items(0, 45).none { (_, stack) ->
                    if (stack != itemStack && stack.javaClass == itemStack.javaClass) {
                        val dmg = (stack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                            ?: 0.0) + getWeaponEnchantFactor(stack, nbtWeaponPriority.get(), goal)
                        if (damage == dmg) {
                            val currDamage = item.getDamage(itemStack)
                            currDamage >= stack.item.getDamage(stack)
                        } else damage < dmg
                    } else {
                        false
                    }
                }
            } else if (item is ItemBow) {
                val currPower = enchantment(itemStack, Enchantment.power)
                items().none { (_, stack) ->
                    if (itemStack != stack && stack.item is ItemBow) {
                        val power = enchantment(stack, Enchantment.power)

                        if (currPower == power) {
                            val currDamage = item.getDamage(itemStack)
                            currDamage >= stack.item.getDamage(stack)
                        } else currPower < power
                    } else {
                        false
                    }
                }
            } else if (item is ItemArmor) {
                val currArmor = ArmorPiece(itemStack, slot)
                items().none { (slot, stack) ->
                    if (stack != itemStack && stack.item is ItemArmor) {
                        val armor = ArmorPiece(stack, slot)

                        if (armor.armorType != currArmor.armorType) {
                            false
                        } else {
                            val currDamage = item.getDamage(itemStack)
                            val result = compareArmor(currArmor, armor, nbtArmorPriority.get(), goal)
                            if (result == 0)
                                currDamage >= stack.item.getDamage(stack)
                            else result < 0
                        }
                    } else {
                        false
                    }
                }
            } else if (item is ItemFlintAndSteel) {
                val currDamage = item.getDamage(itemStack)
                items().none { (_, stack) ->
                    itemStack != stack && stack.item is ItemFlintAndSteel && currDamage >= stack.item.getDamage(stack)
                }
            } else if (itemStack.unlocalizedName == "item.compass") {
                items(0, 45).none { (_, stack) -> itemStack != stack && stack.unlocalizedName == "item.compass" }
            } else {
                (nbtItemNotGarbage.get() && hasNBTGoal(itemStack, goal)) ||
                        item is ItemFood || itemStack.unlocalizedName == "item.arrow" ||
                        (item is ItemBlock && !InventoryItem.isBlockListBlock(item)) ||
                        item is ItemBed || (item is ItemPotion && (!onlyPositivePotionValue.get() || isPositivePotion(
                    item,
                    itemStack
                ))) ||
                        item is ItemEnderPearl || item is ItemBucket || ignoreVehiclesValue.get() && (item is ItemBoat || item is ItemMinecart)
            }
        } catch (ex: Exception) {
            println("(InvManager) Failed to check item: ${itemStack.unlocalizedName}.")
            true
        }
    }


    private fun isPositivePotionEffect(id: Int): Boolean {
        return id == Potion.regeneration.id || id == Potion.moveSpeed.id ||
                id == Potion.heal.id || id == Potion.nightVision.id ||
                id == Potion.jump.id || id == Potion.invisibility.id ||
                id == Potion.resistance.id || id == Potion.waterBreathing.id ||
                id == Potion.absorption.id || id == Potion.digSpeed.id ||
                id == Potion.damageBoost.id || id == Potion.healthBoost.id ||
                id == Potion.fireResistance.id
    }

    fun isPositivePotion(item: ItemPotion, stack: ItemStack): Boolean {
        item.getEffects(stack).forEach {
            if (isPositivePotionEffect(it.potionID)) {
                return true
            }
        }

        return false
    }

    private fun findBestArmor(): Array<ArmorPiece?> {
        val armorPieces = IntStream.range(0, 36)
            .filter { i: Int ->
                val itemStack = mc.thePlayer.inventory.getStackInSlot(i)
                (itemStack != null && itemStack.item is ItemArmor &&
                        (i < 9 || System.currentTimeMillis() - (itemStack as IItemStack).itemDelay >= itemDelayValue.get()))
            }
            .mapToObj { i: Int -> ArmorPiece(mc.thePlayer.inventory.getStackInSlot(i), i) }
            .collect(Collectors.groupingBy { obj: ArmorPiece -> obj.armorType })

        val bestArmor = arrayOfNulls<ArmorPiece>(4)
        for ((key, value) in armorPieces) {
            bestArmor[key!!] = value.also {
                it.sortWith { armorPiece, armorPiece2 ->
                    compareArmor(
                        armorPiece,
                        armorPiece2,
                        nbtArmorPriority.get(),
                        goal
                    )
                }
            }.lastOrNull()
        }

        return bestArmor
    }
    fun getWeaponEnchantFactor(
        stack: ItemStack,
        nbtedPriority: Float = 0f,
        goal: EnumNBTPriorityType = EnumNBTPriorityType.NONE,
    ): Double {
        return (1.25 * enchantment(stack, Enchantment.sharpness)) +
                (1.0 * enchantment(stack, Enchantment.fireAspect)) +
                if (hasNBTGoal(stack, goal)) {
                    nbtedPriority
                } else {
                    0f
                }
    }
    private fun findBetterItem(targetSlot: Int, slotStack: ItemStack?): Int? {
        val type = type(targetSlot)

        when (type.lowercase()) {
            "sword", "pickaxe", "axe" -> {
                val currentType: Class<out Item> = when {
                    type.equals("Sword", ignoreCase = true) -> ItemSword::class.java
                    type.equals("Pickaxe", ignoreCase = true) -> ItemPickaxe::class.java
                    type.equals("Axe", ignoreCase = true) -> ItemAxe::class.java
                    else -> return null
                }

                var bestWeapon = if (slotStack?.item?.javaClass == currentType) {
                    targetSlot
                } else {
                    -1
                }

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack?.item?.javaClass == currentType && !type(index).equals(
                            type,
                            ignoreCase = true
                        ) && (!onlySwordDamage.get() || type.equals("Sword", ignoreCase = true))
                    ) {
                        if (bestWeapon == -1) {
                            bestWeapon = index
                        } else {
                            val currDamage = (itemStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                ?: 0.0) + getWeaponEnchantFactor(itemStack, nbtWeaponPriority.get(), goal)

                            val bestStack = mc.thePlayer.inventory.getStackInSlot(bestWeapon) ?: return@forEachIndexed
                            val bestDamage = (bestStack.attributeModifiers["generic.attackDamage"].firstOrNull()?.amount
                                ?: 0.0) + getWeaponEnchantFactor(bestStack, nbtWeaponPriority.get(), goal)

                            if (bestDamage < currDamage) {
                                bestWeapon = index
                            }
                        }
                    }
                }

                return if (bestWeapon != -1 || bestWeapon == targetSlot) bestWeapon else null
            }

            "bow" -> {
                var bestBow = if (slotStack?.item is ItemBow) targetSlot else -1
                var bestPower = if (bestBow != -1) {
                    enchantment(slotStack!!, Enchantment.power)
                } else {
                    0
                }

                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, itemStack ->
                    if (itemStack?.item is ItemBow && !type(index).equals(type, ignoreCase = true)) {
                        if (bestBow == -1) {
                            bestBow = index
                        } else {
                            val power = enchantment(itemStack, Enchantment.power)

                            if (enchantment(itemStack, Enchantment.power) > bestPower) {
                                bestBow = index
                                bestPower = power
                            }
                        }
                    }
                }

                return if (bestBow != -1) bestBow else null
            }

            "food" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemFood && item !is ItemAppleGold && !type(index).equals("Food", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemFood

                        return if (replaceCurr) index else null
                    }
                }
            }

            "block" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBlock && !InventoryItem.BLOCK_BLACKLIST.contains(item.block) &&
                        !type(index).equals("Block", ignoreCase = true)
                    ) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemBlock

                        return if (replaceCurr) index else null
                    }
                }
            }

            "water" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemBucket && item.isFull == Blocks.flowing_water && !type(index).equals(
                            "Water",
                            ignoreCase = true
                        )
                    ) {
                        val replaceCurr =
                            slotStack == null || slotStack.item !is ItemBucket || (slotStack.item as ItemBucket).isFull != Blocks.flowing_water

                        return if (replaceCurr) index else null
                    }
                }
            }

            "gapple" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemAppleGold && !type(index).equals("Gapple", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemAppleGold

                        return if (replaceCurr) index else null
                    }
                }
            }

            "pearl" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if (item is ItemEnderPearl && !type(index).equals("Pearl", ignoreCase = true)) {
                        val replaceCurr = slotStack == null || slotStack.item !is ItemEnderPearl

                        return if (replaceCurr) index else null
                    }
                }
            }

            "potion" -> {
                mc.thePlayer.inventory.mainInventory.forEachIndexed { index, stack ->
                    val item = stack?.item

                    if ((item is ItemPotion && ItemPotion.isSplash(stack.itemDamage)) &&
                        !type(index).equals("Potion", ignoreCase = true)
                    ) {
                        val replaceCurr =
                            slotStack == null || slotStack.item !is ItemPotion || !ItemPotion.isSplash(slotStack.itemDamage)

                        return if (replaceCurr) index else null
                    }
                }
            }
        }

        return null
    }
    private fun items(start: Int = 0, end: Int = 45): Map<Int, ItemStack> {
        val items = mutableMapOf<Int, ItemStack>()

        for (i in end - 1 downTo start) {
            val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
            itemStack.item ?: continue

            if (i in 36..44 && type(i).equals("Ignore", ignoreCase = true)) {
                continue
            }

            if (System.currentTimeMillis() - (itemStack as IItemStack).itemDelay >= itemDelayValue.get()) {
                items[i] = itemStack
            }
        }

        return items
    }
    private fun move(item: Int, isArmorSlot: Boolean): Boolean {
        if (item == -1) {
            return false
        } else if (!isArmorSlot && item < 9 && hotbarValue.get() && mc.currentScreen !is GuiInventory) {
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(item))
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(item).stack))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            return true
        } else {
            if (checkOpen()) {
                return true // make sure to return
            }
            if (throwValue.get() && isArmorSlot) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, item, 0, 4, mc.thePlayer)
            } else {
                mc.playerController.windowClick(
                    mc.thePlayer.inventoryContainer.windowId,
                    if (isArmorSlot) item else if (item < 9) item + 36 else item,
                    0,
                    1,
                    mc.thePlayer
                )
            }
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            return true
        }
    }
    private fun type(targetSlot: Int) = when (targetSlot) {
        0 -> sortSlot1Value.get()
        1 -> sortSlot2Value.get()
        2 -> sortSlot3Value.get()
        3 -> sortSlot4Value.get()
        4 -> sortSlot5Value.get()
        5 -> sortSlot6Value.get()
        6 -> sortSlot7Value.get()
        7 -> sortSlot8Value.get()
        8 -> sortSlot9Value.get()
        else -> ""
    }
}
