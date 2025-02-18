package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Items

@ModuleInfo(name = "WeaponConfig", category = ModuleCategory.Rage)
class WeaponConfig : Module() {
    private val weapons = ListValue("Weapons", arrayOf("AK47", "M4A1", "AWP", "Shotgun", "P250", "Deagle", "MP7"), "AK47")

    // AK47
    private val headAK = BoolValue("HitBox-Head-AK", true).displayable { weapons.get() == "AK47" }
    private val chestAK = BoolValue("HitBox-Chest-AK", true).displayable { weapons.get() == "AK47" }
    private val feetAK = BoolValue("HitBox-Feet-AK", true).displayable { weapons.get() == "AK47" }
    private val priorityAK = ListValue("Priority-AK", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "AK47" }
    private val pitchOffsetAK = FloatValue("PitchOffset-AK", 0.2F, -0.5F, 1F).displayable { weapons.get() == "AK47" }
    private val targetPredictSizeAK = FloatValue("TargetPredictSize-AK", 4.3F, 0F, 10F).displayable { weapons.get() == "AK47" }
    private val fireModeAK = ListValue("FireMode-AK", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "AK47" }
    private val fireTickAK = IntegerValue("FireTick-AK", 1, 0, 5).displayable { weapons.get() == "AK47" }
    private val noSpreadTickAK = IntegerValue("NoSpreadBaseTick-AK", 2, 0, 5).displayable { weapons.get() == "AK47" }
    private val silentRotateAK = BoolValue("SilentRotate-AK", true).displayable { weapons.get() == "AK47" }

    // M4A1
    private val headM4A1 = BoolValue("HitBox-Head-M4A1", true).displayable { weapons.get() == "M4A1" }
    private val chestM4A1 = BoolValue("HitBox-Chest-M4A1", true).displayable { weapons.get() == "M4A1" }
    private val feetM4A1 = BoolValue("HitBox-Feet-M4A1", true).displayable { weapons.get() == "M4A1" }
    private val priorityM4A1 = ListValue("Priority-M4A1", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "M4A1" }
    private val pitchOffsetM4A1 = FloatValue("PitchOffset-M4A1", 0.2F, -0.5F, 5F).displayable { weapons.get() == "M4A1" }
    private val targetPredictSizeM4A1 = FloatValue("TargetPredictSize-M4A1", 4.3F, 0F, 10F).displayable { weapons.get() == "M4A1" }
    private val fireModeM4A1 = ListValue("FireMode-M4A1", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "M4A1" }
    private val fireTickM4A1 = IntegerValue("FireTick-M4A1", 1, 0, 5).displayable { weapons.get() == "M4A1" }
    private val noSpreadTickM4A1 = IntegerValue("NoSpreadBaseTick-M4A1", 2, 0, 5).displayable { weapons.get() == "M4A1" }
    private val silentRotateM4A1 = BoolValue("SilentRotate-M4A1", true).displayable { weapons.get() == "M4A1" }

    // AWP
    private val headAWP = BoolValue("HitBox-Head-AWP", true).displayable { weapons.get() == "AWP" }
    private val chestAWP = BoolValue("HitBox-Chest-AWP", true).displayable { weapons.get() == "AWP" }
    private val feetAWP = BoolValue("HitBox-Feet-AWP", true).displayable { weapons.get() == "AWP" }
    private val priorityAWP = ListValue("Priority-AWP", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "AWP" }
    private val pitchOffsetAWP = FloatValue("PitchOffset-AWP", 0.2F, -0.5F, 5F).displayable { weapons.get() == "AWP" }
    private val targetPredictSizeAWP = FloatValue("TargetPredictSize-AWP", 4.3F, 0F, 10F).displayable { weapons.get() == "AWP" }
    private val fireModeAWP = ListValue("FireMode-AWP", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "AWP" }
    private val fireTickAWP = IntegerValue("FireTick-AWP", 1, 0, 5).displayable { weapons.get() == "AWP" }
    private val noSpreadTickAWP = IntegerValue("NoSpreadBaseTick-AWP", 2, 0, 5).displayable { weapons.get() == "AWP" }
    private val silentRotateAWP = BoolValue("SilentRotate-AWP", true).displayable { weapons.get() == "AWP" }

    // Shotgun
    private val headShotgun = BoolValue("HitBox-Head-Shotgun", true).displayable { weapons.get() == "Shotgun" }
    private val chestShotgun = BoolValue("HitBox-Chest-Shotgun", true).displayable { weapons.get() == "Shotgun" }
    private val feetShotgun = BoolValue("HitBox-Feet-Shotgun", true).displayable { weapons.get() == "Shotgun" }
    private val priorityShotgun = ListValue("Priority-Shotgun", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "Shotgun" }
    private val pitchOffsetShotgun = FloatValue("PitchOffset-Shotgun", 0.2F, -0.5F, 5F).displayable { weapons.get() == "Shotgun" }
    private val targetPredictSizeShotgun = FloatValue("TargetPredictSize-Shotgun", 4.3F, 0F, 10F).displayable { weapons.get() == "Shotgun" }
    private val fireModeShotgun = ListValue("FireMode-Shotgun", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "Shotgun" }
    private val fireTickShotgun = IntegerValue("FireTick-Shotgun", 1, 0, 5).displayable { weapons.get() == "Shotgun" }
    private val noSpreadTickShotgun = IntegerValue("NoSpreadBaseTick-Shotgun", 2, 0, 5).displayable { weapons.get() == "Shotgun" }
    private val silentRotateShotgun = BoolValue("SilentRotate-Shotgun", true).displayable { weapons.get() == "Shotgun" }

    // P250
    private val headP250 = BoolValue("HitBox-Head-P250", true).displayable { weapons.get() == "P250" }
    private val chestP250 = BoolValue("HitBox-Chest-P250", true).displayable { weapons.get() == "P250" }
    private val feetP250 = BoolValue("HitBox-Feet-P250", true).displayable { weapons.get() == "P250" }
    private val priorityP250 = ListValue("Priority-P250", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "P250" }
    private val pitchOffsetP250 = FloatValue("PitchOffset-P250", 0.2F, -0.5F, 5F).displayable { weapons.get() == "P250" }
    private val targetPredictSizeP250 = FloatValue("TargetPredictSize-P250", 4.3F, 0F, 10F).displayable { weapons.get() == "P250" }
    private val fireModeP250 = ListValue("FireMode-P250", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "P250" }
    private val fireTickP250 = IntegerValue("FireTick-P250", 1, 0, 5).displayable { weapons.get() == "P250" }
    private val noSpreadTickP250 = IntegerValue("NoSpreadBaseTick-P250", 2, 0, 5).displayable { weapons.get() == "P250" }
    private val silentRotateP250 = BoolValue("SilentRotate-P250", true).displayable { weapons.get() == "P250" }

    // Deagle
    private val headDeagle = BoolValue("HitBox-Head-Deagle", true).displayable { weapons.get() == "Deagle" }
    private val chestDeagle = BoolValue("HitBox-Chest-Deagle", true).displayable { weapons.get() == "Deagle" }
    private val feetDeagle = BoolValue("HitBox-Feet-Deagle", true).displayable { weapons.get() == "Deagle" }
    private val priorityDeagle = ListValue("Priority-Deagle", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "Deagle" }
    private val pitchOffsetDeagle = FloatValue("PitchOffset-Deagle", 0.2F, -0.5F, 5F).displayable { weapons.get() == "Deagle" }
    private val targetPredictSizeDeagle = FloatValue("TargetPredictSize-Deagle", 4.3F, 0F, 10F).displayable { weapons.get() == "Deagle" }
    private val fireModeDeagle = ListValue("FireMode-Deagle", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "Deagle" }
    private val fireTickDeagle = IntegerValue("FireTick-Deagle", 1, 0, 5).displayable { weapons.get() == "Deagle" }
    private val noSpreadTickDeagle = IntegerValue("NoSpreadBaseTick-Deagle", 2, 0, 5).displayable { weapons.get() == "Deagle" }
    private val silentRotateDeagle = BoolValue("SilentRotate-Deagle", true).displayable { weapons.get() == "Deagle" }

    // MP7
    private val headMP7 = BoolValue("HitBox-Head-MP7", true).displayable { weapons.get() == "MP7" }
    private val chestMP7 = BoolValue("HitBox-Chest-MP7", true).displayable { weapons.get() == "MP7" }
    private val feetMP7 = BoolValue("HitBox-Feet-MP7", true).displayable { weapons.get() == "MP7" }
    private val priorityMP7 = ListValue("Priority-MP7", arrayOf("Head", "Chest", "Feet"), "Head").displayable { weapons.get() == "MP7" }
    private val pitchOffsetMP7 = FloatValue("PitchOffset-MP7", 0.2F, -0.5F, 5F).displayable { weapons.get() == "MP7" }
    private val targetPredictSizeMP7 = FloatValue("TargetPredictSize-MP7", 4.3F, 0F, 10F).displayable { weapons.get() == "MP7" }
    private val fireModeMP7 = ListValue("FireMode-MP7", arrayOf("Legit", "Packet"), "Packet").displayable { weapons.get() == "MP7" }
    private val fireTickMP7 = IntegerValue("FireTick-MP7", 1, 0, 5).displayable { weapons.get() == "MP7" }
    private val noSpreadTickMP7 = IntegerValue("NoSpreadBaseTick-MP7", 2, 0, 5).displayable { weapons.get() == "MP7" }
    private val silentRotateMP7 = BoolValue("SilentRotate-MP7", true).displayable { weapons.get() == "MP7" }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when(mc?.thePlayer?.heldItem?.item?:return){
            Items.stone_hoe -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "AK47"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickAK.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateAK.get()
            }
            Items.iron_hoe-> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "M4A1"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickM4A1.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickM4A1.get()
                LiquidBounce.moduleManager[ RageBot::class.java]!!.rotateValue.value = silentRotateM4A1.get()
            }
            Items.golden_hoe -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "AWP"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickAWP.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateAWP.get()
            }
            Items.diamond_shovel -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "Shotgun"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickShotgun.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateShotgun.get()
            }
            Items.wooden_pickaxe -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "P250"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickP250.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateP250.get()
            }
            Items.golden_pickaxe -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "Deagle"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickDeagle.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateDeagle.get()
            }
            Items.stone_shovel -> {
                LiquidBounce.moduleManager[WeaponConfig::class.java]!!.weapons.value = "MP7"
                LiquidBounce.moduleManager[RageBot::class.java]!!.head.value = headMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.chest.value = chestMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.feet.value = feetMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.priority.value = priorityMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.pitchOffset.value = pitchOffsetMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.targetPredictSize.value = targetPredictSizeMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireMode.value = fireModeMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.fireTick.value = fireTickMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.noSpreadTick.value = noSpreadTickMP7.get()
                LiquidBounce.moduleManager[RageBot::class.java]!!.rotateValue.value = silentRotateMP7.get()
            }
        }
    }
}