//All the code was written by N0ne.
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.code.customCode
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.data.PitchData
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.data.YawData
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.data.YawData2
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.invoke.*
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.utils.*
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot.isBot
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils.getRotationTo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.EntityUtils.isFriend
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.*

@ModuleInfo(name = "Aura", category = ModuleCategory.COMBAT)
object Aura : Module() {
    //Attack
    private val attack = BoolValue("Attack", true)
    private val maxAttackDelay = IntegerValue("MaxAttackDelay", 100, 0, 1000).displayable { attack.get() }
    private val minAttackDelay = IntegerValue("MinAttackDelay", 50, 0, 1000).displayable { attack.get() }
    private val maxAttackRange = FloatValue("MaxAttackRange", 3F, 0F, 6F).displayable { attack.get() }
    private val minAttackRange = FloatValue("MinAttackRange", 3F, 0F, 6F).displayable { attack.get() }
    private val attackRangeSprintReduceMaxValue = FloatValue("AttackRangeSprintReduceMaxValue", 0f, 0f, 6f).displayable { attack.get() }
    private val attackRangeSprintReduceMinValue = FloatValue("AttackRangeSprintReduceMinValue", 0f, 0f, 6f).displayable { attack.get() }
    private val attackRangeAirReduceMaxValue = FloatValue("AttackRangeAirReduceMaxValue", 0f, 0f, 6f).displayable { attack.get() }
    private val attackRangeAirReduceMinValue = FloatValue("AttackRangeAirReduceMinValue", 0f, 0f, 6f).displayable { attack.get() }
    private val minHurtTime = IntegerValue("MinAttackHurtTime", 9, 0, 10).displayable { attack.get() }
    private val maxHurtTime = IntegerValue("MaxAttackHurtTime", 10, 0, 10).displayable { attack.get() }
    private val swingMode = ListValue("SwingMode", arrayOf("SwingItem", "C0A","None"), "SwingItem").displayable { attack.get() }
    private val attackMode = ListValue("AttackMode", arrayOf("C02", "KeyBindAttack","None"), "C02").displayable { attack.get() }
    private val callAttackEvent = BoolValue("CallAttackEvent", true).displayable { attack.get() }
    private val attackTargetEntityWithCurrentItem = BoolValue("AttackTargetEntityWithCurrentItem", true).displayable { attack.get() }
    private val allowAttackWhenNotBlocking = BoolValue("AllowAttackWhenNotBlocking", false).displayable { attack.get() }

    //AutoBlock
    private val autoBlockMode = ListValue("AutoBlockMode", arrayOf("C08", "KeyBind","None"), "C07")
    private val autoBlockTrigger = ListValue("AutoBlockTrigger", arrayOf("Range-Always", "Always","Range-Delay"), "Range-Always").displayable{autoBlockMode.get() != "None"}
    private val autoBlockDelayValue = IntegerValue("AutoBlockDelay", 50, 1, 1000).displayable{autoBlockMode.get() != "None"}
    private val autoBlockRange = FloatValue("AutoBlockMaxRange", 3F, 0F, 6F).displayable{autoBlockMode.get() != "None"}
    private val autoBlockDetectsHeldItemAreOnlySwords = BoolValue("AutoBlockDetectsHeldItemAreOnlySwords", true).displayable{autoBlockMode.get() != "None"}
    private val autoBlockDetectionGUIIsNullOnly = BoolValue("AutoBlockDetectionGUIIsNullOnly", true).displayable{autoBlockMode.get() != "None"}

    //Visibility
    private val visibilityDetection = BoolValue("VisibilityDetection", true)
    val visibilityDetectionEntityBoundingBox = BoolValue("VisibilityDetectionEntityBoundingBox", true).displayable { visibilityDetection.get() }
    private val visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue = BoolValue("VisibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue", true).displayable { visibilityDetection.get() }
    private val maxThroughWallsRange = FloatValue("MaxThroughWallsRange", 3F, 0F, 6F).displayable { !visibilityDetection.get() }
    private val minThroughWallsRange = FloatValue("MinThroughWallsRange", 3F, 0F, 6F).displayable { !visibilityDetection.get() }

    //Rotation
    private val rotationMode = ListValue("RotationMode", arrayOf("Smooth","None"), "Smooth")
    private val smoothMode = ListValue("SmoothMode", arrayOf("None", "Slerp","AdaptiveBezier","AdaptiveSlerp", "DataSimulationA", "DataSimulationB", "Sinusoidal", "Spring", "CosineInterpolation", "LogarithmicInterpolation", "ElasticSpring", "Bezier", "Custom"), "Slerp").displayable { rotationMode.get() == "Smooth" }
    val customSmoothCode = TextValue("CustomSmoothCode","ifelseifelseifelseifelse").displayable { smoothMode.get() == "Custom" }
    val rotateValue = BoolValue("SilentRotate", false).displayable { rotationMode.get() != "None" }
    private val randomSpeedValue = BoolValue("RandomSpeed", true).displayable { rotationMode.get() != "None" }
    private val randomSpeedFrequency = IntegerValue("RandomSpeedFrequency", 1, 1, 10).displayable { randomSpeedValue.get()&&rotationMode.get() != "None" }
    private val slerpSpeed = FloatValue("SlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Slerp" &&rotationMode.get() != "None"}
    private val slerpRandomMinSpeed = FloatValue("Slerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp"&&rotationMode.get() != "None" }
    private val slerpRandomMaxSpeed = FloatValue("Slerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" &&rotationMode.get() != "None"}
    private val adaptiveBezierSpeed = FloatValue("AdaptiveBezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" &&rotationMode.get() != "None"}
    private val adaptiveBezierRandomMinSpeed = FloatValue("AdaptiveBezier-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveBezier"&&rotationMode.get() != "None" }
    private val adaptiveBezierRandomMaxSpeed = FloatValue("AdaptiveBezier-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveBezier" &&rotationMode.get() != "None"}
    private val adaptiveSlerpSpeed = FloatValue("AdaptiveSlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "AdaptiveSlerp" &&rotationMode.get() != "None"}
    private val adaptiveSlerpRandomMinSpeed = FloatValue("AdaptiveSlerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveSlerp"&&rotationMode.get() != "None" }
    private val adaptiveSlerpRandomMaxSpeed = FloatValue("AdaptiveSlerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveSlerp" &&rotationMode.get() != "None"}
    private val adaptiveBezierP0 = FloatValue("AdaptiveBezier-P0", 0f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP1 = FloatValue("AdaptiveBezier-P1", 0.05f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP2 = FloatValue("AdaptiveBezier-P2", 0.2f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP3 = FloatValue("AdaptiveBezier-P3", 0.4f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP4 = FloatValue("AdaptiveBezier-P4", 0.6f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP5 = FloatValue("AdaptiveBezier-P5", 0.8f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP6 = FloatValue("AdaptiveBezier-P6", 0.95f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val adaptiveBezierP7 = FloatValue("AdaptiveBezier-P7", 1f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() != "None" }
    private val dampingSpeed = FloatValue("DampingSpeed", 0.5f, 0.01f, 1f).displayable { smoothMode.get() == "Damping"&&rotationMode.get() != "None" }
    private val dampingRandomMinSpeed = FloatValue("Damping-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping"&&rotationMode.get() != "None" }
    private val dampingRandomMaxSpeed = FloatValue("Damping-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" &&rotationMode.get() != "None"}
    private val sinusoidalSpeed = FloatValue("SinusoidalTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Sinusoidal" &&rotationMode.get() != "None"}
    private val sinusoidalRandomMinSpeed = FloatValue("Sinusoidal-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal"&&rotationMode.get() != "None" }
    private val sinusoidalRandomMaxSpeed = FloatValue("Sinusoidal-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal"&&rotationMode.get() != "None" }
    private val springSpeed = FloatValue("SpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Spring"&&rotationMode.get() != "None" }
    private val springRandomMinSpeed = FloatValue("Spring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" &&rotationMode.get() != "None"}
    private val springRandomMaxSpeed = FloatValue("Spring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring"&&rotationMode.get() != "None" }
    private val cosineInterpolationSpeed = FloatValue("CosineInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "CosineInterpolation"&&rotationMode.get() != "None" }
    private val cosineInterpolationRandomMinSpeed = FloatValue("CosineInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation"&&rotationMode.get() != "None" }
    private val cosineInterpolationRandomMaxSpeed = FloatValue("CosineInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation"&&rotationMode.get() != "None" }
    private val logarithmicInterpolationSpeed = FloatValue("LogarithmicInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "LogarithmicInterpolation"&&rotationMode.get() != "None" }
    private val logarithmicInterpolationRandomMinSpeed = FloatValue("LogarithmicInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation" &&rotationMode.get() != "None"}
    private val logarithmicInterpolationRandomMaxSpeed = FloatValue("LogarithmicInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation"&&rotationMode.get() != "None" }
    private val elasticSpringSpeed = FloatValue("ElasticSpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "ElasticSpring"&&rotationMode.get() != "None"}
    private val elasticSpringRandomMinSpeed = FloatValue("ElasticSpring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" &&rotationMode.get() != "None"}
    private val elasticSpringRandomMaxSpeed = FloatValue("ElasticSpring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" &&rotationMode.get() != "None"}
    private val bezierSpeed = FloatValue("BezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Bezier" &&rotationMode.get() != "None"}
    private val bezierRandomMinSpeed = FloatValue("Bezier-RandomMinTurnSpeed", -0.1f, -5f, 10f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierRandomMaxSpeed = FloatValue("Bezier-RandomMaxTurnSpeed", 0.1f, -5f, 10f).displayable { smoothMode.get() == "Bezier" &&rotationMode.get() != "None"}
    private val bezierP0 = FloatValue("Bezier-P0", 0f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierP1 = FloatValue("Bezier-P1", 0.05f, 0f, 1f).displayable { smoothMode.get() == "Bezier" &&rotationMode.get() != "None"}
    private val bezierP2 = FloatValue("Bezier-P2", 0.2f, 0f, 1f).displayable { smoothMode.get() == "Bezier" &&rotationMode.get() != "None"}
    private val bezierP3 = FloatValue("Bezier-P3", 0.4f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierP4 = FloatValue("Bezier-P4", 0.6f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierP5 = FloatValue("Bezier-P5", 0.8f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierP6 = FloatValue("Bezier-P6", 0.95f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val bezierP7 = FloatValue("Bezier-P7", 1f, 0f, 1f).displayable { smoothMode.get() == "Bezier"&&rotationMode.get() != "None" }
    private val elasticity = FloatValue("ElasticSpring-Elasticity", 0.3f, 0.01f, 1f).displayable{smoothMode.get() == "ElasticSpring"&&rotationMode.get() != "None"}
    private val dampingFactor2 = FloatValue("ElasticSpring-DampingFactor", 0.5f, 0.01f, 1f).displayable{smoothMode.get() == "ElasticSpring"&&rotationMode.get() != "None"}
    private val reverseDeflectionAllowedOnlyOutside = BoolValue("ReverseDeflectionAllowedOnlyOutside", true).displayable { rotationMode.get() != "None"}
    private val extraReverseDeflectionRate = IntegerValue("ExtraReverseDeflectionRate", 50 , 0 , 100).displayable { rotationMode.get() != "None" }
    private val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 10, 0, 20).displayable { rotateValue.get()&&rotationMode.get() != "None" }
    private val StationaryAccelerateSpeed = FloatValue("WhenTargetStationaryAccelerateSpeed", 0.1f, 0.0f, 1f).displayable { rotationMode.get() != "None" }
    private val noFacingRotations = BoolValue("NoFacingRotations", true).displayable{ rotationMode.get() != "None"}
    private val silentRotateKeepLastRotation = BoolValue("NoFacingRotations-SilentRotateKeepLastRotation", true).displayable{noFacingRotations.get() &&rotationMode.get() != "None"}
    private val noFacingRotationsMaxRange = FloatValue("NoFacingPitchMaxRange", 1F, 0F, 6F).displayable{noFacingRotations.get()&&rotationMode.get() != "None"}
    private val noFacingPitch = BoolValue("NoFacingPitch", true).displayable{ rotationMode.get() != "None"}
    private val noFacingPitchOnlyPlayerMove = BoolValue("NoFacingOnlyPlayerMove", true).displayable{noFacingPitch.get()&&rotationMode.get() != "None"}
    private val noFacingPitchMaxRange = FloatValue("NoFacingMaxRange", 1F, 0F, 6F).displayable{noFacingPitch.get()&&rotationMode.get() != "None"}

    //Jitter
    private val pitchJitter = BoolValue("PitchJitter", true).displayable { rotationMode.get() != "None" }
    private val pitchJitterRandomMode = ListValue("PitchJitterRandomMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchJitterAmount = FloatValue("PerlinNoiseRandomPitchJitterAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None"&& pitchJitter.get() }
    private val randomPitchJitterPerlinNoiseMinSeed = IntegerValue("PerlinNoiseRandomPitchJitterMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None"&& pitchJitter.get() }
    private val randomPitchJitterPerlinNoiseMaxSeed = IntegerValue("PerlinNoiseRandomPitchJitterMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& pitchJitter.get() }
    private val randomPitchMinValue = FloatValue("RandomPitchMinValue", -1f, -5f, 5f).displayable { rotationMode.get() != "None" && pitchJitter.get()}
    private val randomPitchMaxValue = FloatValue("RandomPitchMaxValue", 1f, -5f, 5f).displayable { rotationMode.get() != "None"&& pitchJitter.get() }
    private val yawJitter = BoolValue("YawJitter", true).displayable { rotationMode.get() != "None" }
    private val yawJitterRandomMode = ListValue("YawJitterRandomMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawJitterAmount = FloatValue("PerlinNoiseRandomYawJitterAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None"&& yawJitter.get() }
    private val randomYawJitterPerlinNoiseMinSeed = IntegerValue("PerlinNoiseRandomYawJitterMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None"&& yawJitter.get() }
    private val randomYawJitterPerlinNoiseMaxSeed = IntegerValue("PerlinNoiseRandomYawJitterMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& yawJitter.get() }
    private val randomYawMinValue = FloatValue("RandomYawMinValue", -1f, -5f, 5f).displayable { rotationMode.get() != "None" && yawJitter.get()}
    private val randomYawMaxValue = FloatValue("RandomYawMaxValue", 1f, -5f, 5f).displayable { rotationMode.get() != "None"&& yawJitter.get() }

    //RandomTargetPos
    private val randomTargetPos = BoolValue("RandomTargetPos", true).displayable { rotationMode.get() != "None" }
    private val randomTargetPosOnlyOutside = BoolValue("RandomTargetPosOnlyOutside", true).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val minRandomTargetPosHurtTime = IntegerValue("MinRandomTargetPosHurtTime", 0, 0, 10).displayable { randomTargetPos.get() }
    private val maxRandomTargetPosHurtTime = IntegerValue("MaxRandomTargetPosHurtTime", 0, 0, 10).displayable { randomTargetPos.get() }
    private val randomTargetPosFrequency = IntegerValue("RandomTargetPosFrequency", 500, 0, 1000).displayable { randomTargetPos.get() }
    private val randomTargetPosMode = ListValue("RandomTargetPosMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetLevelMode = ListValue("RandomTargetPosLevelMode", arrayOf("Single", "SymmetricalDistribution"), "Single").displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleLevelModeAmount = FloatValue("PerlinNoiseRandomTargetPosSingleLevelModeAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleLevelModeMinSeed = IntegerValue("PerlinNoiseRandomTargetPosSingleLevelModeMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleLevelModeMaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSingleLevelModeMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIAmount = FloatValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMinSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIRate = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIAmount = FloatValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMinSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIRate = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSingleLevelModeMinValue = IntegerValue("RandomTargetPosSingleLevelModeMinValue", 1, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSingleLevelModeMaxValue = IntegerValue("RandomTargetPosSingleLevelModeMaxValue", 10000, 1, 10000).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIMinValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIMinValue", 0.5f, -1f, 1f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIMaxValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIMaxValue", 0.2f, -1f, 1f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIRate = IntegerValue("RandomTargetPosSymmetricalDistributionLevelModePartIRate", 50, 0, 100).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIIMinValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIIMinValue", 0.5f, -1f, 1f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIIMaxValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIIMaxValue", 0.2f, -1f, 1f).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionLevelModePartIIRate = IntegerValue("RandomTargetPosSymmetricalDistributionLevelModePartIIRate", 50, 0, 100).displayable { rotationMode.get() != "None"&& randomTargetPos.get() }

    //Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable { raycastValue.get() }
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable { raycastValue.get() }

    //Predict
    private val predictValue = BoolValue("Predict", true)
    private val predictSize = FloatValue("PredictSize", 2f, 1f, 5f).displayable{predictValue.get()}

    //Other
    private val targetMode = ListValue("TargetMode", arrayOf("Single", "Switch"), "Single")
    private val switchDelay = IntegerValue("SwitchDelay", 50, 0, 1000)
    private val fov = FloatValue("FOV", 180f, 0f, 180f)
    private val playerPosYOffset = FloatValue("PlayerPosYOffset", 0F, -1F, 1F)
    private val targetPosYOffset = FloatValue("TargetPosYOffset", 0F, -1F, 1F)
    private val noBadPackets = BoolValue("NoBadPackets", true)
    private val hitable = BoolValue("Hitable", true)
    private val sprint = BoolValue("Sprint", true)
    val strafe = BoolValue("StrictStrafe", true).displayable { rotateValue.get() }
    private val fakeSwing = BoolValue("FakeSwing", true)

    //Debug
    private val debug = BoolValue("Debug", false)
    private val cpsDebug = BoolValue("CPSDebug", false)
    private val renderDebug = BoolValue("RenderDebug", false)

    //Render
    private val circleValue = BoolValue("Circle", true)
    private val circleRealRange = BoolValue("CircleRealRange", true)
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() && !circleRealRange.get()}
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }

    private var RR = false
    private var speedValue = 0.0
    private var speedTick = 0
    var sprintValue = true //Sprint
    var strictStrafeValue = false //EntityLivingBase
    private var lastRotation = Rotation(0.0F,0.0F)
    private val clickDelay = MSTimer()
    private var abreset = false
    private val autoBlockDelay = MSTimer()
    private var allowStrictStrafe = false
    private var cps = 0
    private val CPSTimer = MSTimer()
    private var cpsUpdate = false
    private var rayCastedTarget: EntityLivingBase? = null
    private var currentRange = 0.0
    private var currentThroughWallsRange = 0.0
    private var randomPosVec = Vec3(0.0,0.0,0.0)
    private val randomPosTimer = MSTimer()
    override fun onEnable() {
        if (debug.get()){
           if (allowAttackWhenNotBlocking.get() && autoBlockMode.get() != "None") ChatPrint("§0[§cError§0] §7Conflict settings:id(01)\n§7at Aura.allowAttackWhenNotBlocking(BoolValue)\n§7at Aura.autoBlockMode(ListValue)")
            if (visibilityDetection.get() && (maxThroughWallsRange.get() != 0.0F|| minThroughWallsRange.get() != 0.0f)) ChatPrint("§0[§cError§0] §7Conflict settings:id(02)\n§7at Aura.visibilityDetection(BoolValue)\n§7at Aura.throughWallsRange(FloatValue)")
            if (rotateValue.get() && attackMode.get() == "KeyBindAttack") ChatPrint("§0[§cError§0] §7Invalid action:id(03)\n§7at Aura.rotateValue(BoolValue)\n§7at Aura.attackMode(ListValue)")
            if (rotationMode.get() == "None" && hitable.get()) ChatPrint("§0[§eWarn§0] §7Potentially invalid action:id(04)\n§7at Aura.rotateMode(ListValue)\n§7at Aura.hitable(BoolValue)")
        }
    }
    override fun onDisable() {
        switchDelayValue.reset()
        randomPosTimer.reset()
        CPSTimer.reset()
        autoBlockDelay.reset()
        clickDelay.reset()
        rayCastedTarget = null
        RR = false
        foundTarget = false
        cpsUpdate = false
        allowStrictStrafe = false
        abreset = false
        sprintValue = true
        speedTick = 0
        strictStrafeValue = false
        mc.gameSettings.keyBindUseItem.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)
        mc.gameSettings.keyBindAttack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindAttack)
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(circleThicknessValue.get())
            GL11.glColor4f(
                circleRedValue.get().toFloat() / 255.0F,
                circleGreenValue.get().toFloat() / 255.0F,
                circleBlueValue.get().toFloat() / 255.0F,
                circleAlphaValue.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 5) {
                if (circleRealRange.get()) GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * currentRange.toFloat(),
                    (sin(i * Math.PI / 180.0).toFloat() * currentRange.toFloat())
                ) else GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * circleRange.get(),
                    (sin(i * Math.PI / 180.0).toFloat() * circleRange.get()))
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C02PacketUseEntity){
            if (CPSTimer.hasTimePassed(1000)){
              if (cpsDebug.get()) ChatPrint(cps.toString())
                cps = 0
                CPSTimer.reset()
            }else{
                cps ++
            }
        }
    }
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (renderDebug.get()) {
            val sr = ScaledResolution(mc)
            val text = "cr:$currentRange , ctr:$currentThroughWallsRange , speedR:$speedValue , TR:$speedTick" +
                              "cpsC:$cps , ssv:$strictStrafeValue , RR:$RR , RT:$rayCastedTarget" +
                              "sprint:${mc.thePlayer.isSprinting} , block:${mc.thePlayer.isBlocking} , eat:${mc.thePlayer.isEating} " +
                              "CYaw:${mc.thePlayer.rotationYaw} , CPitch:${mc.thePlayer.rotationPitch}" +
                              "abr:${autoBlockRange.get()} , YDN:$YawDataIndex , PDN:$PitchDataIndex"
            Fonts.minecraftFont.drawStringWithShadow(text, sr.scaledWidth / 2f - Fonts.minecraftFont.getStringWidth(text) / 2f,
                sr.scaledHeight / 2f - 60f, Color.blue.rgb)
        }
    }
    private var foundTarget = false
    private var entityList : MutableList<EntityPlayer> = arrayListOf()
    private val switchDelayValue = MSTimer()
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        val sprintReduceValue = if (mc.thePlayer.isSprinting) randomDouble(attackRangeSprintReduceMinValue.get().toDouble(),attackRangeSprintReduceMaxValue.get().toDouble()) else 0.0
        val airReduceValue = if (!mc.thePlayer.onGround) randomDouble(attackRangeAirReduceMinValue.get().toDouble(),attackRangeAirReduceMaxValue.get().toDouble()) else 0.0

        currentRange = randomDouble(minAttackRange.get().toDouble(),maxAttackRange.get().toDouble()) - sprintReduceValue - airReduceValue
        currentThroughWallsRange = randomDouble(minThroughWallsRange.get().toDouble(), maxThroughWallsRange.get().toDouble())
        val target = run {
            var currentTarget: EntityPlayer? = null
            if (raycastValue.get()) {
                val rayCastedEntity = RaycastUtils.raycastEntity(currentRange) {
                    (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand)
                            && (EntityUtils.isSelected(it, true) || raycastIgnoredValue.get()
                            && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
                }
                if (rayCastedEntity is EntityLivingBase && !isFriend(rayCastedEntity)) {
                    rayCastedTarget = rayCastedEntity
                    foundTarget = false
                }
            }

            for (entity in mc.theWorld.playerEntities?:return) {
                if (entity == rayCastedTarget && entity is EntityPlayer && !isFriend(entity) && !isBot(entity) && entity.isEntityAlive && (targetMode.get() == "Single" || !entityList.contains(entity)) && !entity.isDead && entity != player
                    && entity.getDistanceToEntityBox(player) <= currentRange && MathHelper.wrapAngleTo180_double(RotationUtils.getRotationDifference(entity)) <= fov.get()
                    && EntityUtils.isSelected(entity, true)) {
                    val (isVisible, _) = visibility(player, entity)
                    if ((!visibilityDetection.get() && !isVisible && entity.getDistanceToEntityBox(player) <= currentThroughWallsRange) || isVisible) {
                        currentTarget = rayCastedTarget as EntityPlayer?
                        foundTarget = true
                        break

                    }
                }else  foundTarget = false
            }

            if (!foundTarget) {
                for (entity in mc.theWorld.playerEntities?:return) {
                    if (entity is EntityPlayer && entity.isEntityAlive && !isFriend(entity) && !isBot(entity) && !entity.isDead && (targetMode.get() == "Single" || !entityList.contains(entity)) && entity != player
                        && EntityUtils.isSelected(entity, true) && MathHelper.wrapAngleTo180_double(RotationUtils.getRotationDifference(entity)) <= fov.get()
                        && entity.getDistanceToEntityBox(player) <= currentRange) {
                        val (isVisible, _) = visibility(player, entity)
                        if ((!visibilityDetection.get() && !isVisible && entity.getDistanceToEntityBox(player) <= currentThroughWallsRange) || isVisible) {
                            currentTarget = entity
                            break
                        }
                    }
                }
                entityList.clear()
            }
            entityList.clear()
            currentTarget
        }
        target?.let {
            if (targetMode.get() == "Switch") {
                if (switchDelayValue.hasTimePassed(switchDelay.get().toLong())){
                    switchDelayValue.reset()
                    entityList.add(it)
                }
            }else{
                entityList.clear()
            }
            entityList.add(it)
            if (!hitable.get() || hitable(it, currentRange) && attack.get() && it.hurtTime in minHurtTime.get()..maxHurtTime.get() && it.getDistanceToEntityBox(player) <= currentRange) {
                if (clickDelay.hasTimePassed(randomLong(minAttackDelay.get().toLong(),maxAttackDelay.get().toLong()))) {
                    clickDelay.reset()
                    attack(it)

                }
            } else if (fakeSwing.get()) mc.thePlayer.swingItem()
            allowStrictStrafe = true
            val itemStack: ItemStack? = player.heldItem
            if ((autoBlockTrigger.get() == "Range-Always" && it.getDistanceToEntityBox(player) <= autoBlockRange.get()) || (autoBlockTrigger.get() == "Always") || (autoBlockTrigger.get() == "Range-Delay" && autoBlockDelay.hasTimePassed(autoBlockDelayValue.get().toLong()))) {
                autoBlockDelay.reset()
                if ((!autoBlockDetectsHeldItemAreOnlySwords.get() ||(itemStack != null && itemStack.item is ItemSword)) && (!autoBlockDetectionGUIIsNullOnly.get() || mc.currentScreen == null)) {
                    when (autoBlockMode.get()) {
                        "C08" -> mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                        "KeyBind" -> {
                            mc.gameSettings.keyBindUseItem.pressed = true
                            abreset = true
                        }
                        else -> ABReset()
                    }
                } else ABReset()
            } else ABReset()
            sprintValue = sprint.get()
            if (speedTick < randomSpeedFrequency.get()) speedTick++ else {
                speedTick = 0
                when (smoothMode.get()) {
                    "Slerp" -> speedValue = getSpeedValue(it, slerpRandomMinSpeed.get(), slerpRandomMaxSpeed.get())
                    "AdaptiveBezier" -> speedValue = getSpeedValue(it, adaptiveBezierRandomMinSpeed.get(), adaptiveBezierRandomMaxSpeed.get())
                    "AdaptiveSlerp" -> speedValue = getSpeedValue(it, adaptiveSlerpRandomMinSpeed.get(), adaptiveSlerpRandomMaxSpeed.get())
                    "Damping" -> speedValue = getSpeedValue(it, dampingRandomMinSpeed.get(), dampingRandomMaxSpeed.get())
                    "Sinusoidal" -> speedValue = getSpeedValue(it, sinusoidalRandomMinSpeed.get(), sinusoidalRandomMaxSpeed.get())
                    "Spring" -> speedValue = getSpeedValue(it, springRandomMinSpeed.get(), springRandomMaxSpeed.get())
                    "CosineInterpolation" -> speedValue = getSpeedValue(it, cosineInterpolationRandomMinSpeed.get(), cosineInterpolationRandomMaxSpeed.get())
                    "LogarithmicInterpolation" -> speedValue = getSpeedValue(it, logarithmicInterpolationRandomMinSpeed.get(), logarithmicInterpolationRandomMaxSpeed.get())
                    "ElasticSpring" -> speedValue = getSpeedValue(it, elasticSpringRandomMinSpeed.get(), elasticSpringRandomMaxSpeed.get())
                    "Bezier" -> speedValue = getSpeedValue(it, bezierRandomMinSpeed.get(), bezierRandomMaxSpeed.get())
                }
            }

            val x = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get()) visibility(player, it).second.xCoord else it.posX
            val y = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get())visibility(player, it).second.yCoord else it.posY
            val z = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get()) visibility(player, it).second.zCoord else it.posZ

            randomPosVec = updatePos(it,x,y,z)

            val targetX = if (predictValue.get()) randomPosVec.xCoord + (it.posX - it.prevPosX) * predictSize.get() else randomPosVec.xCoord
            val targetY = if (predictValue.get()) randomPosVec.yCoord + targetPosYOffset.get()+(it.posY - it.prevPosY)* predictSize.get() else randomPosVec.yCoord + targetPosYOffset.get()
            val targetZ = if (predictValue.get()) randomPosVec.zCoord + (it.posZ - it.prevPosZ) * predictSize.get() else randomPosVec.zCoord
            val playerX = player.posX
            val playerY = player.posY + playerPosYOffset.get()
            val playerZ = player.posZ
            val targetVec = Vec3(targetX,targetY,targetZ)
            val playerVec = Vec3(playerX,playerY,playerZ)
            val rotation = getRotationTo(playerVec, targetVec)
            var currentYaw = mc.thePlayer.rotationYaw
            var currentPitch = mc.thePlayer.rotationPitch
            var currentServerYaw = RotationUtils.serverRotation.yaw
            var currentServerPitch = RotationUtils.serverRotation.pitch
            if (smoothMode.get() == "DataSimulationA" || smoothMode.get() == "DataSimulationB") {
                simulationYaw(if (rotateValue.get()) currentServerYaw else currentYaw, rotation.yaw,
                    if (rotateValue.get()) currentServerPitch else currentPitch, rotation.pitch)
                if (rotateValue.get()) RotationUtils.setTargetRotation(Rotation(yaw, pitch),keepDirectionTickValue.get())
            }
            if (smoothMode.get() != "DataSimulationA" && smoothMode.get() != "DataSimulationB") {
                var currentSpeed = 0.0f
                if (speedValue < 0.0 && reverseDeflectionAllowedOnlyOutside.get() && hitable(it,currentRange) && !RR) speedValue = 0.0
                when (smoothMode.get()) {
                    "Slerp" -> currentSpeed = if (!isMove(it)) slerpSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else slerpSpeed.get() + speedValue.toFloat()
                    "AdaptiveBezier" -> currentSpeed = if (!isMove(it)) adaptiveBezierSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else adaptiveBezierSpeed.get() + speedValue.toFloat()
                    "AdaptiveSlerp" -> currentSpeed = if (!isMove(it)) adaptiveSlerpSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else adaptiveSlerpSpeed.get() + speedValue.toFloat()
                    "Damping" -> currentSpeed = if (!isMove(it)) dampingSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else dampingSpeed.get() + speedValue.toFloat()
                    "Sinusoidal" -> currentSpeed = if (!isMove(it)) sinusoidalSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else sinusoidalSpeed.get() + speedValue.toFloat()
                    "Spring" -> currentSpeed = if (!isMove(it)) springSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else springSpeed.get() + speedValue.toFloat()
                    "CosineInterpolation" -> currentSpeed = if (!isMove(it)) cosineInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else cosineInterpolationSpeed.get() + speedValue.toFloat()
                    "LogarithmicInterpolation" -> currentSpeed = if (!isMove(it)) logarithmicInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else logarithmicInterpolationSpeed.get() + speedValue.toFloat()
                    "ElasticSpring" -> currentSpeed = if (!isMove(it)) elasticSpringSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else elasticSpringSpeed.get() + speedValue.toFloat()
                    "Bezier" -> currentSpeed = if (!isMove(it)) bezierSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else bezierSpeed.get() + speedValue.toFloat()
                }
                currentYaw = smoothYaw(currentYaw, rotation.yaw, currentSpeed)
                currentPitch = if (!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble()) || (noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())) smoothPitch(currentPitch, rotation.pitch, currentSpeed) else currentPitch
                currentServerYaw = smoothYaw(currentServerYaw, rotation.yaw, currentSpeed)
                currentServerPitch = if (!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble()) || (noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())) smoothPitch(currentServerPitch, rotation.pitch, currentSpeed) else currentServerPitch

                val rotationCoord = if (rotateValue.get()) getRotationToVec(currentServerYaw,currentServerPitch) else getRotationToVec(currentYaw,currentPitch)
                val yawNoiseValue = perlinNoise(rotationCoord.xCoord, 0.0, rotationCoord.zCoord, randomInt(randomYawJitterPerlinNoiseMinSeed.get(), randomYawJitterPerlinNoiseMaxSeed.get()))
                val yawJitterAmount = yawNoiseValue * randomYawJitterAmount.get()
                val pitchNoiseValue = perlinNoise(0.0, rotationCoord.yCoord, 0.0, randomInt(randomPitchJitterPerlinNoiseMinSeed.get(), randomPitchJitterPerlinNoiseMaxSeed.get()))
                val pitchJitterAmount = pitchNoiseValue * randomPitchJitterAmount.get()


                if (yawJitter.get()) if (rotateValue.get())
                    currentServerYaw += if (yawJitterRandomMode.get() == "Perlin") yawJitterAmount.toFloat() else randomDouble(randomYawMinValue.get().toDouble(), randomYawMaxValue.get().toDouble()).toFloat()
                else
                    currentYaw += if (yawJitterRandomMode.get() == "Perlin") yawJitterAmount.toFloat() else randomDouble(randomYawMinValue.get().toDouble(), randomYawMaxValue.get().toDouble()).toFloat()

                if (pitchJitter.get()) if (rotateValue.get())
                    currentServerPitch += if (pitchJitterRandomMode.get() == "Perlin") pitchJitterAmount.toFloat() else randomDouble(randomPitchMinValue.get().toDouble(), randomPitchMaxValue.get().toDouble()).toFloat()
                  else
                      currentPitch += if (pitchJitterRandomMode.get() == "Perlin") pitchJitterAmount.toFloat() else randomDouble(randomPitchMinValue.get().toDouble(), randomPitchMaxValue.get().toDouble()).toFloat()



                if ((!noFacingRotations.get() || !hitable(it, noFacingRotationsMaxRange.get().toDouble()))) {
                    if (noBadPackets.get() && ((!rotateValue.get() && (currentPitch < -90.0 || currentPitch > 90.0)) || (rotateValue.get() && (currentServerPitch < -90.0 || currentServerPitch > 90.0)))) {
                        if (debug.get()) ChatPrint("[Aura]Blocked a bad packet: $currentYaw , $currentPitch , $currentServerYaw , $currentServerPitch")
                        return
                    }
                  if (rotationMode.get() != "None") turn(currentYaw, currentPitch, currentServerYaw, currentServerPitch)
                } else if (silentRotateKeepLastRotation.get() && rotateValue.get()) RotationUtils.setTargetRotation(lastRotation)
            }
        } ?: run {
            allowStrictStrafe = false
            ABReset()
            sprintValue = true
            foundTarget = false
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (strafe.get() && rotateValue.get() && mc.thePlayer != null && allowStrictStrafe) {
            strictStrafeValue = true
            val (yaw) = RotationUtils.targetRotation ?: return
            var strafe = event.strafe
            var forward = event.forward
            val friction = event.friction
            var f = strafe * strafe + forward * forward
            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt_float(f)
                if (f < 1.0F) f = 1.0F
                f = friction / f
                strafe *= f
                forward *= f
                mc.thePlayer.motionX += strafe * MathHelper.cos((yaw * Math.PI / 180F).toFloat()) - forward * MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                mc.thePlayer.motionZ += forward * MathHelper.cos((yaw * Math.PI / 180F).toFloat()) + strafe * MathHelper.sin((yaw * Math.PI / 180F).toFloat())
            }
            event.cancelEvent()
        }else{
            strictStrafeValue = false
        }
    }
    private fun ABReset(){
        if (abreset){
            mc.gameSettings.keyBindUseItem.pressed = false
            abreset = false
        }
    }
    private var randomTargetPosNoiseSingleLevelModeValue: Double? = null
    private var randomTargetPosSingleLevelModeAmount: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModeP1Value: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModeP1Amount: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModeP2Value: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModeP2Amount: Double? = null
    private var randomTargetPosSingleLevelModeValue: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModePart1Value: Double? = null
    private var randomTargetPosSymmetricalDistributionLevelModePart2Value: Double? = null
    private fun updatePos(it:EntityPlayer, x:Double,y:Double,z:Double):Vec3{
        if (!randomTargetPos.get() ||
        (randomPosTimer.hasTimePassed(randomTargetPosFrequency.get().toLong()) &&
                it.hurtTime in minRandomTargetPosHurtTime.get()..maxRandomTargetPosHurtTime.get() && (!randomTargetPosOnlyOutside.get() || hitable(it, currentRange)))) {

        randomTargetPosNoiseSingleLevelModeValue = perlinNoise(x, y, z, randomInt(
            perlinNoiseRandomTargetPosSingleLevelModeMinSeed.get(), perlinNoiseRandomTargetPosSingleLevelModeMaxSeed.get()))
        randomTargetPosSingleLevelModeAmount = randomTargetPosNoiseSingleLevelModeValue!! * perlinNoiseRandomTargetPosSingleLevelModeAmount.get()

        randomTargetPosSymmetricalDistributionLevelModeP1Value = perlinNoise(x, y, z, randomInt(
            perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMinSeed.get(), perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed.get()))
        randomTargetPosSymmetricalDistributionLevelModeP1Amount = randomTargetPosSymmetricalDistributionLevelModeP1Value!! * perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIAmount.get()

        randomTargetPosSymmetricalDistributionLevelModeP2Value = perlinNoise(x, y, z, randomInt(
            perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMinSeed.get(), perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMaxSeed.get()))
        randomTargetPosSymmetricalDistributionLevelModeP2Amount = randomTargetPosSymmetricalDistributionLevelModeP2Value!! * perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIAmount.get()

        randomTargetPosSingleLevelModeValue = randomDouble(
            randomTargetPosSingleLevelModeMinValue.get().toDouble(), randomTargetPosSingleLevelModeMaxValue.get().toDouble())
        randomTargetPosSymmetricalDistributionLevelModePart1Value = randomDouble(
            randomTargetPosSymmetricalDistributionLevelModePartIMinValue.get().toDouble(), randomTargetPosSymmetricalDistributionLevelModePartIMaxValue.get().toDouble())
        randomTargetPosSymmetricalDistributionLevelModePart2Value = randomDouble(
            randomTargetPosSymmetricalDistributionLevelModePartIIMinValue.get().toDouble(), randomTargetPosSymmetricalDistributionLevelModePartIIMaxValue.get().toDouble())
            randomPosTimer.reset()
    }

        val baseX = when(randomTargetPosMode.get()) {
            "Perlin" -> when(randomTargetLevelMode.get()) {
                "Single" -> x + randomTargetPosSingleLevelModeAmount!!
                "SymmetricalDistribution" -> when {
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> x + randomTargetPosSymmetricalDistributionLevelModeP1Amount!!
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> x + randomTargetPosSymmetricalDistributionLevelModeP2Amount!!
                    else -> x
                }
                else -> x
            }
            "Random" -> when(randomTargetLevelMode.get()) {
                "Single" -> x + randomTargetPosSingleLevelModeValue!!
                "SymmetricalDistribution" -> when {
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> x + randomTargetPosSymmetricalDistributionLevelModePart1Value!!
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> x + randomTargetPosSymmetricalDistributionLevelModePart2Value!!
                    else -> x
                }
                else -> x
            }
            else -> x
        }

        val baseY = when(randomTargetPosMode.get()) {
            "Perlin" -> when(randomTargetLevelMode.get()) {
                "Single" -> y + randomTargetPosSingleLevelModeAmount!!
                "SymmetricalDistribution" -> when {
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> y + randomTargetPosSymmetricalDistributionLevelModeP1Amount!!
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> y + randomTargetPosSymmetricalDistributionLevelModeP2Amount!!
                    else -> y
                }
                else -> y
            }
            "Random" -> when(randomTargetLevelMode.get()) {
                "Single" -> y + randomTargetPosSingleLevelModeValue!!
                "SymmetricalDistribution" -> when {
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> y + randomTargetPosSymmetricalDistributionLevelModePart1Value!!
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> y + randomTargetPosSymmetricalDistributionLevelModePart2Value!!
                    else -> y
                }
                else -> y
            }
            else -> y
        }

        val baseZ = when(randomTargetPosMode.get()) {
            "Perlin" -> when(randomTargetLevelMode.get()) {
                "Single" -> z + randomTargetPosSingleLevelModeAmount!!
                "SymmetricalDistribution" -> when {
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> z + randomTargetPosSymmetricalDistributionLevelModeP1Amount!!
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> z + randomTargetPosSymmetricalDistributionLevelModeP2Amount!!
                    else -> z
                }
                else -> z
            }
            "Random" -> when(randomTargetLevelMode.get()) {
                "Single" -> z + randomTargetPosSingleLevelModeValue!!
                "SymmetricalDistribution" -> when {
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIRate.get()) -> z + randomTargetPosSymmetricalDistributionLevelModePart1Value!!
                    probability(randomTargetPosSymmetricalDistributionLevelModePartIIRate.get()) -> z + randomTargetPosSymmetricalDistributionLevelModePart2Value!!
                    else -> z
                }
                else -> z
            }
            else -> z
        }
        return if (randomTargetPos.get()) Vec3(baseX,baseY,baseZ) else Vec3(x,y,z)
    }
    private fun attack(it:EntityPlayer){
        val event = AttackEvent(it)
        if (!allowAttackWhenNotBlocking.get() || !mc.thePlayer.isBlocking) {
            if (callAttackEvent.get()) LiquidBounce.eventManager.callEvent(event)
            if (attackTargetEntityWithCurrentItem.get()) mc.thePlayer.attackTargetEntityWithCurrentItem(it)
            if (swingMode.get() == "SwingItem") mc.thePlayer.swingItem() else if (swingMode.get() == "C0A") mc.netHandler.addToSendQueue(C0APacketAnimation())
            if (attackMode.get() == "C02") mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))
            else if (attackMode.get() == "KeyBindAttack") mc.gameSettings.keyBindAttack.pressed = true
            if (debug.get()) ChatPrint("[Aura]Attack")
        }
    }
    private fun getSpeedValue(it: EntityPlayer, minSpeed: Float, maxSpeed: Float): Double {
        val min = minSpeed.toDouble()
        val max = if (extraReverseDeflectionRate.get() != 0 && minSpeed < 0 &&
            (!reverseDeflectionAllowedOnlyOutside.get() || !hitable(it, currentRange)) &&
            probability(extraReverseDeflectionRate.get())) 0.0 else maxSpeed.toDouble()
        return if (randomSpeedValue.get()) randomDouble(min, max) else 0.0
    }
    private var YawDataIndex = 0
    private var PitchDataIndex = 0
    var SData = false
    var yaw = 0f
    var pitch = 0f

    private fun simulationYaw(currentYaw: Float, targetYaw: Float,currentPitch: Float, targetPitch: Float) {
        when (smoothMode.get()) {
            "DataSimulationA" -> {
                val closestStartYaw = findClosestValue(YawData, MathHelper.wrapAngleTo180_float(currentYaw).toDouble())
                val closestEndYaw = findClosestValue(YawData, MathHelper.wrapAngleTo180_float(targetYaw).toDouble())
                val startIndexYaw = YawData.indexOf(closestStartYaw)
                val endIndexYaw = YawData.indexOf(closestEndYaw)
                val resultYaw = if (startIndexYaw <= endIndexYaw) YawData.subList(startIndexYaw, endIndexYaw + 1) else YawData.subList(endIndexYaw, startIndexYaw + 1).reversed()
                if (YawDataIndex < resultYaw.size) {
                    YawDataIndex++
                    if (rotateValue.get()) yaw = resultYaw[YawDataIndex].toFloat()  else  mc.thePlayer.rotationYaw = resultYaw[YawDataIndex].toFloat()
                }
                if (YawDataIndex >= resultYaw.size) YawDataIndex = 0

                val closestStartPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(currentPitch).toDouble())
                val closestEndPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(targetPitch).toDouble())
                val startIndexPitch = PitchData.indexOf(closestStartPitch)
                val endIndexPitch = PitchData.indexOf(closestEndPitch)
                val resultPitch = if (startIndexPitch <= endIndexPitch) PitchData.subList(startIndexPitch, endIndexPitch + 1)  else PitchData.subList(endIndexPitch, startIndexPitch + 1).reversed()
                if (PitchDataIndex < resultPitch.size) {
                    PitchDataIndex++
                    if (rotateValue.get()) pitch = resultPitch[PitchDataIndex].toFloat() else   mc.thePlayer.rotationPitch = resultPitch[PitchDataIndex].toFloat()
                }
                if (PitchDataIndex >= resultPitch.size) PitchDataIndex = 0
            }
            "DataSimulationB" -> {
                val closestStartYaw = findClosestValue(YawData2, MathHelper.wrapAngleTo180_float(currentYaw).toDouble())
                val closestEndYaw = findClosestValue(YawData2, MathHelper.wrapAngleTo180_float(targetYaw).toDouble())
                val startIndexYaw = YawData2.indexOf(closestStartYaw)
                val endIndexYaw = YawData2.indexOf(closestEndYaw)
                val resultYaw = if (startIndexYaw <= endIndexYaw) YawData2.subList(startIndexYaw, endIndexYaw + 1) else YawData2.subList(endIndexYaw, startIndexYaw + 1).reversed()
                if (YawDataIndex < resultYaw.size) {
                    YawDataIndex++
                    if (rotateValue.get()) yaw = resultYaw[YawDataIndex].toFloat()  else    mc.thePlayer.rotationYaw = resultYaw[YawDataIndex].toFloat()
                }
                if (YawDataIndex >= resultYaw.size) YawDataIndex = 0

                val closestStartPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(currentPitch).toDouble())
                val closestEndPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(targetPitch).toDouble())
                val startIndexPitch = PitchData.indexOf(closestStartPitch)
                val endIndexPitch = PitchData.indexOf(closestEndPitch)
                val resultPitch = if (startIndexPitch <= endIndexPitch) PitchData.subList(startIndexPitch, endIndexPitch + 1)  else PitchData.subList(endIndexPitch, startIndexPitch + 1).reversed()
                if (PitchDataIndex < resultPitch.size) {
                    PitchDataIndex++
                    if (rotateValue.get()) pitch = resultPitch[PitchDataIndex].toFloat() else   mc.thePlayer.rotationPitch = resultPitch[PitchDataIndex].toFloat()
                }
                if (PitchDataIndex >= resultPitch.size) PitchDataIndex = 0
            }
        }
    }
    private fun smoothYaw(current: Float, target: Float, speed: Float): Float {
        when (smoothMode.get()) {
            "Slerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * speed
            }
            "AdaptiveBezier" -> {
                val t = speed / 10f
                val p0 = adaptiveBezierP0.get()
                val p1 = adaptiveBezierP1.get()
                val p2 = adaptiveBezierP2.get()
                val p3 = adaptiveBezierP3.get()
                val p4 = adaptiveBezierP4.get()
                val p5 = adaptiveBezierP5.get()
                val p6 = adaptiveBezierP6.get()
                val p7 = adaptiveBezierP7.get()

                val factor = (1 - t).pow(7) * p0 +
                        7 * (1 - t).pow(6) * t * p1 +
                        21 * (1 - t).pow(5) * t.pow(2) * p2 +
                        35 * (1 - t).pow(4) * t.pow(3) * p3 +
                        35 * (1 - t).pow(3) * t.pow(4) * p4 +
                        21 * (1 - t).pow(2) * t.pow(5) * p5 +
                        7 * (1 - t) * t.pow(6) * p6 +
                        t.pow(7) * p7
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val distance = Math.abs(delta)
                val maxDelta = factor
                val deltaTime = Math.min(distance, maxDelta)
                return current + deltaTime * Math.signum(delta)
            }
            "AdaptiveSlerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val distance = Math.abs(delta)
                val smoothFactor = Math.pow(distance / 180.0, 2.0)
                val deltaTime = Math.min(smoothFactor.toFloat() * speed, distance)
                return current + deltaTime * Math.signum(delta)
            }
            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
            }
            "CosineInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = (1 - Math.cos(Math.PI * speed)).toFloat() * 0.5f
                return current + delta * factor
            }
            "ElasticSpring" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val elasticity = elasticity.get()
                val damping = dampingFactor2.get()
                val factor = Math.exp((-elasticity * speed).toDouble()) * Math.cos(damping * speed * Math.PI).toFloat()
                return current + delta * factor.toFloat()
            }
            "Bezier" -> {
                val t = speed / 10f
                val p0 = bezierP0.get()
                val p1 = bezierP1.get()
                val p2 = bezierP2.get()
                val p3 = bezierP3.get()
                val p4 = bezierP4.get()
                val p5 = bezierP5.get()
                val p6 = bezierP6.get()
                val p7 = bezierP7.get()
                val factor = (1 - t).pow(7) * p0 +
                        7 * (1 - t).pow(6) * t * p1 +
                        21 * (1 - t).pow(5) * t.pow(2) * p2 +
                        35 * (1 - t).pow(4) * t.pow(3) * p3 +
                        35 * (1 - t).pow(3) * t.pow(4) * p4 +
                        21 * (1 - t).pow(2) * t.pow(5) * p5 +
                        7 * (1 - t) * t.pow(6) * p6 +
                        t.pow(7) * p7
                return current + (MathHelper.wrapAngleTo180_float(target - current)) * factor
            }
            "Custom" ->{
                return customCode(current,target, speed).toFloat()
            }
            else -> return target
        }
    }

    private fun smoothPitch(current: Float, target: Float, speed: Float): Float {
        when (smoothMode.get()) {
            "Slerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * speed
            }
            "AdaptiveBezier" -> {
                val t = speed / 10f
                val p0 = adaptiveBezierP0.get()
                val p1 = adaptiveBezierP1.get()
                val p2 = adaptiveBezierP2.get()
                val p3 = adaptiveBezierP3.get()
                val p4 = adaptiveBezierP4.get()
                val p5 = adaptiveBezierP5.get()
                val p6 = adaptiveBezierP6.get()
                val p7 = adaptiveBezierP7.get()

                val factor = (1 - t).pow(7) * p0 +
                        7 * (1 - t).pow(6) * t * p1 +
                        21 * (1 - t).pow(5) * t.pow(2) * p2 +
                        35 * (1 - t).pow(4) * t.pow(3) * p3 +
                        35 * (1 - t).pow(3) * t.pow(4) * p4 +
                        21 * (1 - t).pow(2) * t.pow(5) * p5 +
                        7 * (1 - t) * t.pow(6) * p6 +
                        t.pow(7) * p7
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val distance = Math.abs(delta)
                val maxDelta = factor
                val deltaTime = Math.min(distance, maxDelta)
                return current + deltaTime * Math.signum(delta)
            }
            "AdaptiveSlerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val distance = Math.abs(delta)
                val smoothFactor = Math.pow(distance / 180.0, 2.0)
                val deltaTime = Math.min(smoothFactor.toFloat() * speed, distance)
                return current + deltaTime * Math.signum(delta)
            }
            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
            }
            "CosineInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = (1 - Math.cos(Math.PI * speed)).toFloat() * 0.5f
                return current + delta * factor
            }
            "LogarithmicInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.log((1 + speed).toDouble()).toFloat()
                return current + delta * factor
            }
            "ElasticSpring" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val elasticity = elasticity.get()
                val damping = dampingFactor2.get()
                val factor = Math.exp((-elasticity * speed).toDouble()) * Math.cos(damping * speed * Math.PI).toFloat()
                return current + delta * factor.toFloat()
            }
            "Bezier" -> {
                val t = speed / 10f
                val p0 = bezierP0.get()
                val p1 = bezierP1.get()
                val p2 = bezierP2.get()
                val p3 = bezierP3.get()
                val p4 = bezierP4.get()
                val p5 = bezierP5.get()
                val p6 = bezierP6.get()
                val p7 = bezierP7.get()
                val factor = (1 - t).pow(7) * p0 +
                        7 * (1 - t).pow(6) * t * p1 +
                        21 * (1 - t).pow(5) * t.pow(2) * p2 +
                        35 * (1 - t).pow(4) * t.pow(3) * p3 +
                        35 * (1 - t).pow(3) * t.pow(4) * p4 +
                        21 * (1 - t).pow(2) * t.pow(5) * p5 +
                        7 * (1 - t) * t.pow(6) * p6 +
                        t.pow(7) * p7
                return current + (MathHelper.wrapAngleTo180_float(target - current)) * factor
            }
            "Custom" ->{
                return customCode(current,target, speed).toFloat()
            }
            else -> return target
        }
    }
}