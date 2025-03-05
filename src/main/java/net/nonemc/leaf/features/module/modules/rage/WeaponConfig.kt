package net.nonemc.leaf.features.module.modules.rage

import net.minecraft.init.Items
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "WeaponConfig", category = ModuleCategory.Rage)
class WeaponConfig : Module() {
    private val weapons =
        ListValue("Weapons", arrayOf("AK47", "M4A1", "AWP", "Shotgun", "P250", "Deagle", "MP7"), "AK47")

    private val priorityAK =
        ListValue("Priority-AK", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "AK47" }
    private val pitchOffsetAK = FloatValue("PitchOffset-AK", 0.2F, -0.5F, 1F).displayable { weapons.get() == "AK47" }
    private val targetPredictSizeAK =
        FloatValue("TargetPredictSize-AK", 4.3F, 0F, 10F).displayable { weapons.get() == "AK47" }
    private val fireModeAK =
        ListValue("FireMode-AK", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "AK47" }
    private val fireTickAK = IntegerValue("FireTick-AK", 1, 0, 5).displayable { weapons.get() == "AK47" }
    private val noSpreadTickAK = IntegerValue("NoSpreadBaseTick-AK", 2, 0, 5).displayable { weapons.get() == "AK47" }
    private val silentRotateAK = BoolValue("SilentRotate-AK", true).displayable { weapons.get() == "AK47" }

    private val priorityM4A1 =
        ListValue("Priority-M4A1", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "M4A1" }
    private val pitchOffsetM4A1 =
        FloatValue("PitchOffset-M4A1", 0.2F, -0.5F, 5F).displayable { weapons.get() == "M4A1" }
    private val targetPredictSizeM4A1 =
        FloatValue("TargetPredictSize-M4A1", 4.3F, 0F, 10F).displayable { weapons.get() == "M4A1" }
    private val fireModeM4A1 =
        ListValue("FireMode-M4A1", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "M4A1" }
    private val fireTickM4A1 = IntegerValue("FireTick-M4A1", 1, 0, 5).displayable { weapons.get() == "M4A1" }
    private val noSpreadTickM4A1 =
        IntegerValue("NoSpreadBaseTick-M4A1", 2, 0, 5).displayable { weapons.get() == "M4A1" }
    private val silentRotateM4A1 = BoolValue("SilentRotate-M4A1", true).displayable { weapons.get() == "M4A1" }

    private val priorityAWP =
        ListValue("Priority-AWP", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "AWP" }
    private val pitchOffsetAWP = FloatValue("PitchOffset-AWP", 0.2F, -0.5F, 5F).displayable { weapons.get() == "AWP" }
    private val targetPredictSizeAWP =
        FloatValue("TargetPredictSize-AWP", 4.3F, 0F, 10F).displayable { weapons.get() == "AWP" }
    private val fireModeAWP =
        ListValue("FireMode-AWP", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "AWP" }
    private val fireTickAWP = IntegerValue("FireTick-AWP", 1, 0, 5).displayable { weapons.get() == "AWP" }
    private val noSpreadTickAWP = IntegerValue("NoSpreadBaseTick-AWP", 2, 0, 5).displayable { weapons.get() == "AWP" }
    private val silentRotateAWP = BoolValue("SilentRotate-AWP", true).displayable { weapons.get() == "AWP" }

    private val priorityShotgun = ListValue(
        "Priority-Shotgun",
        arrayOf("Head", "Chest", "Feet"),
        "Head"
    ).displayable { weapons.get() == "Shotgun" }
    private val pitchOffsetShotgun =
        FloatValue("PitchOffset-Shotgun", 0.2F, -0.5F, 5F).displayable { weapons.get() == "Shotgun" }
    private val targetPredictSizeShotgun =
        FloatValue("TargetPredictSize-Shotgun", 4.3F, 0F, 10F).displayable { weapons.get() == "Shotgun" }
    private val fireModeShotgun =
        ListValue("FireMode-Shotgun", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "Shotgun" }
    private val fireTickShotgun = IntegerValue("FireTick-Shotgun", 1, 0, 5).displayable { weapons.get() == "Shotgun" }
    private val noSpreadTickShotgun =
        IntegerValue("NoSpreadBaseTick-Shotgun", 2, 0, 5).displayable { weapons.get() == "Shotgun" }
    private val silentRotateShotgun = BoolValue("SilentRotate-Shotgun", true).displayable { weapons.get() == "Shotgun" }

    // P250
    private val priorityP250 =
        ListValue("Priority-P250", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "P250" }
    private val pitchOffsetP250 =
        FloatValue("PitchOffset-P250", 0.2F, -0.5F, 5F).displayable { weapons.get() == "P250" }
    private val targetPredictSizeP250 =
        FloatValue("TargetPredictSize-P250", 4.3F, 0F, 10F).displayable { weapons.get() == "P250" }
    private val fireModeP250 =
        ListValue("FireMode-P250", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "P250" }
    private val fireTickP250 = IntegerValue("FireTick-P250", 1, 0, 5).displayable { weapons.get() == "P250" }
    private val noSpreadTickP250 =
        IntegerValue("NoSpreadBaseTick-P250", 2, 0, 5).displayable { weapons.get() == "P250" }
    private val silentRotateP250 = BoolValue("SilentRotate-P250", true).displayable { weapons.get() == "P250" }

    // Deagle
    private val priorityDeagle =
        ListValue("Priority-Deagle", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "Deagle" }
    private val pitchOffsetDeagle =
        FloatValue("PitchOffset-Deagle", 0.2F, -0.5F, 5F).displayable { weapons.get() == "Deagle" }
    private val targetPredictSizeDeagle =
        FloatValue("TargetPredictSize-Deagle", 4.3F, 0F, 10F).displayable { weapons.get() == "Deagle" }
    private val fireModeDeagle =
        ListValue("FireMode-Deagle", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "Deagle" }
    private val fireTickDeagle = IntegerValue("FireTick-Deagle", 1, 0, 5).displayable { weapons.get() == "Deagle" }
    private val noSpreadTickDeagle =
        IntegerValue("NoSpreadBaseTick-Deagle", 2, 0, 5).displayable { weapons.get() == "Deagle" }
    private val silentRotateDeagle = BoolValue("SilentRotate-Deagle", true).displayable { weapons.get() == "Deagle" }

    // MP7
    private val priorityMP7 =
        ListValue("Priority-MP7", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "MP7" }
    private val pitchOffsetMP7 = FloatValue("PitchOffset-MP7", 0.2F, -0.5F, 5F).displayable { weapons.get() == "MP7" }
    private val targetPredictSizeMP7 =
        FloatValue("TargetPredictSize-MP7", 4.3F, 0F, 10F).displayable { weapons.get() == "MP7" }
    private val fireModeMP7 =
        ListValue("FireMode-MP7", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "MP7" }
    private val fireTickMP7 = IntegerValue("FireTick-MP7", 1, 0, 5).displayable { weapons.get() == "MP7" }
    private val noSpreadTickMP7 = IntegerValue("NoSpreadBaseTick-MP7", 2, 0, 5).displayable { weapons.get() == "MP7" }
    private val silentRotateMP7 = BoolValue("SilentRotate-MP7", true).displayable { weapons.get() == "MP7" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (mc?.thePlayer?.heldItem?.item ?: return) {
            Items.stone_hoe -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "AK47"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickAK.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateAK.get()
            }

            Items.iron_hoe -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "M4A1"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickM4A1.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateM4A1.get()
            }

            Items.golden_hoe -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "AWP"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickAWP.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateAWP.get()
            }

            Items.diamond_shovel -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "Shotgun"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickShotgun.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateShotgun.get()
            }

            Items.wooden_pickaxe -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "P250"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickP250.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateP250.get()
            }

            Items.golden_pickaxe -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "Deagle"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickDeagle.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateDeagle.get()
            }

            Items.stone_shovel -> {
                Leaf.moduleManager[WeaponConfig::class.java]!!.weapons.value = "MP7"
                Leaf.moduleManager[RageBot::class.java]!!.priority.value = priorityMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickMP7.get()
                Leaf.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateMP7.get()
            }
        }
    }
}