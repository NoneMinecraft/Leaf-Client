//All the code was written by N0ne.
package net.nonemc.leaf.features.module.modules.combat

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
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.event.*
import net.nonemc.leaf.libs.packet.PacketText.chatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.combat.aura.code.customCode
import net.nonemc.leaf.features.module.modules.combat.aura.data.PitchData
import net.nonemc.leaf.features.module.modules.combat.aura.data.YawData
import net.nonemc.leaf.features.module.modules.combat.aura.data.YawData2
import net.nonemc.leaf.features.module.modules.combat.aura.invoke.*
import net.nonemc.leaf.features.module.modules.combat.aura.utils.*
import net.nonemc.leaf.features.module.modules.misc.AntiBot.isBot
import net.nonemc.leaf.libs.neuralnetwork.getNetwork
import net.nonemc.leaf.font.Fonts
import net.nonemc.leaf.libs.extensions.getDistanceToEntityBox
import net.nonemc.leaf.libs.random.randomDouble
import net.nonemc.leaf.libs.random.randomInt
import net.nonemc.leaf.libs.random.randomLong
import net.nonemc.leaf.libs.entity.EntityTypeLib
import net.nonemc.leaf.libs.entity.EntityTypeLib.isFriend
import net.nonemc.leaf.libs.raycast.Raycast
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.libs.rotation.fixRotation
import net.nonemc.leaf.libs.rotation.getRotationTo
import net.nonemc.leaf.libs.rotation.getRotationToVec
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.*

@ModuleInfo(name = "Aura", category = ModuleCategory.COMBAT)
object Aura : Module() {
    //Attack
    private val searchRange = FloatValue("SearchRange",4F, 0F, 6F)
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
    private val swingMode = ListValue("SwingMode", arrayOf("SwingItem", "C0A", "None"), "SwingItem").displayable { attack.get() }
    private val attackMode = ListValue("AttackMode", arrayOf("C02", "KeyBindAttack", "None"), "C02").displayable { attack.get() }
    private val callAttackEvent = BoolValue("CallAttackEvent", true).displayable { attack.get() }
    private val attackTargetEntityWithCurrentItem = BoolValue("AttackTargetEntityWithCurrentItem", true).displayable { attack.get() }
    private val allowAttackWhenNotBlocking = BoolValue("AllowAttackWhenNotBlocking", false).displayable { attack.get() }

    //AutoBlock
    private val autoBlockMode = ListValue("AutoBlockMode", arrayOf("C08", "KeyBind", "Animation", "None"), "C08")
    private val autoBlockTrigger = ListValue("AutoBlockTrigger", arrayOf("Range-Always", "Always", "Range-Delay"), "Range-Always").displayable { autoBlockMode.get() != "None" }
    private val autoBlockDelayValue = IntegerValue("AutoBlockDelay", 50, 1, 1000).displayable { autoBlockMode.get() != "None" }
    private val autoBlockRange = FloatValue("AutoBlockMaxRange", 3F, 0F, 6F).displayable { autoBlockMode.get() != "None" }
    private val autoBlockDetectsHeldItemAreOnlySwords = BoolValue("AutoBlockDetectsHeldItemAreOnlySwords", true).displayable { autoBlockMode.get() != "None" }
    private val autoBlockDetectionGUIIsNullOnly = BoolValue("AutoBlockDetectionGUIIsNullOnly", true).displayable { autoBlockMode.get() != "None" }

    //Visibility
    private val visibilityDetection = BoolValue("VisibilityDetection", true)
    val visibilityDetectionEntityBoundingBox = BoolValue("VisibilityDetectionEntityBoundingBox", true).displayable { visibilityDetection.get() }
    private val visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue = BoolValue("VisibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue", true).displayable { visibilityDetection.get() }
    private val maxThroughWallsRange = FloatValue("MaxThroughWallsRange", 3F, 0F, 6F).displayable { !visibilityDetection.get() }
    private val minThroughWallsRange = FloatValue("MinThroughWallsRange", 3F, 0F, 6F).displayable { !visibilityDetection.get() }


    private val rotationMode = ListValue(
        "RotationMode",
        arrayOf("Smooth",
            "DataSimulationA",
            "DataSimulationB",
            "GlobalDataSimulation",
            "NeuralNetworkGlobalDataSimulation",
            "BasicSimulation",
            "AdvancedSimulation",
            "None"),
        "Smooth"
    )
    private val smoothMode = ListValue(
        "SmoothMode",
        arrayOf(
            "None",
            "Slerp",
            "AdaptiveBezier",
            "AdaptiveSlerp",
            "Sinusoidal",
            "Spring",
            "CosineInterpolation",
            "LogarithmicInterpolation",
            "ElasticSpring",
            "Bezier",
            "Custom"),
        "Slerp").displayable { rotationMode.get() == "Smooth" }

    val rotateValue = BoolValue("SilentRotate", false).displayable { rotationMode.get() != "None" }
    val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 10, 0, 20).displayable { rotateValue.get() && rotationMode.get() != "None" }
    val customSmoothCode = TextValue("CustomSmoothCode", "ifelseifelseifelseifelse").displayable { smoothMode.get() == "Custom" }
    private val randomSpeedValue = BoolValue("RandomSpeed", true).displayable { rotationMode.get() != "None" }
    private val randomSpeedFrequency = IntegerValue("RandomSpeedFrequency", 1, 1, 10).displayable { randomSpeedValue.get() && rotationMode.get() != "None" }


    private val neuralNetworkGlobalDataSimulationPoint = IntegerValue("NeuralNetworkGlobalDataSimulation-Point", 7, 1, 180)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationSpeed = FloatValue("NeuralNetworkGlobalDataSimulationSpeed", 0.2f, 0.01f, 1f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationBaseSpeed = FloatValue("NeuralNetworkGlobalDataSimulation-BaseSpeed", 180f, 50f, 360f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationSpeedMultLarge = FloatValue("NeuralNetworkGlobalDataSimulation-SpeedMult-Large", 1.8f, 1.0f, 3.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationSpeedMultMedium = FloatValue("NeuralNetworkGlobalDataSimulation-SpeedMult-Medium", 1.2f, 0.8f, 2.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationSpeedMultSmall = FloatValue("NeuralNetworkGlobalDataSimulation-SpeedMult-Small", 0.8f, 0.5f, 1.5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationBaseAccel = FloatValue("NeuralNetworkGlobalDataSimulation-BaseAcceleration", 720f, 300f, 1200f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAccelMultLarge = FloatValue("NeuralNetworkGlobalDataSimulation-AccelMult-Large", 2.0f, 1.0f, 4.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAccelMultMedium = FloatValue("NeuralNetworkGlobalDataSimulation-AccelMult-Medium", 1.5f, 0.8f, 3.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAccelMultSmall = FloatValue("NeuralNetworkGlobalDataSimulation-AccelMult-Small", 1.0f, 0.5f, 2.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationPBase = FloatValue("NeuralNetworkGlobalDataSimulation-PID-P-Base", 1.8f, 0f, 50.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationPAttenuation = FloatValue("NeuralNetworkGlobalDataSimulation-PID-P-Attenuation", 0.6f, 0f, 50.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationIBase = FloatValue("NeuralNetworkGlobalDataSimulation-PID-I-Base", 0.08f, 0f, 50f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationIAttenuation = FloatValue("NeuralNetworkGlobalDataSimulation-PID-I-Attenuation", 0.9f, 0f, 50.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDBase = FloatValue("NeuralNetworkGlobalDataSimulation-PID-D-Base", 0.3f, 0f, 50.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDAttenuation = FloatValue("NeuralNetworkGlobalDataSimulation-PID-D-Attenuation", 0.8f, 0.5f, 50.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationJitterHigh = FloatValue("NeuralNetworkGlobalDataSimulation-Jitter-High", 1.8f, 0.0f, 3.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationJitterMedium = FloatValue("NeuralNetworkGlobalDataSimulation-Jitter-Medium", 1.2f, 0.0f, 2.5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationJitterLow = FloatValue("NeuralNetworkGlobalDataSimulation-Jitter-Low", 0.6f, 0.0f, 2.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDampingFactor = FloatValue("NeuralNetworkGlobalDataSimulation-Damping", 0.2f, 0.0f, 0.5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationFatigueDecay = FloatValue("NeuralNetworkGlobalDataSimulation-FatigueDecay", 0.97f, 0.9f, 1.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationFatigueRecover = FloatValue("NeuralNetworkGlobalDataSimulation-FatigueRecover", 0.4f, 0.1f, 0.9f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationOvershootDecay = FloatValue("NeuralNetworkGlobalDataSimulation-OvershootDecay", 0.6f, 0.3f, 1.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationOvershootThreshold = FloatValue("NeuralNetworkGlobalDataSimulation-OvershootThreshold", 0.2f, 0.05f, 0.5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationFineTuningThreshold = FloatValue("NeuralNetworkGlobalDataSimulation-FineTuningThreshold", 3f, 1f, 10f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationMicroJitter = FloatValue("NeuralNetworkGlobalDataSimulation-MicroJitter", 0.3f, 0.1f, 1.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAdjustMinDelta = FloatValue("NeuralNetworkGlobalDataSimulation-AdjustMinDelta", 10f, 0f, 180f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAdjustMaxDelta = FloatValue("NeuralNetworkGlobalDataSimulation-AdjustMaxDelta", 20f, 0f, 180f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAdjustTime = IntegerValue("NeuralNetworkGlobalDataSimulation-AdjustTime", 1000, 1, 5000)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAdjustMaxRandom = FloatValue("NeuralNetworkGlobalDataSimulation-AdjustMaxRandom", 0.5f, -5f, 5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationAdjustMinRandom = FloatValue("NeuralNetworkGlobalDataSimulation-AdjustMinRandom", -0.5f, -5f, 5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationPDynamicGain = FloatValue("NeuralNetworkGlobalDataSimulation-P-DynamicGain", 1.5f, 0.5f, 3.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationPNonlinearity = FloatValue("NeuralNetworkGlobalDataSimulation-P-Nonlinearity", 2.4f, 1.0f, 5.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationPResponseCurve = FloatValue("NeuralNetworkGlobalDataSimulation-P-ResponseCurve", 0.7f, 0.3f, 1.5f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationIAdaptiveThreshold = FloatValue("NeuralNetworkGlobalDataSimulation-I-AdaptiveThreshold", 15f, 5f, 30f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationINonlinearDecay = FloatValue("NeuralNetworkGlobalDataSimulation-I-NonlinearDecay", 0.85f, 0.5f, 0.95f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationIAntiWindup = FloatValue("NeuralNetworkGlobalDataSimulation-I-AntiWindup", 0.4f, 0.1f, 0.9f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationIDynamicClamp = FloatValue("NeuralNetworkGlobalDataSimulation-I-DynamicClamp", 50f, 10f, 100f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDNoiseFilter = FloatValue("NeuralNetworkGlobalDataSimulation-D-NoiseFilter", 0.6f, 0.3f, 0.9f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDNonlinearBoost = FloatValue("NeuralNetworkGlobalDataSimulation-D-NonlinearBoost", 1.8f, 1.0f, 3.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDErrorSensitivity = FloatValue("NeuralNetworkGlobalDataSimulation-D-ErrorSensitivity", 0.4f, 0.1f, 1.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationErrorDeadzone = FloatValue("NeuralNetworkGlobalDataSimulation-ErrorDeadzone", 0.5f, 0.1f, 2.0f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationErrorSaturation = FloatValue("NeuralNetworkGlobalDataSimulation-ErrorSaturation", 45f, 30f, 90f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationDynamicCoupling = FloatValue("NeuralNetworkGlobalDataSimulation-DynamicCoupling", 0.7f, 0.3f, 1.2f)
        .displayable { rotationMode.get() == "NeuralNetworkGlobalDataSimulation" }
    private val neuralNetworkGlobalDataSimulationLastDerivativeValue = FloatValue("NeuralNetworkGlobalDataSimulation-LastDerivativeValue", 1f, 0f, 90f)


    private val basicSimulationSpeed = FloatValue("BasicSimulationSpeed", 0.2f, 0.01f, 1f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationBaseSpeed = FloatValue("BasicSimulation-BaseSpeed", 180f, 50f, 360f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationSpeedMultLarge = FloatValue("BasicSimulation-SpeedMult-Large", 1.8f, 1.0f, 3.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationSpeedMultMedium = FloatValue("BasicSimulation-SpeedMult-Medium", 1.2f, 0.8f, 2.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationSpeedMultSmall = FloatValue("BasicSimulation-SpeedMult-Small", 0.8f, 0.5f, 1.5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationBaseAccel = FloatValue("BasicSimulation-BaseAcceleration", 720f, 300f, 1200f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAccelMultLarge = FloatValue("BasicSimulation-AccelMult-Large", 2.0f, 1.0f, 4.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAccelMultMedium = FloatValue("BasicSimulation-AccelMult-Medium", 1.5f,0.8f, 3.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAccelMultSmall = FloatValue("BasicSimulation-AccelMult-Small", 1.0f, 0.5f, 2.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationPBase = FloatValue("BasicSimulation-PID-P-Base", 1.8f, 0f, 50.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationPAttenuation = FloatValue("BasicSimulation-PID-P-Attenuation", 0.6f, 0f, 50.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationIBase = FloatValue("BasicSimulation-PID-I-Base", 0.08f, 0f, 50f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationIAttenuation = FloatValue("BasicSimulation-PID-I-Attenuation", 0.9f, 0f, 50.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDBase = FloatValue("BasicSimulation-PID-D-Base", 0.3f, 0f, 50.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDAttenuation = FloatValue("BasicSimulation-PID-D-Attenuation", 0.8f, 0.5f, 50.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationJitterHigh = FloatValue("BasicSimulation-Jitter-High", 1.8f, 0.0f, 3.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationJitterMedium = FloatValue("BasicSimulation-Jitter-Medium", 1.2f, 0.0f, 2.5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationJitterLow = FloatValue("BasicSimulation-Jitter-Low", 0.6f, 0.0f, 2.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDampingFactor = FloatValue("BasicSimulation-Damping", 0.2f, 0.0f, 0.5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationFatigueDecay = FloatValue("BasicSimulation-FatigueDecay", 0.97f, 0.9f, 1.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationFatigueRecover = FloatValue("BasicSimulation-FatigueRecover", 0.4f, 0.1f, 0.9f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationOvershootDecay = FloatValue("BasicSimulation-OvershootDecay", 0.6f, 0.3f, 1.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationOvershootThreshold = FloatValue("BasicSimulation-OvershootThreshold", 0.2f, 0.05f, 0.5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationFineTuningThreshold = FloatValue("BasicSimulation-FineTuningThreshold", 3f, 1f, 10f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationMicroJitter = FloatValue("BasicSimulation-MicroJitter", 0.3f, 0.1f, 1.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAdjustMinDelta = FloatValue("BasicSimulation-AdjustMinDelta", 10f, 0f, 180f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAdjustMaxDelta = FloatValue("BasicSimulation-AdjustMaxDelta", 20f, 0f, 180f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAdjustTime = IntegerValue("BasicSimulation-AdjustTime", 1000, 1, 5000).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAdjustMaxRandom = FloatValue("BasicSimulation-AdjustMaxRandom", 0.5f, -5f, 5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationAdjustMinRandom = FloatValue("BasicSimulation-AdjustMinRandom", -0.5f, -5f, 5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationPDynamicGain = FloatValue("BasicSimulation-P-DynamicGain", 1.5f, 0.5f, 3.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationPNonlinearity = FloatValue("BasicSimulation-P-Nonlinearity", 2.4f, 1.0f, 5.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationPResponseCurve = FloatValue("BasicSimulation-P-ResponseCurve", 0.7f, 0.3f, 1.5f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationIAdaptiveThreshold = FloatValue("BasicSimulation-I-AdaptiveThreshold", 15f, 5f, 30f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationINonlinearDecay = FloatValue("BasicSimulation-I-NonlinearDecay", 0.85f, 0.5f, 0.95f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationIAntiWindup = FloatValue("BasicSimulation-I-AntiWindup", 0.4f, 0.1f, 0.9f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationIDynamicClamp = FloatValue("BasicSimulation-I-DynamicClamp", 50f, 10f, 100f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDNoiseFilter = FloatValue("BasicSimulation-D-NoiseFilter", 0.6f, 0.3f, 0.9f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDNonlinearBoost = FloatValue("BasicSimulation-D-NonlinearBoost", 1.8f, 1.0f, 3.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDErrorSensitivity = FloatValue("BasicSimulation-D-ErrorSensitivity", 0.4f, 0.1f, 1.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationErrorDeadzone = FloatValue("BasicSimulation-ErrorDeadzone", 0.5f, 0.1f, 2.0f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationErrorSaturation = FloatValue("BasicSimulation-ErrorSaturation", 45f, 30f, 90f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationDynamicCoupling = FloatValue("BasicSimulation-DynamicCoupling", 0.7f, 0.3f, 1.2f).displayable { rotationMode.get() == "BasicSimulation" }
    private val basicSimulationLastDerivativeValue = FloatValue("BasicSimulation-LastDerivativeValue", 1f, 0f, 90f)


    private val globalDataSimulationSpeed = FloatValue("GlobalDataSimulationSpeed", 0.2f, 0.01f, 1f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationBaseSpeed = FloatValue("GlobalDataSimulation-BaseSpeed", 180f, 50f, 360f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationSpeedMultLarge = FloatValue("GlobalDataSimulation-SpeedMult-Large", 1.8f, 1.0f, 3.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationSpeedMultMedium = FloatValue("GlobalDataSimulation-SpeedMult-Medium", 1.2f, 0.8f, 2.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationSpeedMultSmall = FloatValue("GlobalDataSimulation-SpeedMult-Small", 0.8f, 0.5f, 1.5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationBaseAccel = FloatValue("GlobalDataSimulation-BaseAcceleration", 720f, 300f, 1200f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAccelMultLarge = FloatValue("GlobalDataSimulation-AccelMult-Large", 2.0f, 1.0f, 4.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAccelMultMedium = FloatValue("GlobalDataSimulation-AccelMult-Medium", 1.5f, 0.8f, 3.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAccelMultSmall = FloatValue("GlobalDataSimulation-AccelMult-Small", 1.0f, 0.5f, 2.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }

    private val globalDataSimulationPBase = FloatValue("GlobalDataSimulation-PID-P-Base", 1.8f, 0f, 50.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationPAttenuation = FloatValue("GlobalDataSimulation-PID-P-Attenuation", 0.6f, 0f, 50.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationIBase = FloatValue("GlobalDataSimulation-PID-I-Base", 0.08f, 0f, 50f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationIAttenuation = FloatValue("GlobalDataSimulation-PID-I-Attenuation", 0.9f, 0f, 50.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDBase = FloatValue("GlobalDataSimulation-PID-D-Base", 0.3f, 0f, 50.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDAttenuation = FloatValue("GlobalDataSimulation-PID-D-Attenuation", 0.8f, 0.5f, 50.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }

    private val globalDataSimulationJitterHigh = FloatValue("GlobalDataSimulation-Jitter-High", 1.8f, 0.0f, 3.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationJitterMedium = FloatValue("GlobalDataSimulation-Jitter-Medium", 1.2f, 0.0f, 2.5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationJitterLow = FloatValue("GlobalDataSimulation-Jitter-Low", 0.6f, 0.0f, 2.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDampingFactor = FloatValue("GlobalDataSimulation-Damping", 0.2f, 0.0f, 0.5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationFatigueDecay = FloatValue("GlobalDataSimulation-FatigueDecay", 0.97f, 0.9f, 1.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationFatigueRecover = FloatValue("GlobalDataSimulation-FatigueRecover", 0.4f, 0.1f, 0.9f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationOvershootDecay = FloatValue("GlobalDataSimulation-OvershootDecay", 0.6f, 0.3f, 1.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationOvershootThreshold = FloatValue("GlobalDataSimulation-OvershootThreshold", 0.2f, 0.05f, 0.5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationFineTuningThreshold = FloatValue("GlobalDataSimulation-FineTuningThreshold", 3f, 1f, 10f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationMicroJitter = FloatValue("GlobalDataSimulation-MicroJitter", 0.3f, 0.1f, 1.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAdjustMinDelta = FloatValue("GlobalDataSimulation-AdjustMinDelta", 10f, 0f, 180f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAdjustMaxDelta = FloatValue("GlobalDataSimulation-AdjustMaxDelta", 20f, 0f, 180f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAdjustTime = IntegerValue("GlobalDataSimulation-AdjustTime", 1000, 1, 5000)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAdjustMaxRandom = FloatValue("GlobalDataSimulation-AdjustMaxRandom", 0.5f, -5f, 5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationAdjustMinRandom = FloatValue("GlobalDataSimulation-AdjustMinRandom", -0.5f, -5f, 5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationPDynamicGain = FloatValue("GlobalDataSimulation-P-DynamicGain", 1.5f, 0.5f, 3.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationPNonlinearity = FloatValue("GlobalDataSimulation-P-Nonlinearity", 2.4f, 1.0f, 5.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationPResponseCurve = FloatValue("GlobalDataSimulation-P-ResponseCurve", 0.7f, 0.3f, 1.5f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationIAdaptiveThreshold = FloatValue("GlobalDataSimulation-I-AdaptiveThreshold", 15f, 5f, 30f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationINonlinearDecay = FloatValue("GlobalDataSimulation-I-NonlinearDecay", 0.85f, 0.5f, 0.95f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationIAntiWindup = FloatValue("GlobalDataSimulation-I-AntiWindup", 0.4f, 0.1f, 0.9f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationIDynamicClamp = FloatValue("GlobalDataSimulation-I-DynamicClamp", 50f, 10f, 100f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDNoiseFilter = FloatValue("GlobalDataSimulation-D-NoiseFilter", 0.6f, 0.3f, 0.9f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDNonlinearBoost = FloatValue("GlobalDataSimulation-D-NonlinearBoost", 1.8f, 1.0f, 3.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDErrorSensitivity = FloatValue("GlobalDataSimulation-D-ErrorSensitivity", 0.4f, 0.1f, 1.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationErrorDeadzone = FloatValue("GlobalDataSimulation-ErrorDeadzone", 0.5f, 0.1f, 2.0f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationErrorSaturation = FloatValue("GlobalDataSimulation-ErrorSaturation", 45f, 30f, 90f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationDynamicCoupling = FloatValue("GlobalDataSimulation-DynamicCoupling", 0.7f, 0.3f, 1.2f)
        .displayable { rotationMode.get() == "GlobalDataSimulation" }
    private val globalDataSimulationLastDerivativeValue = FloatValue("GlobalDataSimulation-LastDerivativeValue", 1f, 0f, 90f)

    private var globalDataSimulationLastUpdateTime: Long = 0L
    private var globalDataSimulationLastError: Float = 0f
    private var globalDataSimulationIntegral: Float = 0f

    private var basicSimulationLastUpdateTime: Long = 0L
    private var basicSimulationLastError: Float = 0f
    private var basicSimulationIntegral: Float = 0f

    private var neuralNetworkGlobalDataSimulationLastUpdateTime: Long = 0L
    private var neuralNetworkGlobalDataSimulationLastError: Float = 0f
    private var neuralNetworkGlobalDataSimulationIntegral: Float = 0f

    private val advancedSimulationSpeed = FloatValue("AdvancedSimulationSpeed", 0.2f, 0.01f, 1f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationBaseSpeed = FloatValue("AdvancedSimulation-BaseSpeed", 180f, 50f, 360f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationSpeedMultLarge = FloatValue("AdvancedSimulation-SpeedMult-Large", 1.8f, 1.0f, 3.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationSpeedMultMedium = FloatValue("AdvancedSimulation-SpeedMult-Medium", 1.2f, 0.8f, 2.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationSpeedMultSmall = FloatValue("AdvancedSimulation-SpeedMult-Small", 0.8f, 0.5f, 1.5f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationBaseAccel = FloatValue("AdvancedSimulation-BaseAcceleration", 720f, 300f, 1200f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationAccelMultLarge = FloatValue("AdvancedSimulation-AccelMult-Large", 2.0f, 1.0f, 4.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationAccelMultMedium = FloatValue("AdvancedSimulation-AccelMult-Medium", 1.5f,0.8f, 3.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationAccelMultSmall = FloatValue("AdvancedSimulation-AccelMult-Small", 1.0f, 0.5f, 2.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationPBase = FloatValue("AdvancedSimulation-PID-P-Base", 1.8f, 0.5f, 3.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationPAttenuation = FloatValue("AdvancedSimulation-PID-P-Attenuation", 0.6f, 0.1f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationIBase = FloatValue("AdvancedSimulation-PID-I-Base", 0.08f, 0.01f, 0.2f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationIAttenuation = FloatValue("AdvancedSimulation-PID-I-Attenuation", 0.9f, 0.5f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationDBase = FloatValue("AdvancedSimulation-PID-D-Base", 0.3f, 0.1f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationDAttenuation = FloatValue("AdvancedSimulation-PID-D-Attenuation", 0.8f, 0.5f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationJitterHigh = FloatValue("AdvancedSimulation-Jitter-High", 1.8f, 0.0f, 3.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationJitterMedium = FloatValue("AdvancedSimulation-Jitter-Medium", 1.2f, 0.0f, 2.5f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationJitterLow = FloatValue("AdvancedSimulation-Jitter-Low", 0.6f, 0.0f, 2.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationDampingFactor = FloatValue("AdvancedSimulation-Damping", 0.2f, 0.0f, 0.5f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationFatigueDecay = FloatValue("AdvancedSimulation-FatigueDecay", 0.97f, 0.9f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationFatigueRecover = FloatValue("AdvancedSimulation-FatigueRecover", 0.4f, 0.1f, 0.9f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationOvershootDecay = FloatValue("AdvancedSimulation-OvershootDecay", 0.6f, 0.3f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationOvershootThreshold = FloatValue("AdvancedSimulation-OvershootThreshold", 0.2f, 0.05f, 0.5f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationFineTuningThreshold = FloatValue("AdvancedSimulation-FineTuningThreshold", 3f, 1f, 10f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationMicroJitter = FloatValue("AdvancedSimulation-MicroJitter", 0.3f, 0.1f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationSpeedCurve = ListValue("AdvancedSimulation-SpeedCurve", arrayOf("Linear", "Exponential", "Sigmoid"), "Sigmoid").displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationAccelStages = ListValue("AdvancedSimulation-AccelStages", arrayOf("Original", "Dual", "Triple"), "Triple").displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationAccelThresholds = FloatValue("AdvancedSimulation-Accel-Thresholds", 45f, 15f, 90f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationDecelMode = ListValue("AdvancedSimulation-Decel-Mode", arrayOf("Original", "Quadratic", "CustomCurve"), "CustomCurve").displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationMuscleMemory = FloatValue("AdvancedSimulation-MuscleMemory", 0.7f, 0.0f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationTremorFrequency = FloatValue("AdvancedSimulation-TremorFreq", 8.0f, 1.0f, 20.0f).displayable { rotationMode.get() == "AdvancedSimulation" }
    private val advancedSimulationOcularFixation = FloatValue("AdvancedSimulation-OcularFixation", 0.4f, 0.1f, 1.0f).displayable { rotationMode.get() == "AdvancedSimulation" }


    private val slerpSpeed = FloatValue("SlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Slerp" && rotationMode.get() == "Smooth" }
    private val slerpRandomMinSpeed = FloatValue("Slerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" && rotationMode.get() == "Smooth" }
    private val slerpRandomMaxSpeed = FloatValue("Slerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" && rotationMode.get() == "Smooth" }
    private val slerpDecayRate = FloatValue("Slerp-DecayRate", 0.85f, 0.5f, 1f).displayable { smoothMode.get() == "Slerp" && rotationMode.get() == "Smooth" }
    private val microAdjustBoost = FloatValue("Slerp-MicroBoost", 1.5f, 1.0f, 3.0f).displayable { smoothMode.get() == "Slerp" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierSpeed = FloatValue("AdaptiveBezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth"}
    private val adaptiveBezierRandomMinSpeed = FloatValue("AdaptiveBezier-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierRandomMaxSpeed = FloatValue("AdaptiveBezier-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveSlerpSpeed = FloatValue("AdaptiveSlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "AdaptiveSlerp" && rotationMode.get() == "Smooth" }
    private val adaptiveSlerpRandomMinSpeed = FloatValue("AdaptiveSlerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveSlerp" &&rotationMode.get() == "Smooth" }
    private val adaptiveSlerpRandomMaxSpeed = FloatValue("AdaptiveSlerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "AdaptiveSlerp" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP0 = FloatValue("AdaptiveBezier-P0", 0f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP1 = FloatValue("AdaptiveBezier-P1", 0.05f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP2 = FloatValue("AdaptiveBezier-P2", 0.2f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP3 = FloatValue("AdaptiveBezier-P3", 0.4f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP4 = FloatValue("AdaptiveBezier-P4", 0.6f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP5 = FloatValue("AdaptiveBezier-P5", 0.8f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP6 = FloatValue("AdaptiveBezier-P6", 0.95f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val adaptiveBezierP7 = FloatValue("AdaptiveBezier-P7", 1f, 0f, 1f).displayable { smoothMode.get() == "AdaptiveBezier" && rotationMode.get() == "Smooth" }
    private val dampingSpeed = FloatValue("DampingSpeed", 0.5f, 0.01f, 1f).displayable { smoothMode.get() == "Damping" && rotationMode.get() == "Smooth" }
    private val dampingRandomMinSpeed = FloatValue("Damping-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" && rotationMode.get() == "Smooth" }
    private val dampingRandomMaxSpeed = FloatValue("Damping-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" && rotationMode.get() == "Smooth" }
    private val sinusoidalSpeed = FloatValue("SinusoidalTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Sinusoidal" && rotationMode.get() == "Smooth" }
    private val sinusoidalRandomMinSpeed = FloatValue("Sinusoidal-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" && rotationMode.get() == "Smooth" }
    private val sinusoidalRandomMaxSpeed = FloatValue("Sinusoidal-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" && rotationMode.get() == "Smooth" }

    private val springSpeed = FloatValue("SpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Spring" && rotationMode.get() == "Smooth" }
    private val springRandomMinSpeed = FloatValue("Spring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" && rotationMode.get() == "Smooth" }
    private val springRandomMaxSpeed = FloatValue("Spring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" && rotationMode.get() == "Smooth" }

    private val cosineInterpolationSpeed = FloatValue("CosineInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "CosineInterpolation" && rotationMode.get() == "Smooth" }
    private val cosineInterpolationRandomMinSpeed = FloatValue("CosineInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation" && rotationMode.get() == "Smooth" }
    private val cosineInterpolationRandomMaxSpeed = FloatValue("CosineInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation" && rotationMode.get() == "Smooth" }
    private val logarithmicInterpolationSpeed = FloatValue("LogarithmicInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "LogarithmicInterpolation" && rotationMode.get() == "Smooth" }
    private val logarithmicInterpolationRandomMinSpeed = FloatValue("LogarithmicInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation" && rotationMode.get() == "Smooth" }
    private val logarithmicInterpolationRandomMaxSpeed = FloatValue("LogarithmicInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation" && rotationMode.get() == "Smooth" }

    private val elasticSpringSpeed = FloatValue("ElasticSpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "ElasticSpring" && rotationMode.get() == "Smooth" }
    private val elasticSpringRandomMinSpeed = FloatValue("ElasticSpring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" && rotationMode.get() == "Smooth" }
    private val elasticSpringRandomMaxSpeed = FloatValue("ElasticSpring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" && rotationMode.get() == "Smooth" }
    private val bezierSpeed = FloatValue("BezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() != "None" }
    private val bezierRandomMinSpeed = FloatValue("Bezier-RandomMinTurnSpeed", -0.1f, -5f, 10f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierRandomMaxSpeed = FloatValue("Bezier-RandomMaxTurnSpeed", 0.1f, -5f, 10f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP0 = FloatValue("Bezier-P0", 0f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP1 = FloatValue("Bezier-P1", 0.05f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP2 = FloatValue("Bezier-P2", 0.2f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP3 = FloatValue("Bezier-P3", 0.4f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP4 = FloatValue("Bezier-P4", 0.6f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP5 = FloatValue("Bezier-P5", 0.8f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP6 = FloatValue("Bezier-P6", 0.95f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val bezierP7 = FloatValue("Bezier-P7", 1f, 0f, 1f).displayable { smoothMode.get() == "Bezier" && rotationMode.get() == "Smooth" }
    private val elasticity = FloatValue("ElasticSpring-Elasticity", 0.3f, 0.01f, 1f).displayable { smoothMode.get() == "ElasticSpring" && rotationMode.get() == "Smooth" }
    private val dampingFactor2 = FloatValue("ElasticSpring-DampingFactor", 0.5f, 0.01f, 1f).displayable { smoothMode.get() == "ElasticSpring" && rotationMode.get() == "Smooth" }

    private val noFacingYaw = BoolValue("NoFacingYaw", true).displayable { rotationMode.get() != "None" }
    private val noFacingYawOnlyPlayerMove = BoolValue("NoFacingYawOnlyPlayerMove", true).displayable { noFacingYaw.get() && rotationMode.get() != "None" }

    private val noFacingYawMaxRange = FloatValue("NoFacingYawMaxRange", 1F, 0F, 6F).displayable { noFacingYaw.get() && rotationMode.get() != "None" }
    private val noFacingPitch = BoolValue("NoFacingPitch", true).displayable { rotationMode.get() != "None" }
    private val noFacingPitchOnlyPlayerMove = BoolValue("NoFacingOnlyPlayerMove", true).displayable { noFacingPitch.get() && rotationMode.get() != "None" }
    private val noFacingPitchMaxRange = FloatValue("NoFacingMaxRange", 1F, 0F, 6F).displayable { noFacingPitch.get() && rotationMode.get() != "None" }
    private val reverseDeflectionAllowedOnlyOutside = BoolValue("ReverseDeflectionAllowedOnlyOutside", true).displayable { rotationMode.get() != "None" }
    private val extraReverseDeflectionRate = IntegerValue("ExtraReverseDeflectionRate", 50, 0, 100).displayable { rotationMode.get() != "None" }
    private val StationaryAccelerateSpeed = FloatValue("WhenTargetStationaryAccelerateSpeed", 0.1f, 0.0f, 1f).displayable { rotationMode.get() != "None" }

    //Jitter
    private val pitchJitter = BoolValue("PitchJitter", true).displayable { rotationMode.get() != "None" }
    private val pitchJitterRandomMode = ListValue("PitchJitterRandomMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchJitterAmount = FloatValue("PerlinNoiseRandomPitchJitterAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchJitterPerlinNoiseMinSeed = IntegerValue("PerlinNoiseRandomPitchJitterMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchJitterPerlinNoiseMaxSeed = IntegerValue("PerlinNoiseRandomPitchJitterMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchMinValue = FloatValue("RandomPitchMinValue", -1f, -5f, 5f).displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val randomPitchMaxValue = FloatValue("RandomPitchMaxValue", 1f, -5f, 5f).displayable { rotationMode.get() != "None" && pitchJitter.get() }
    private val yawJitter = BoolValue("YawJitter", true).displayable { rotationMode.get() != "None" }
    private val yawJitterRandomMode = ListValue("YawJitterRandomMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawJitterAmount = FloatValue("PerlinNoiseRandomYawJitterAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawJitterPerlinNoiseMinSeed = IntegerValue("PerlinNoiseRandomYawJitterMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawJitterPerlinNoiseMaxSeed = IntegerValue("PerlinNoiseRandomYawJitterMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawMinValue = FloatValue("RandomYawMinValue", -1f, -5f, 5f).displayable { rotationMode.get() != "None" && yawJitter.get() }
    private val randomYawMaxValue = FloatValue("RandomYawMaxValue", 1f, -5f, 5f).displayable { rotationMode.get() != "None" && yawJitter.get() }

    //RandomTargetPos
    private val randomTargetPos = BoolValue("RandomTargetPos", true).displayable { rotationMode.get() != "None" }
    private val randomTargetPosOnlyOutside = BoolValue("RandomTargetPosOnlyOutside", true).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val minRandomTargetPosHurtTime = IntegerValue("MinRandomTargetPosHurtTime", 0, 0, 10).displayable { randomTargetPos.get() }
    private val maxRandomTargetPosHurtTime = IntegerValue("MaxRandomTargetPosHurtTime", 0, 0, 10).displayable { randomTargetPos.get() }
    private val randomTargetPosFrequency = IntegerValue("RandomTargetPosFrequency", 500, 0, 1000).displayable { randomTargetPos.get() }
    private val randomTargetPosMode = ListValue("RandomTargetPosMode", arrayOf("Random", "Perlin"), "Perlin").displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetLevelMode = ListValue("RandomTargetPosLevelMode", arrayOf("Single", "SymmetricalDistribution"), "Single").displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleAmount = FloatValue("PerlinNoiseRandomTargetPosSingleLevelModeAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleMinSeed = IntegerValue("PerlinNoiseRandomTargetPosSingleLevelModeMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSingleMaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSingleLevelModeMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP1Amount = FloatValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP1MinSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP1MaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP1Rate = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP2Amount = FloatValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIAmount", 2f, 0.01f, 50f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP2MinSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMinSeed", 1, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP2MaxSeed = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val perlinNoiseRandomTargetPosSymmetricalDistributionP2Rate = IntegerValue("PerlinNoiseRandomTargetPosSymmetricalDistributionLevelModePartIMaxSeed", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSingleMinValue = IntegerValue("RandomTargetPosSingleLevelModeMinValue", 1, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSingleMaxValue = IntegerValue("RandomTargetPosSingleLevelModeMaxValue", 10000, 1, 10000).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP1MinValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIMinValue", 0.5f, -1f, 1f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP1MaxValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIMaxValue", 0.2f, -1f, 1f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP1Rate = IntegerValue("RandomTargetPosSymmetricalDistributionLevelModePartIRate", 50, 0, 100).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP2MinValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIIMinValue", 0.5f, -1f, 1f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP2MaxValue = FloatValue("RandomTargetPosSymmetricalDistributionLevelModePartIIMaxValue", 0.2f, -1f, 1f).displayable { rotationMode.get() != "None" && randomTargetPos.get() }
    private val randomTargetPosSymmetricalDistributionP2Rate = IntegerValue("RandomTargetPosSymmetricalDistributionLevelModePartIIRate", 50, 0, 100).displayable { rotationMode.get() != "None" && randomTargetPos.get() }

    //Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable { raycastValue.get() }
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable { raycastValue.get() }

    //Predict
    private val predictValue = BoolValue("Predict", true)
    private val predictSize = FloatValue("PredictSize", 2f, 1f, 5f).displayable { predictValue.get() }

    //Other
    private val fixSensitivity = BoolValue("FixSensitivity", true)
    private val moveFixMode = ListValue("MoveFixMode", arrayOf("StrictStrafe", "SilentControl", "StrictControl", "SilentStrafe", "None"), "SilentControl").displayable { rotateValue.get() }
    private val targetMode = ListValue("TargetMode", arrayOf("Single", "Switch"), "Single")
    private val switchDelay = IntegerValue("SwitchDelay", 50, 0, 1000).displayable { targetMode.get() == "Switch" }
    private val playerPosYOffset = FloatValue("PlayerPosYOffset", 0F, -1F, 1F)
    private val targetPosYOffset = FloatValue("TargetPosYOffset", 0F, -1F, 1F)
    private val fov = FloatValue("FOV", 180f, 0f, 180f)
    private val fakeSwing = BoolValue("FakeSwing", true)
    private val noBadPackets = BoolValue("NoBadPackets", true)
    private val hitable = BoolValue("Hitable", true)
    private val sprint = BoolValue("Sprint", true)

    //Debug
    private val debug = BoolValue("Debug", false)
    private val cpsDebug = BoolValue("CPSDebug", false)
    private val renderDebug = BoolValue("RenderDebug", false)

    //Render
    private val circleValue = BoolValue("Circle", true)
    private val circleRealRange = BoolValue("CircleRealRange", true).displayable { circleValue.get() }
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() && !circleRealRange.get() }
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }

    private var fatigueFactor: Float = 1f
    private var speedValue = 0.0
    private var speedTick = 0
    var sprintValue = true //Sprint
    var strictStrafeValue = false //EntityLivingBase
    var displayBlocking: Boolean = false //MixinItemRenderer
    var blocking: Boolean = false //EntityPlayerSP
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
    private var randomPosVec = Vec3(0.0, 0.0, 0.0)
    private val randomPosTimer = MSTimer()
    private var foundTarget = false
    private var entityList: MutableList<EntityPlayer> = arrayListOf()
    private val switchDelayValue = MSTimer()
    private var YawDataIndex = 0
    private var PitchDataIndex = 0
    private val adTime = MSTimer()

    var yaw = 0f
    var pitch = 0f
    private var advancedSimulationLastUpdateTime = 0L
    private var advancedSimulationCurrentSpeed = 0f
    private var advancedSimulationIntegral = 0f
    private var advancedSimulationLastError = 0f
    private var advancedSimulationFatigueFactor = 1.0f
    private var advancedSimulationMuscleMemoryBias = 0f
    private var advancedSimulationTremorPhase = 0f

    private var randomTargetPosSingleNoise: Double? = null
    private var randomTargetPosSingleAmount: Double? = null
    private var randomTargetPosP1Noise: Double? = null
    private var randomTargetPosP1Amount: Double? = null
    private var randomTargetPosP2Noise: Double? = null
    private var randomTargetPosP2Amount: Double? = null
    private var randomTargetPosSingleValue: Double? = null
    private var randomTargetPosP1Value: Double? = null
    private var randomTargetPosP2Value: Double? = null

    private var bsSpeed: Float = 0f
    private var currentYaw = 0f
    private var currentPitch = 0f
    private var currentServerYaw = 0f
    private var currentServerPitch = 0f
    private var currentSpeed = 0.0f
    private var targetX: Double = 0.0
    private var targetY: Double = 0.0
    private var targetZ: Double = 0.0
    override fun onEnable() {
        if (debug.get()) {
            if (allowAttackWhenNotBlocking.get() && autoBlockMode.get() != "None") chatPrint("§0[§cError§0] §7Conflict settings:id(01)\n§7at Aura.allowAttackWhenNotBlocking(BoolValue)\n§7at Aura.autoBlockMode(ListValue)")
            if (visibilityDetection.get() && (maxThroughWallsRange.get() != 0.0F || minThroughWallsRange.get() != 0.0f)) chatPrint(
                "§0[§cError§0] §7Conflict settings:id(02)\n§7at Aura.visibilityDetection(BoolValue)\n§7at Aura.throughWallsRange(FloatValue)"
            )
            if (rotateValue.get() && attackMode.get() == "KeyBindAttack") chatPrint("§0[§cError§0] §7Invalid action:id(03)\n§7at Aura.rotateValue(BoolValue)\n§7at Aura.attackMode(ListValue)")
            if (rotationMode.get() == "None" && hitable.get()) chatPrint("§0[§eWarn§0] §7Potentially invalid action:id(04)\n§7at Aura.rotateMode(ListValue)\n§7at Aura.hitable(BoolValue)")
            if ((rotationMode.get() == "BasicSimulation" || rotationMode.get() == "AdvancedSimulation") && rotateValue.get()) chatPrint(
                "§0[§eWarn§0] §7Invalid Warning:id(05)\n§7at Aura.rotateMode(ListValue)\n§7at Aura.rotateValue(BoolValue)"
            )
        }
    }

    override fun onDisable() {
        lastYaw = true
        adTime.reset()
        blocking = false
        switchDelayValue.reset()
        randomPosTimer.reset()
        CPSTimer.reset()
        autoBlockDelay.reset()
        clickDelay.reset()
        rayCastedTarget = null
        foundTarget = false
        cpsUpdate = false
        allowStrictStrafe = false
        abreset = false
        sprintValue = true
        speedTick = 0
        strictStrafeValue = false
        resetMove()
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
                    (sin(i * Math.PI / 180.0).toFloat() * circleRange.get())
                )
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
        if (packet is C02PacketUseEntity) {
            if (CPSTimer.hasTimePassed(1000)) {
                if (cpsDebug.get()) chatPrint(cps.toString())
                cps = 0
                CPSTimer.reset()
            } else {
                cps++
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (renderDebug.get()) {
            val sr = ScaledResolution(mc)
            val text = "cr:$currentRange , ctr:$currentThroughWallsRange , speedR:$speedValue , TR:$speedTick" +
                    "cpsC:$cps , ssv:$strictStrafeValue  , RT:$rayCastedTarget" +
                    "sprint:${mc.thePlayer.isSprinting} , block:${mc.thePlayer.isBlocking} , eat:${mc.thePlayer.isEating} " +
                    "CYaw:${mc.thePlayer.rotationYaw} , CPitch:${mc.thePlayer.rotationPitch}" +
                    "abr:${autoBlockRange.get()} , YDN:$YawDataIndex , PDN:$PitchDataIndex"
            Fonts.minecraftFont.drawStringWithShadow(
                text, sr.scaledWidth / 2f - Fonts.minecraftFont.getStringWidth(text) / 2f,
                sr.scaledHeight / 2f - 60f, Color.blue.rgb
            )
        }
    }

    private fun setRotation(rotation: Rotation, it: EntityPlayer) {
        if (!noFacingYaw.get() || !hitable(it, noFacingYawMaxRange.get().toDouble()) || (noFacingYawOnlyPlayerMove.get() && !isPlayerMoving())
        ) {
            if (rotateValue.get()) {
                currentServerYaw =
                   if (fixSensitivity.get()) {
                       fixRotation(currentServerYaw, calculateYaw(currentServerYaw, rotation.yaw, currentSpeed))
                   } else {
                       calculateYaw(currentServerYaw, rotation.yaw, currentSpeed)
                   }
            } else {
                currentYaw =
                    if (fixSensitivity.get()) {
                        fixRotation(currentYaw, calculateYaw(currentYaw, rotation.yaw, currentSpeed))
                    } else {
                        calculateYaw(currentYaw, rotation.yaw, currentSpeed)
                    }
            }
        }
        if (!noFacingPitch.get() || !hitable(
                it,
                noFacingPitchMaxRange.get().toDouble()
            ) || (noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())
        ) {
            if (rotateValue.get()) {
                currentServerPitch =
                    if (fixSensitivity.get()) {
                        fixRotation(currentServerPitch, calculatePitch(currentServerPitch, rotation.pitch, currentSpeed))
                    } else {
                        calculatePitch(currentServerPitch, rotation.pitch, currentSpeed)
                    }
            } else {
                currentPitch =
                    if (fixSensitivity.get()) {
                        fixRotation(currentPitch, calculatePitch(currentPitch, rotation.pitch, currentSpeed))
                    } else {
                        calculatePitch(currentPitch, rotation.pitch, currentSpeed)
                    }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        setRange()
        val target = searchTarget()
        val x: Double
        val y: Double
        val z: Double
        target?.let {
            switchTarget(it)
            runAttack(player, it)
            setValue()
            runAutoBlock(player, it)
            randomSpeed(it)
            resetView()

            val result = visibility(player, it).second
            if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get()
                && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get()
            ) {
                x = result.xCoord
                y = result.yCoord
                z = result.zCoord
            } else {
                x = it.posX
                y = it.posY
                z = it.posZ
            }
            randomPosVec = updatePos(it, x, y, z)
            if (predictValue.get()) {
                targetX = randomPosVec.xCoord + (it.posX - it.prevPosX) * predictSize.get()
                targetY = randomPosVec.yCoord + targetPosYOffset.get() + (it.posY - it.prevPosY) * predictSize.get()
                targetZ = randomPosVec.zCoord + (it.posZ - it.prevPosZ) * predictSize.get()
            } else {
                targetX = randomPosVec.xCoord
                targetY = randomPosVec.yCoord + targetPosYOffset.get()
                targetZ = randomPosVec.zCoord
            }
            val targetVec = Vec3(targetX, targetY, targetZ)
            val playerVec = Vec3(player.posX, targetY + playerPosYOffset.get(), player.posZ)
            val rotation = getRotationTo(playerVec, targetVec)

            resetSpeed(it)
            setSpeed(it)
            setRotation(rotation, it)
            setJitter()

            if (noBadPackets.get() && ((!rotateValue.get() && (currentPitch < -90.0 || currentPitch > 90.0))
                        || (rotateValue.get() && (currentServerPitch < -90.0 || currentServerPitch > 90.0)))
            ) {
                if (debug.get()) chatPrint("[Aura]Blocked a bad packet: $currentYaw , $currentPitch , $currentServerYaw , $currentServerPitch")
                return
            }
            if (rotationMode.get() != "None") turn(currentYaw, currentPitch, currentServerYaw, currentServerPitch)
        } ?: run {
            lastYaw = true
            resetValue()
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val (yaw) = RotationBaseLib.targetRotation ?: return
        val currentYaw = mc.thePlayer.rotationYaw
        when (moveFixMode.get()) {
            "SilentControl" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) moveTo(yaw, currentYaw) else resetMove()
            }

            "StrictControl" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) moveTo(currentYaw, yaw) else resetMove()
            }

            "StrictStrafe" -> {
                if (rotateValue.get() && mc.thePlayer != null && allowStrictStrafe) {
                    strictStrafeValue = true
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
                        mc.thePlayer.motionX += strafe * MathHelper.cos((yaw * Math.PI / 180F).toFloat()) - forward * MathHelper.sin(
                            (yaw * Math.PI / 180F).toFloat()
                        )
                        mc.thePlayer.motionZ += forward * MathHelper.cos((yaw * Math.PI / 180F).toFloat()) + strafe * MathHelper.sin(
                            (yaw * Math.PI / 180F).toFloat()
                        )
                    }
                    event.cancelEvent()
                } else {
                    strictStrafeValue = false
                }
            }

            "SilentStrafe" -> {
                if (event.isCancelled) return
                var strafe = event.strafe
                var forward = event.forward
                var friction = event.friction
                var factor = strafe * strafe + forward * forward

                val angleDiff =
                    ((MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw - 22.5f - 135.0f) + 180.0) / (45.0).toDouble()).toInt()
                val calcYaw = yaw + 45.0f * angleDiff.toFloat()

                var calcMoveDir = abs(strafe).coerceAtLeast(abs(forward))
                calcMoveDir *= calcMoveDir
                val calcMultiplier = MathHelper.sqrt_float(calcMoveDir / 1.0f.coerceAtMost(calcMoveDir * 2.0f))

                when (angleDiff) {
                    1, 3, 5, 7, 9 -> {
                        if ((abs(forward) > 0.005 || abs(strafe) > 0.005) && !(abs(forward) > 0.005 && abs(
                                strafe
                            ) > 0.005)
                        ) {
                            friction /= calcMultiplier
                        } else if (abs(forward) > 0.005 && abs(strafe) > 0.005) {
                            friction *= calcMultiplier
                        }
                    }
                }

                if (factor >= 1.0E-4F) {
                    factor = MathHelper.sqrt_float(factor)

                    if (factor < 1.0F) {
                        factor = 1.0F
                    }

                    factor = friction / factor
                    strafe *= factor
                    forward *= factor

                    val yawSin = MathHelper.sin((calcYaw * Math.PI / 180F).toFloat())
                    val yawCos = MathHelper.cos((calcYaw * Math.PI / 180F).toFloat())

                    mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                    mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                }
                event.cancelEvent()
            }
        }
    }

    private fun attack(it: EntityPlayer) {
        val event = AttackEvent(it)
        if (!allowAttackWhenNotBlocking.get() || !mc.thePlayer.isBlocking) {
            if (callAttackEvent.get()) Leaf.eventManager.callEvent(event)
            if (attackTargetEntityWithCurrentItem.get()) mc.thePlayer.attackTargetEntityWithCurrentItem(it)
            if (swingMode.get() == "SwingItem") mc.thePlayer.swingItem() else if (swingMode.get() == "C0A") mc.netHandler.addToSendQueue(
                C0APacketAnimation()
            )
            if (attackMode.get() == "C02") mc.netHandler.addToSendQueue(
                C02PacketUseEntity(
                    it,
                    C02PacketUseEntity.Action.ATTACK
                )
            )
            else if (attackMode.get() == "KeyBindAttack") mc.gameSettings.keyBindAttack.pressed = true
            if (debug.get()) chatPrint("[Aura]Attack")
        }
    }

    private fun runAttack(player: EntityPlayer, it: EntityPlayer) {
        if (!hitable.get() || hitable(
                it,
                currentRange
            ) && attack.get() && it.hurtTime in minHurtTime.get()..maxHurtTime.get()
            && it.getDistanceToEntityBox(player) <= currentRange
        ) {
            if (clickDelay.hasTimePassed(randomLong(minAttackDelay.get().toLong(), maxAttackDelay.get().toLong()))) {
                clickDelay.reset()
                attack(it)
            }
        } else if (fakeSwing.get()) mc.thePlayer.swingItem()
    }

    private fun runAutoBlock(player: EntityPlayer, it: EntityPlayer) {
        val itemStack: ItemStack? = player.heldItem
        if ((autoBlockTrigger.get() == "Range-Always" && it.getDistanceToEntityBox(player) <= autoBlockRange.get()) || (autoBlockTrigger.get() == "Always") || (autoBlockTrigger.get() == "Range-Delay" && autoBlockDelay.hasTimePassed(
                autoBlockDelayValue.get().toLong()
            ))
        ) {
            autoBlockDelay.reset()
            if ((!autoBlockDetectsHeldItemAreOnlySwords.get() || (itemStack != null && itemStack.item is ItemSword)) && (!autoBlockDetectionGUIIsNullOnly.get() || mc.currentScreen == null)) {
                when (autoBlockMode.get()) {
                    "C08" -> {
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                        blocking = true
                        displayBlocking = true
                    }

                    "KeyBind" -> {
                        mc.gameSettings.keyBindUseItem.pressed = true
                    }

                    "Animation" -> {
                        blocking = false
                        displayBlocking = true
                    }

                    else -> {
                        abReset()
                        blocking = false
                        displayBlocking = false
                    }
                }
            } else abReset()
        } else abReset()
    }

    private fun searchTarget(): EntityPlayer? {
        val player = mc.thePlayer
        return run {
            var currentTarget: EntityPlayer? = null
            if (raycastValue.get()) {
                val rayCastedEntity = Raycast.raycastEntity(searchRange.get().toDouble()) {
                    (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand)
                            && (EntityTypeLib.isSelected(it, true) || raycastIgnoredValue.get()
                            && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
                }
                if (rayCastedEntity is EntityLivingBase && !isFriend(rayCastedEntity)) {
                    rayCastedTarget = rayCastedEntity
                    foundTarget = false
                }
            }

            for (entity in mc.theWorld.playerEntities) {
                if (entity == rayCastedTarget && entity is EntityPlayer && !isFriend(entity) && !isBot(entity) && (targetMode.get() == "Single" || !entityList.contains(
                        entity
                    )) && entity != player
                    && entity.getDistanceToEntityBox(player) <= searchRange.get()
                        .toDouble() && MathHelper.wrapAngleTo180_double(
                        RotationBaseLib.getRotationDifference(entity)
                    ) <= fov.get()
                    && EntityTypeLib.isSelected(entity, true)
                ) {
                    val (isVisible, _) = visibility(player, entity)
                    if ((!visibilityDetection.get() && !isVisible && entity.getDistanceToEntityBox(player) <= currentThroughWallsRange) || isVisible) {
                        currentTarget = rayCastedTarget as EntityPlayer?
                        foundTarget = true
                        break

                    }
                } else foundTarget = false
            }

            if (!foundTarget) {
                for (entity in mc.theWorld.playerEntities) {
                    if (entity is EntityPlayer && !isFriend(entity) && !isBot(entity) && (targetMode.get() == "Single" || !entityList.contains(
                            entity
                        )) && entity != player
                        && EntityTypeLib.isSelected(
                            entity,
                            true
                        ) && MathHelper.wrapAngleTo180_double(RotationBaseLib.getRotationDifference(entity)) <= fov.get()
                        && entity.getDistanceToEntityBox(player) <= searchRange.get().toDouble()
                    ) {
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
    }
    private fun switchTarget(it: EntityPlayer) {
        if (targetMode.get() == "Switch") {
            if (switchDelayValue.hasTimePassed(switchDelay.get().toLong())) {
                switchDelayValue.reset()
                entityList.add(it)
            }
        } else {
            entityList.clear()
        }
        entityList.add(it)
    }

    private fun setValue() {
        sprintValue = sprint.get()
        allowStrictStrafe = true
    }

    private fun resetView() {
        if (rotateValue.get()) {
            currentServerYaw = RotationBaseLib.serverRotation.yaw
            currentServerPitch = RotationBaseLib.serverRotation.pitch
        } else {
            currentYaw = mc.thePlayer.rotationYaw
            currentPitch = mc.thePlayer.rotationPitch
        }
    }

    private fun abReset() {
        mc.gameSettings.keyBindUseItem.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)
        abreset = false
        blocking = false
        displayBlocking = false
    }

    private fun resetSpeed(it: EntityPlayer) {
        if (speedValue < 0.0 && reverseDeflectionAllowedOnlyOutside.get() && hitable(it, currentRange)) speedValue = 0.0
    }

    private fun resetValue() {
        adTime.reset()
        allowStrictStrafe = false
        abReset()
        sprintValue = true
        foundTarget = false
    }

    private fun resetMove() {
        mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
        mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
        mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
    }

    private fun setRange() {
        val sprintReduceValue = if (mc.thePlayer.isSprinting) randomDouble(
            attackRangeSprintReduceMinValue.get().toDouble(),
            attackRangeSprintReduceMaxValue.get().toDouble()
        ) else 0.0
        val airReduceValue = if (!mc.thePlayer.onGround) randomDouble(
            attackRangeAirReduceMinValue.get().toDouble(),
            attackRangeAirReduceMaxValue.get().toDouble()
        ) else 0.0
        currentRange = randomDouble(
            minAttackRange.get().toDouble(),
            maxAttackRange.get().toDouble()
        ) - sprintReduceValue - airReduceValue
        currentThroughWallsRange =
            randomDouble(minThroughWallsRange.get().toDouble(), maxThroughWallsRange.get().toDouble())
    }

    private fun getSpeedValue(it: EntityPlayer, minSpeed: Float, maxSpeed: Float): Double {
        val min = minSpeed.toDouble()
        val max = if (extraReverseDeflectionRate.get() != 0 && minSpeed < 0 &&
            (!reverseDeflectionAllowedOnlyOutside.get() || !hitable(it, currentRange)) &&
            probability(extraReverseDeflectionRate.get())
        ) 0.0 else maxSpeed.toDouble()
        return if (randomSpeedValue.get()) randomDouble(min, max) else 0.0
    }

    private fun setJitter() {
        val rotationCoord =
            if (rotateValue.get()) getRotationToVec(currentServerYaw, currentServerPitch) else getRotationToVec(
                currentYaw,
                currentPitch
            )
        val yawNoiseValue = perlinNoise(
            rotationCoord.xCoord,
            0.0,
            rotationCoord.zCoord,
            randomInt(randomYawJitterPerlinNoiseMinSeed.get(), randomYawJitterPerlinNoiseMaxSeed.get())
        )
        val yawJitterAmount = yawNoiseValue * randomYawJitterAmount.get()
        val pitchNoiseValue = perlinNoise(
            0.0,
            rotationCoord.yCoord,
            0.0,
            randomInt(randomPitchJitterPerlinNoiseMinSeed.get(), randomPitchJitterPerlinNoiseMaxSeed.get())
        )
        val pitchJitterAmount = pitchNoiseValue * randomPitchJitterAmount.get()
        val jitterYawValue = if (yawJitter.get()) {
            when (yawJitterRandomMode.get()) {
                "Perlin" -> yawJitterAmount
                "Random" -> randomDouble(randomYawMinValue.get().toDouble(), randomYawMaxValue.get().toDouble())
                else -> 0.0
            }
        } else 0.0
        val jitterPitchValue = if (pitchJitter.get()) {
            when (pitchJitterRandomMode.get()) {
                "Perlin" -> pitchJitterAmount
                "Random" -> randomDouble(randomPitchMinValue.get().toDouble(), randomPitchMaxValue.get().toDouble())
                else -> 0.0
            }
        } else 0.0
        if (rotateValue.get()) {
            currentServerYaw += jitterYawValue.toFloat()
            currentServerPitch += jitterPitchValue.toFloat()
        } else {
            currentYaw += jitterYawValue.toFloat()
            currentPitch += jitterPitchValue.toFloat()
        }
    }

    private fun updatePos(it: EntityPlayer, x: Double, y: Double, z: Double): Vec3 {
        if ((randomPosTimer.hasTimePassed(randomTargetPosFrequency.get().toLong())
                    && it.hurtTime in minRandomTargetPosHurtTime.get()..maxRandomTargetPosHurtTime.get()
                    && (!randomTargetPosOnlyOutside.get() || hitable(it, currentRange)))
        ) {
            randomTargetPosSingleNoise = perlinNoise(
                x,
                y,
                z,
                randomInt(perlinNoiseRandomTargetPosSingleMinSeed.get(), perlinNoiseRandomTargetPosSingleMaxSeed.get())
            )
            randomTargetPosSingleAmount = randomTargetPosSingleNoise!! * perlinNoiseRandomTargetPosSingleAmount.get()
            randomTargetPosP1Noise = perlinNoise(
                x,
                y,
                z,
                randomInt(
                    perlinNoiseRandomTargetPosSymmetricalDistributionP1MinSeed.get(),
                    perlinNoiseRandomTargetPosSymmetricalDistributionP1MaxSeed.get()
                )
            )
            randomTargetPosP1Amount =
                randomTargetPosP1Noise!! * perlinNoiseRandomTargetPosSymmetricalDistributionP1Amount.get()
            randomTargetPosP2Noise = perlinNoise(
                x,
                y,
                z,
                randomInt(
                    perlinNoiseRandomTargetPosSymmetricalDistributionP2MinSeed.get(),
                    perlinNoiseRandomTargetPosSymmetricalDistributionP2MaxSeed.get()
                )
            )
            randomTargetPosP2Amount =
                randomTargetPosP2Noise!! * perlinNoiseRandomTargetPosSymmetricalDistributionP2Amount.get()
            randomTargetPosSingleValue = randomDouble(
                randomTargetPosSingleMinValue.get().toDouble(),
                randomTargetPosSingleMaxValue.get().toDouble()
            )
            randomTargetPosP1Value = randomDouble(
                randomTargetPosSymmetricalDistributionP1MinValue.get().toDouble(),
                randomTargetPosSymmetricalDistributionP1MaxValue.get().toDouble()
            )
            randomTargetPosP2Value = randomDouble(
                randomTargetPosSymmetricalDistributionP2MinValue.get().toDouble(),
                randomTargetPosSymmetricalDistributionP2MaxValue.get().toDouble()
            )
            randomPosTimer.reset()
        }
        return if (randomTargetPos.get()) Vec3(randomPosVec(x), randomPosVec(y), randomPosVec(z)) else Vec3(x, y, z)
    }

    private fun randomPosVec(value: Double): Double {
        return when (randomTargetPosMode.get()) {
            "Perlin" -> when (randomTargetLevelMode.get()) {
                "Single" -> value + randomTargetPosSingleAmount!!
                "SymmetricalDistribution" -> when {
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionP1Rate.get()) -> value + randomTargetPosP1Amount!!
                    probability(perlinNoiseRandomTargetPosSymmetricalDistributionP2Rate.get()) -> value + randomTargetPosP2Amount!!
                    else -> value
                }

                else -> value
            }

            "Random" -> when (randomTargetLevelMode.get()) {
                "Single" -> value + randomTargetPosSingleValue!!
                "SymmetricalDistribution" -> when {
                    probability(randomTargetPosSymmetricalDistributionP1Rate.get()) -> value + randomTargetPosP1Value!!
                    probability(randomTargetPosSymmetricalDistributionP2Rate.get()) -> value + randomTargetPosP2Value!!
                    else -> value
                }

                else -> value
            }

            else -> value
        }
    }
    var networkPoint = listOf(0f)
    private val directionThreshold = FloatValue("DirectionThreshold", 30f, 10f, 90f)
    private fun calculateOptimalStep(currentIndex: Int, targetIndex: Int, realDelta: Float): Int {
        val dataSize = YawData2.size
        val forwardSteps = (targetIndex - currentIndex + dataSize) % dataSize
        val backwardSteps = (currentIndex - targetIndex + dataSize) % dataSize

        return when {
            forwardSteps <= backwardSteps && abs(realDelta) < 180 - directionThreshold.get() -> 1
            forwardSteps > backwardSteps && abs(realDelta) > directionThreshold.get() -> -1
            realDelta > 0 -> if (forwardSteps < backwardSteps) 1 else -1
            else -> if (backwardSteps < forwardSteps) -1 else 1
        }
    }

private var lastYaw = true

    private fun calculateYaw(currentYaw: Float, targetYaw: Float, speed: Float): Float {
        when (rotationMode.get()) {
            "NeuralNetworkGlobalDataSimulation"->{
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val currentTime = System.currentTimeMillis()
                val dt = if (neuralNetworkGlobalDataSimulationLastUpdateTime == 0L) {
                    0.05f
                } else {
                    ((currentTime - neuralNetworkGlobalDataSimulationLastUpdateTime).coerceAtMost(100L) / 1000.0f).coerceAtLeast(0.001f)
                }
                val maxSpeed = neuralNetworkGlobalDataSimulationSpeed.get() * neuralNetworkGlobalDataSimulationBaseSpeed.get() * when {
                    abs(delta) > 90 -> neuralNetworkGlobalDataSimulationSpeedMultLarge.get()
                    abs(delta) > 30 -> neuralNetworkGlobalDataSimulationSpeedMultMedium.get()
                    else -> neuralNetworkGlobalDataSimulationSpeedMultSmall.get()
                }
                val acceleration = neuralNetworkGlobalDataSimulationBaseAccel.get() * when {
                    abs(delta) > 90 -> neuralNetworkGlobalDataSimulationAccelMultLarge.get()
                    abs(delta) > 30 -> neuralNetworkGlobalDataSimulationAccelMultMedium.get()
                    else -> neuralNetworkGlobalDataSimulationAccelMultSmall.get()
                }
                val error = when {
                    abs(delta) < neuralNetworkGlobalDataSimulationErrorDeadzone.get() -> 0f
                    abs(delta) > neuralNetworkGlobalDataSimulationErrorSaturation.get() -> neuralNetworkGlobalDataSimulationErrorSaturation.get() * delta.sign
                    else -> delta
                }
                val pNonlinear = 1 - exp(-abs(error) / (30 * neuralNetworkGlobalDataSimulationPResponseCurve.get()))
                val adaptiveP = neuralNetworkGlobalDataSimulationPBase.get() * (neuralNetworkGlobalDataSimulationPDynamicGain.get() +
                        neuralNetworkGlobalDataSimulationPNonlinearity.get() * pNonlinear)

                val decay = when {
                    abs(error) > neuralNetworkGlobalDataSimulationIAdaptiveThreshold.get() ->
                        1 - neuralNetworkGlobalDataSimulationINonlinearDecay.get() *
                                (abs(error) - neuralNetworkGlobalDataSimulationIAdaptiveThreshold.get()) / neuralNetworkGlobalDataSimulationIAdaptiveThreshold.get()
                    else -> 1f
                }
                neuralNetworkGlobalDataSimulationIntegral = (neuralNetworkGlobalDataSimulationIntegral * decay) +
                        (error * dt * 0.5f * (1 - neuralNetworkGlobalDataSimulationIAntiWindup.get() *
                                tanh(abs(neuralNetworkGlobalDataSimulationIntegral) / neuralNetworkGlobalDataSimulationIDynamicClamp.get())))
                neuralNetworkGlobalDataSimulationIntegral = neuralNetworkGlobalDataSimulationIntegral.coerceIn(-neuralNetworkGlobalDataSimulationIDynamicClamp.get(), neuralNetworkGlobalDataSimulationIDynamicClamp.get())

                val derivative = if (dt > 0) (error - neuralNetworkGlobalDataSimulationLastError) / dt else 0f
                val filteredDerivative = neuralNetworkGlobalDataSimulationDNoiseFilter.get() * derivative +
                        (1 - neuralNetworkGlobalDataSimulationDNoiseFilter.get()) * neuralNetworkGlobalDataSimulationLastDerivativeValue.get()
                val dBoost = 1 + neuralNetworkGlobalDataSimulationDNonlinearBoost.get() * tanh(abs(error) * neuralNetworkGlobalDataSimulationDErrorSensitivity.get())

                val p = adaptiveP * (1 - neuralNetworkGlobalDataSimulationPAttenuation.get() * exp(-abs(delta) / 30.0).toFloat())
                val i = neuralNetworkGlobalDataSimulationIBase.get() * (1 - neuralNetworkGlobalDataSimulationIAttenuation.get() * exp(-abs(delta) / 15.0).toFloat())
                val d = neuralNetworkGlobalDataSimulationDBase.get() * dBoost * (1 - neuralNetworkGlobalDataSimulationDAttenuation.get() * exp(-abs(delta) / 20.0).toFloat())

                val dynamicCoupling = 1 + neuralNetworkGlobalDataSimulationDynamicCoupling.get() *
                        (1 - exp(-(abs(bsSpeed) / 180.0).toFloat()))
                val output = ((p * error) + (i * neuralNetworkGlobalDataSimulationIntegral) + (d * filteredDerivative)) * dynamicCoupling

                val desiredSpeed = output.coerceIn(-maxSpeed, maxSpeed)
                val maxAcceleration = acceleration * dt
                val speedChange = (desiredSpeed - bsSpeed).coerceIn(-maxAcceleration, maxAcceleration)
                bsSpeed += speedChange

                val dampingFactor = 1 - neuralNetworkGlobalDataSimulationDampingFactor.get() * exp(-abs(error) / 45.0).toFloat()
                bsSpeed *= dampingFactor

                var newYaw = currentYaw + bsSpeed * dt

                val jitter = when {
                    abs(bsSpeed) > 120 -> (Math.random() - 0.5) * neuralNetworkGlobalDataSimulationJitterHigh.get()
                    abs(bsSpeed) > 60 -> (Math.random() - 0.5) * neuralNetworkGlobalDataSimulationJitterMedium.get()
                    else -> (Math.random() - 0.5) * neuralNetworkGlobalDataSimulationJitterLow.get()
                }

                newYaw += jitter.toFloat()

                val newDelta = MathHelper.wrapAngleTo180_float(targetYaw - newYaw)

                if (abs(newDelta) < abs(error) * neuralNetworkGlobalDataSimulationOvershootThreshold.get()) {
                    bsSpeed *= neuralNetworkGlobalDataSimulationOvershootDecay.get().coerceAtLeast(abs(newDelta) / 15.0f)
                }

                if (abs(newDelta) < neuralNetworkGlobalDataSimulationFineTuningThreshold.get()) {
                    newYaw += (Math.random().toFloat() - 0.5f) * neuralNetworkGlobalDataSimulationMicroJitter.get()
                    bsSpeed *= 0.2f
                }

                fatigueFactor = neuralNetworkGlobalDataSimulationFatigueDecay.get() * fatigueFactor +
                        (1 - neuralNetworkGlobalDataSimulationFatigueDecay.get()) *
                        (1 - neuralNetworkGlobalDataSimulationFatigueRecover.get() * tanh((dt * 0.5f).toDouble()).toFloat())
                bsSpeed *= fatigueFactor

                if (delta in neuralNetworkGlobalDataSimulationAdjustMinDelta.get()..neuralNetworkGlobalDataSimulationAdjustMaxDelta.get()) {
                    if (adTime.hasTimePassed(neuralNetworkGlobalDataSimulationAdjustTime.get().toLong())) {
                        newYaw = targetYaw + kotlin.random.Random.nextDouble(
                            neuralNetworkGlobalDataSimulationAdjustMinRandom.get().toDouble(),
                            neuralNetworkGlobalDataSimulationAdjustMaxRandom.get().toDouble()
                        ).toFloat()
                        adTime.reset()
                    }
                }

                neuralNetworkGlobalDataSimulationLastError = error
                neuralNetworkGlobalDataSimulationLastUpdateTime = currentTime
                val yawDirection = MathHelper.wrapAngleTo180_float(newYaw - currentYaw)

                val n = newYaw

                if (lastYaw) {
                    networkPoint = getNetwork(
                        MathHelper.wrapAngleTo180_float(currentYaw),
                        MathHelper.wrapAngleTo180_float(targetYaw),
                        neuralNetworkGlobalDataSimulationPoint.get()
                    )
                    lastYaw = false
                }



                val bestPointDiff =
                    if (d >= 0 && findNextSmallerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint) != null)
                        findNextSmallerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint)?.second
                    else if (d <= 0 && findNextLargerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint) != null)
                        findNextLargerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint)?.second
                    else 0f

                if (
                    ((findNextSmallerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint) == null) && d >= 0)
                    ||
                    ((findNextLargerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint) == null) && d <= 0)
                ) {
                    lastYaw = true
                }

                val bestPoint = n + MathHelper.wrapAngleTo180_float(bestPointDiff!!)
                val outputV = outputYaw(bestPoint)

                println("rwa:$n , 180rwa:${MathHelper.wrapAngleTo180_float(n)} network:$networkPoint , \n bestPointDiff:$bestPointDiff , \n bestPoint:$bestPoint , \n output:$outputV \n d:$delta\n out:$lastYaw " +
                        "\n bestS:${findNextSmallerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint)?.first} \n bestL:${findNextLargerPoint(MathHelper.wrapAngleTo180_float(n), networkPoint)?.first}")
                return bestPoint
            }
            "GlobalDataSimulation" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val currentTime = System.currentTimeMillis()
                val dt = if (globalDataSimulationLastUpdateTime == 0L) {
                    0.05f
                } else {
                    ((currentTime - globalDataSimulationLastUpdateTime).coerceAtMost(100L) / 1000.0f).coerceAtLeast(0.001f)
                }
                val maxSpeed = globalDataSimulationSpeed.get() * globalDataSimulationBaseSpeed.get() * when {
                    abs(delta) > 90 -> globalDataSimulationSpeedMultLarge.get()
                    abs(delta) > 30 -> globalDataSimulationSpeedMultMedium.get()
                    else -> globalDataSimulationSpeedMultSmall.get()
                }
                val acceleration = globalDataSimulationBaseAccel.get() * when {
                    abs(delta) > 90 -> globalDataSimulationAccelMultLarge.get()
                    abs(delta) > 30 -> globalDataSimulationAccelMultMedium.get()
                    else -> globalDataSimulationAccelMultSmall.get()
                }
                val error = when {
                    abs(delta) < globalDataSimulationErrorDeadzone.get() -> 0f
                    abs(delta) > globalDataSimulationErrorSaturation.get() -> globalDataSimulationErrorSaturation.get() * delta.sign
                    else -> delta
                }
                val pNonlinear = 1 - exp(-abs(error) / (30 * globalDataSimulationPResponseCurve.get()))
                val adaptiveP = globalDataSimulationPBase.get() * (globalDataSimulationPDynamicGain.get() +
                        globalDataSimulationPNonlinearity.get() * pNonlinear)

                val decay = when {
                    abs(error) > globalDataSimulationIAdaptiveThreshold.get() ->
                        1 - globalDataSimulationINonlinearDecay.get() *
                                (abs(error) - globalDataSimulationIAdaptiveThreshold.get()) / globalDataSimulationIAdaptiveThreshold.get()
                    else -> 1f
                }
                globalDataSimulationIntegral = (globalDataSimulationIntegral * decay) +
                        (error * dt * 0.5f * (1 - globalDataSimulationIAntiWindup.get() *
                                tanh(abs(globalDataSimulationIntegral) / globalDataSimulationIDynamicClamp.get())))
                globalDataSimulationIntegral = globalDataSimulationIntegral.coerceIn(-globalDataSimulationIDynamicClamp.get(), globalDataSimulationIDynamicClamp.get())

                val derivative = if (dt > 0) (error - globalDataSimulationLastError) / dt else 0f
                val filteredDerivative = globalDataSimulationDNoiseFilter.get() * derivative +
                        (1 - globalDataSimulationDNoiseFilter.get()) * globalDataSimulationLastDerivativeValue.get()
                val dBoost = 1 + globalDataSimulationDNonlinearBoost.get() * tanh(abs(error) * globalDataSimulationDErrorSensitivity.get())

                val p = adaptiveP * (1 - globalDataSimulationPAttenuation.get() * exp(-abs(delta) / 30.0).toFloat())
                val i = globalDataSimulationIBase.get() * (1 - globalDataSimulationIAttenuation.get() * exp(-abs(delta) / 15.0).toFloat())
                val d = globalDataSimulationDBase.get() * dBoost * (1 - globalDataSimulationDAttenuation.get() * exp(-abs(delta) / 20.0).toFloat())

                val dynamicCoupling = 1 + globalDataSimulationDynamicCoupling.get() *
                        (1 - exp(-(abs(bsSpeed) / 180.0).toFloat()))
                val output = ((p * error) + (i * globalDataSimulationIntegral) + (d * filteredDerivative)) * dynamicCoupling

                val desiredSpeed = output.coerceIn(-maxSpeed, maxSpeed)
                val maxAcceleration = acceleration * dt
                val speedChange = (desiredSpeed - bsSpeed).coerceIn(-maxAcceleration, maxAcceleration)
                bsSpeed += speedChange

                val dampingFactor = 1 - globalDataSimulationDampingFactor.get() * exp(-abs(error) / 45.0).toFloat()
                bsSpeed *= dampingFactor

                var newYaw = currentYaw + bsSpeed * dt

                val jitter = when {
                    abs(bsSpeed) > 120 -> (Math.random() - 0.5) * globalDataSimulationJitterHigh.get()
                    abs(bsSpeed) > 60 -> (Math.random() - 0.5) * globalDataSimulationJitterMedium.get()
                    else -> (Math.random() - 0.5) * globalDataSimulationJitterLow.get()
                }

                newYaw += jitter.toFloat()

                val newDelta = MathHelper.wrapAngleTo180_float(targetYaw - newYaw)

                if (abs(newDelta) < abs(error) * globalDataSimulationOvershootThreshold.get()) {
                    bsSpeed *= globalDataSimulationOvershootDecay.get().coerceAtLeast(abs(newDelta) / 15.0f)
                }

                if (abs(newDelta) < globalDataSimulationFineTuningThreshold.get()) {
                    newYaw += (Math.random().toFloat() - 0.5f) * globalDataSimulationMicroJitter.get()
                    bsSpeed *= 0.2f
                }

                fatigueFactor = globalDataSimulationFatigueDecay.get() * fatigueFactor +
                        (1 - globalDataSimulationFatigueDecay.get()) *
                        (1 - globalDataSimulationFatigueRecover.get() * tanh((dt * 0.5f).toDouble()).toFloat())
                bsSpeed *= fatigueFactor

                if (delta in globalDataSimulationAdjustMinDelta.get()..globalDataSimulationAdjustMaxDelta.get()) {
                    if (adTime.hasTimePassed(globalDataSimulationAdjustTime.get().toLong())) {
                        newYaw = targetYaw + kotlin.random.Random.nextDouble(
                            globalDataSimulationAdjustMinRandom.get().toDouble(),
                            globalDataSimulationAdjustMaxRandom.get().toDouble()
                        ).toFloat()
                        adTime.reset()
                    }
                }

                globalDataSimulationLastError = error
                globalDataSimulationLastUpdateTime = currentTime

                return outputYaw(newYaw)
            }

            "BasicSimulation" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val currentTime = System.currentTimeMillis()
                val dt = if (basicSimulationLastUpdateTime == 0L) {
                    0.05f
                } else {
                    ((currentTime - basicSimulationLastUpdateTime).coerceAtMost(100L) / 1000.0f).coerceAtLeast(0.001f)
                }
                val maxSpeed = basicSimulationSpeed.get() * basicSimulationBaseSpeed.get() * when {
                    abs(delta) > 90 -> basicSimulationSpeedMultLarge.get()
                    abs(delta) > 30 -> basicSimulationSpeedMultMedium.get()
                    else -> basicSimulationSpeedMultSmall.get()
                }
                val acceleration = basicSimulationBaseAccel.get() * when {
                    abs(delta) > 90 -> basicSimulationAccelMultLarge.get()
                    abs(delta) > 30 -> basicSimulationAccelMultMedium.get()
                    else -> basicSimulationAccelMultSmall.get()
                }
                val error = when {
                    abs(delta) < basicSimulationErrorDeadzone.get() -> 0f
                    abs(delta) > basicSimulationErrorSaturation.get() -> basicSimulationErrorSaturation.get() * delta.sign
                    else -> delta
                }
                val pNonlinear = 1 - exp(-abs(error) / (30 * basicSimulationPResponseCurve.get()))
                val adaptiveP = basicSimulationPBase.get() * (basicSimulationPDynamicGain.get() +
                        basicSimulationPNonlinearity.get() * pNonlinear)

                val decay = when {
                    abs(error) > basicSimulationIAdaptiveThreshold.get() ->
                        1 - basicSimulationINonlinearDecay.get() *
                                (abs(error) - basicSimulationIAdaptiveThreshold.get())/basicSimulationIAdaptiveThreshold.get()
                    else -> 1f
                }
                basicSimulationIntegral = (basicSimulationIntegral * decay) +
                        (error * dt * 0.5f * (1 - basicSimulationIAntiWindup.get() *
                                tanh(abs(basicSimulationIntegral)/basicSimulationIDynamicClamp.get())))
                basicSimulationIntegral = basicSimulationIntegral.coerceIn(-basicSimulationIDynamicClamp.get(), basicSimulationIDynamicClamp.get())


                val derivative = if (dt > 0) (error - basicSimulationLastError) / dt else 0f
                val filteredDerivative = basicSimulationDNoiseFilter.get() * derivative +
                        (1 - basicSimulationDNoiseFilter.get()) * basicSimulationLastDerivativeValue.get()
                val dBoost = 1 + basicSimulationDNonlinearBoost.get() * tanh(abs(error) * basicSimulationDErrorSensitivity.get())

                val p = adaptiveP * (1 - basicSimulationPAttenuation.get() * exp(-abs(delta)/30.0).toFloat())
                val i = basicSimulationIBase.get() * (1 - basicSimulationIAttenuation.get() * exp(-abs(delta)/15.0).toFloat())
                val d = basicSimulationDBase.get() * dBoost * (1 - basicSimulationDAttenuation.get() * exp(-abs(delta)/20.0).toFloat())

                val dynamicCoupling = 1 + basicSimulationDynamicCoupling.get() *
                        (1 - exp(-(abs(bsSpeed)/180.0).toFloat()))
                val output =
                    ((p * error) + (i * basicSimulationIntegral) + (d * filteredDerivative)) * dynamicCoupling

                val desiredSpeed = output.coerceIn(-maxSpeed, maxSpeed)
                val maxAcceleration = acceleration * dt
                val speedChange = (desiredSpeed - bsSpeed).coerceIn(-maxAcceleration, maxAcceleration)
                bsSpeed += speedChange

                val dampingFactor = 1 - basicSimulationDampingFactor.get() * exp(-abs(error)/45.0).toFloat()
                bsSpeed *= dampingFactor

                var newYaw = currentYaw + bsSpeed * dt

                val jitter = when {
                    abs(bsSpeed) > 120 -> (Math.random() - 0.5) * basicSimulationJitterHigh.get()
                    abs(bsSpeed) > 60 -> (Math.random() - 0.5) * basicSimulationJitterMedium.get()
                    else -> (Math.random() - 0.5) * basicSimulationJitterLow.get()
                }

                newYaw += jitter.toFloat()

                val newDelta = MathHelper.wrapAngleTo180_float(targetYaw - newYaw)

                if (abs(newDelta) < abs(error) * basicSimulationOvershootThreshold.get()) {
                    bsSpeed *= basicSimulationOvershootDecay.get().coerceAtLeast(abs(newDelta) / 15.0f)
                }

                if (abs(newDelta) < basicSimulationFineTuningThreshold.get()) {
                    newYaw += (Math.random().toFloat() - 0.5f) * basicSimulationMicroJitter.get()
                    bsSpeed *= 0.2f
                }

                fatigueFactor = basicSimulationFatigueDecay.get() * fatigueFactor +
                        (1 - basicSimulationFatigueDecay.get()) *
                        (1 - basicSimulationFatigueRecover.get() * tanh((dt * 0.5f).toDouble()).toFloat())
                bsSpeed *= fatigueFactor

                if (delta in basicSimulationAdjustMinDelta.get()..basicSimulationAdjustMaxDelta.get()) {
                    if (adTime.hasTimePassed(basicSimulationAdjustTime.get().toLong())) {
                        newYaw = targetYaw + kotlin.random.Random.nextDouble(
                            basicSimulationAdjustMinRandom.get().toDouble(),
                            basicSimulationAdjustMaxRandom.get().toDouble()
                        ).toFloat()
                        adTime.reset()
                    }
                }

                basicSimulationLastError = error
                basicSimulationLastUpdateTime = currentTime

                return newYaw
            }

            "AdvancedSimulation" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val currentTime = System.currentTimeMillis()
                val dt =
                    ((currentTime - advancedSimulationLastUpdateTime).coerceAtMost(100L) / 1000.0f).coerceAtLeast(0.001f)
                val baseSpeed = advancedSimulationSpeed.get()
                val maxSpeed = baseSpeed * advancedSimulationBaseSpeed.get() * when {
                    abs(delta) > 90 -> advancedSimulationSpeedMultLarge.get()
                    abs(delta) > 30 -> advancedSimulationSpeedMultMedium.get()
                    else -> advancedSimulationSpeedMultSmall.get()
                }
                val acceleration = when (advancedSimulationAccelStages.get()) {
                    "Triple" -> advancedSimulationBaseAccel.get() * when {
                        abs(delta) > advancedSimulationAccelThresholds.get() * 2 -> advancedSimulationAccelMultLarge.get()
                        abs(delta) > advancedSimulationAccelThresholds.get() -> advancedSimulationAccelMultMedium.get()
                        else -> advancedSimulationAccelMultSmall.get()
                    }

                    "Dual" -> advancedSimulationBaseAccel.get() * if (abs(delta) > advancedSimulationAccelThresholds.get())
                        advancedSimulationAccelMultLarge.get() else advancedSimulationAccelMultSmall.get()

                    else -> advancedSimulationBaseAccel.get() * when {
                        abs(delta) > 90 -> advancedSimulationAccelMultLarge.get()
                        abs(delta) > 30 -> advancedSimulationAccelMultMedium.get()
                        else -> advancedSimulationAccelMultSmall.get()
                    }
                }
                val speedMultiplier = when (advancedSimulationSpeedCurve.get()) {
                    "Exponential" -> 1 - exp(-abs(delta) / 90.0).toFloat()
                    "Sigmoid" -> 1 / (1 + exp(-(abs(delta) - 45) / 15.0).toFloat())
                    else -> abs(delta) / 180f
                }
                advancedSimulationMuscleMemoryBias =
                    advancedSimulationMuscleMemory.get() * advancedSimulationMuscleMemoryBias +
                            (1 - advancedSimulationMuscleMemory.get()) * (Math.random().toFloat() - 0.5f) * 2f
                val kP = advancedSimulationPBase.get() * (1 - advancedSimulationPAttenuation.get() *
                        exp(-abs(delta) / 30.0).toFloat())
                val kI = advancedSimulationIBase.get() * (1 - advancedSimulationIAttenuation.get() *
                        exp(-abs(delta) / 15.0).toFloat())
                val kD = advancedSimulationDBase.get() * (1 - advancedSimulationDAttenuation.get() *
                        exp(-abs(delta) / 20.0).toFloat())
                val error = delta + advancedSimulationMuscleMemoryBias
                advancedSimulationIntegral += error * dt * 0.5f
                advancedSimulationIntegral = advancedSimulationIntegral.coerceIn(-100f, 100f)
                val derivative = if (dt > 0) (error - advancedSimulationLastError) / dt else 0f
                val pidOutput = kP * error + kI * advancedSimulationIntegral + kD * derivative

                val desiredSpeed = pidOutput.coerceIn(-maxSpeed, maxSpeed) * speedMultiplier
                val maxAccel = when (advancedSimulationDecelMode.get()) {
                    "Quadratic" -> acceleration * dt * (1 - (abs(error) / 180f).pow(2))
                    "CustomCurve" -> acceleration * dt * exp(-(abs(error) / 90.0).pow(1.5)).toFloat()
                    else -> acceleration * dt
                }

                val speedChange = (desiredSpeed - advancedSimulationCurrentSpeed).coerceIn(-maxAccel, maxAccel)
                advancedSimulationCurrentSpeed += speedChange

                val dampingFactor = 1 - advancedSimulationDampingFactor.get() *
                        exp(-abs(error) / 45.0).toFloat()
                advancedSimulationCurrentSpeed *= dampingFactor

                val baseJitter = when {
                    abs(advancedSimulationCurrentSpeed) > 120 -> (Math.random() - 0.5) * advancedSimulationJitterHigh.get()
                    abs(advancedSimulationCurrentSpeed) > 60 -> (Math.random() - 0.5) * advancedSimulationJitterMedium.get()
                    else -> (Math.random() - 0.5) * advancedSimulationJitterLow.get()
                }
                advancedSimulationTremorPhase += dt * advancedSimulationTremorFrequency.get()
                val tremorJitter =
                    sin(advancedSimulationTremorPhase.toDouble()) * 0.3 * advancedSimulationTremorFrequency.get()
                        .toDouble()
                var newYaw =
                    currentYaw + advancedSimulationCurrentSpeed * dt + baseJitter.toFloat() + tremorJitter.toFloat()

                val remainingDelta = MathHelper.wrapAngleTo180_float(targetYaw - newYaw)
                if (abs(remainingDelta) < abs(error) * advancedSimulationOvershootThreshold.get()) {
                    advancedSimulationCurrentSpeed *= advancedSimulationOvershootDecay.get()
                        .coerceAtLeast(abs(remainingDelta) / 15.0f)
                }

                if (abs(remainingDelta) < advancedSimulationFineTuningThreshold.get()) {
                    val ocularCompensation = (1 - advancedSimulationOcularFixation.get()) *
                            (Math.random().toFloat() - 0.5f) * 0.3f
                    newYaw += advancedSimulationMicroJitter.get() * ocularCompensation
                    advancedSimulationCurrentSpeed *= 0.2f + 0.1f * Math.random().toFloat()
                }

                advancedSimulationFatigueFactor =
                    advancedSimulationFatigueDecay.get() * advancedSimulationFatigueFactor +
                            (1 - advancedSimulationFatigueDecay.get()) * (1 - advancedSimulationFatigueRecover.get() *
                            tanh((dt * 3f).toDouble()).toFloat())
                advancedSimulationCurrentSpeed *= advancedSimulationFatigueFactor.coerceIn(0.5f, 1.2f)

                advancedSimulationLastError = error
                advancedSimulationLastUpdateTime = currentTime

                return newYaw
            }

            "DataSimulationA" -> {
                val closestStartYaw =
                    findClosestValue(YawData, MathHelper.wrapAngleTo180_float(this.currentYaw).toDouble())
                val closestEndYaw = findClosestValue(YawData, MathHelper.wrapAngleTo180_float(targetYaw).toDouble())
                val startIndexYaw = YawData.indexOf(closestStartYaw)
                val endIndexYaw = YawData.indexOf(closestEndYaw)
                val resultYaw = if (startIndexYaw <= endIndexYaw) YawData.subList(
                    startIndexYaw,
                    endIndexYaw + 1
                ) else YawData.subList(endIndexYaw, startIndexYaw + 1).reversed()
                var newYaw = 0f
                if (YawDataIndex < resultYaw.size) {
                    YawDataIndex++
                    newYaw = resultYaw[YawDataIndex].toFloat()
                }
                if (YawDataIndex >= resultYaw.size) YawDataIndex = 0
                return newYaw
            }

            "DataSimulationB" -> {
                val currentWrapped = MathHelper.wrapAngleTo180_float(currentYaw)
                val targetWrapped = MathHelper.wrapAngleTo180_float(targetYaw)
                val realDelta = MathHelper.wrapAngleTo180_float(targetWrapped - currentWrapped)
                val currentIndex = YawData2.indexOf(findClosestValue(YawData2, currentWrapped.toDouble()))
                val targetIndex = YawData2.indexOf(findClosestValue(YawData2, targetWrapped.toDouble()))
                val step = calculateOptimalStep(currentIndex, targetIndex, realDelta)
                val newIndex = (currentIndex + step + YawData2.size) % YawData2.size
                val newYaw = YawData2[newIndex].toFloat()
                return currentYaw + MathHelper.wrapAngleTo180_float((newYaw - MathHelper.wrapAngleTo180_float(currentYaw)))

            }
        }
        when (smoothMode.get()) {
            "Slerp" -> {
                val rawDelta = targetYaw - currentYaw
                val delta = MathHelper.wrapAngleTo180_float(rawDelta)
                val baseSpeed = when {
                    abs(delta) > 150f -> speed * 0.3f
                    abs(delta) > 90f -> speed * 0.6f
                    abs(delta) > 45f -> speed * 0.8f
                    else -> speed * (microAdjustBoost.get() - abs(delta) / 100f)
                }
                val decayFactor = 1f / (1f + exp(-abs(delta) / 30f))
                val finalSpeed = baseSpeed * decayFactor * slerpDecayRate.get()
                return currentYaw + delta * finalSpeed
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
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val distance = abs(delta)
                val deltaTime = distance.coerceAtMost(factor)
                return currentYaw + deltaTime * sign(delta)
            }

            "AdaptiveSlerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val distance = abs(delta)
                val smoothFactor = (distance / 180.0).pow(2.0)
                val deltaTime = (smoothFactor.toFloat() * speed).coerceAtMost(distance)
                return currentYaw + deltaTime * sign(delta)
            }

            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val factor = sin((speed * Math.PI) / 2).toFloat()
                return currentYaw + delta * factor
            }

            "CosineInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val factor = (1 - cos(Math.PI * speed)).toFloat() * 0.5f
                return currentYaw + delta * factor
            }

            "ElasticSpring" -> {
                val delta = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)
                val elasticity = elasticity.get()
                val damping = dampingFactor2.get()
                val factor = exp((-elasticity * speed).toDouble()) * cos(damping * speed * Math.PI).toFloat()
                return currentYaw + delta * factor.toFloat()
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
                return currentYaw + (MathHelper.wrapAngleTo180_float(targetYaw - currentYaw)) * factor
            }

            "Custom" -> {
                return customCode(currentYaw, targetYaw, speed).toFloat()
            }

            else -> return targetYaw
        }
    }

    private fun calculatePitch(current: Float, target: Float, speed: Float): Float {
        when (rotationMode.get()) {
            "DataSimulationA" -> {
                val closestStartPitch =
                    findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(currentPitch).toDouble())
                val closestEndPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(target).toDouble())
                val startIndexPitch = PitchData.indexOf(closestStartPitch)
                val endIndexPitch = PitchData.indexOf(closestEndPitch)
                val resultPitch = if (startIndexPitch <= endIndexPitch) PitchData.subList(
                    startIndexPitch,
                    endIndexPitch + 1
                ) else PitchData.subList(endIndexPitch, startIndexPitch + 1).reversed()
                var newPitch = 0f
                if (PitchDataIndex < resultPitch.size) {
                    PitchDataIndex++
                    newPitch = resultPitch[PitchDataIndex].toFloat()
                }
                if (PitchDataIndex >= resultPitch.size) PitchDataIndex = 0
                return newPitch
            }

            "DataSimulationB" -> {
                val closestStartPitch =
                    findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(currentPitch).toDouble())
                val closestEndPitch = findClosestValue(PitchData, MathHelper.wrapAngleTo180_float(target).toDouble())
                val startIndexPitch = PitchData.indexOf(closestStartPitch)
                val endIndexPitch = PitchData.indexOf(closestEndPitch)
                val resultPitch = if (startIndexPitch <= endIndexPitch) PitchData.subList(
                    startIndexPitch,
                    endIndexPitch + 1
                ) else PitchData.subList(endIndexPitch, startIndexPitch + 1).reversed()
                var newPitch = 0f
                if (PitchDataIndex < resultPitch.size) {
                    PitchDataIndex++
                    newPitch = resultPitch[PitchDataIndex].toFloat()
                }
                if (PitchDataIndex >= resultPitch.size) PitchDataIndex = 0
                return newPitch
            }
            "GlobalDataSimulation" ->{
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * 0.4f
            }
            "BasicSimulation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * 0.4f
            }
        }
        when (smoothMode.get()) {
            "Slerp" -> {
                val rawDelta = target - current
                val delta = MathHelper.wrapAngleTo180_float(rawDelta)
                val baseSpeed = when {
                    abs(delta) > 150f -> speed * 0.3f
                    abs(delta) > 90f -> speed * 0.6f
                    abs(delta) > 45f -> speed * 0.8f
                    else -> speed * (microAdjustBoost.get() - abs(delta) / 100f)
                }
                val decayFactor = 1f / (1f + exp(-abs(delta) / 30f))
                val finalSpeed = baseSpeed * decayFactor * slerpDecayRate.get()

                return current + delta * finalSpeed
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
                val distance = abs(delta)
                val deltaTime = distance.coerceAtMost(factor)
                return current + deltaTime * sign(delta)
            }

            "AdaptiveSlerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val distance = abs(delta)
                val smoothFactor = (distance / 180.0).pow(2.0)
                val deltaTime = (smoothFactor.toFloat() * speed).coerceAtMost(distance)
                return current + deltaTime * sign(delta)
            }

            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
            }

            "CosineInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = (1 - cos(Math.PI * speed)).toFloat() * 0.5f
                return current + delta * factor
            }

            "LogarithmicInterpolation" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = ln((1 + speed).toDouble()).toFloat()
                return current + delta * factor
            }

            "ElasticSpring" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val elasticity = elasticity.get()
                val damping = dampingFactor2.get()
                val factor = exp((-elasticity * speed).toDouble()) * cos(damping * speed * Math.PI).toFloat()
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

            "Custom" -> {
                return customCode(current, target, speed).toFloat()
            }

            else -> return target
        }
    }

    private fun setSpeed(it: EntityPlayer) {
        currentSpeed = when (smoothMode.get()) {
            "Slerp" -> slerpSpeed.get()
            "AdaptiveBezier" -> adaptiveBezierSpeed.get()
            "AdaptiveSlerp" -> adaptiveSlerpSpeed.get()
            "Damping" -> dampingSpeed.get()
            "Sinusoidal" -> sinusoidalSpeed.get()
            "Spring" -> springSpeed.get()
            "CosineInterpolation" -> cosineInterpolationSpeed.get()
            "LogarithmicInterpolation" -> logarithmicInterpolationSpeed.get()
            "ElasticSpring" -> elasticSpringSpeed.get()
            "Bezier" -> bezierSpeed.get()
            else -> 0f
        } + speedValue.toFloat() + if (!isMove(it)) StationaryAccelerateSpeed.get() else 0f
    }

    private fun randomSpeed(it: EntityPlayer) {
        if (speedTick < randomSpeedFrequency.get()) speedTick++ else {
            speedTick = 0
            when (smoothMode.get()) {
                "Slerp" -> speedValue = getSpeedValue(it, slerpRandomMinSpeed.get(), slerpRandomMaxSpeed.get())
                "AdaptiveBezier" -> speedValue =
                    getSpeedValue(it, adaptiveBezierRandomMinSpeed.get(), adaptiveBezierRandomMaxSpeed.get())

                "AdaptiveSlerp" -> speedValue =
                    getSpeedValue(it, adaptiveSlerpRandomMinSpeed.get(), adaptiveSlerpRandomMaxSpeed.get())

                "Damping" -> speedValue = getSpeedValue(it, dampingRandomMinSpeed.get(), dampingRandomMaxSpeed.get())
                "Sinusoidal" -> speedValue =
                    getSpeedValue(it, sinusoidalRandomMinSpeed.get(), sinusoidalRandomMaxSpeed.get())

                "Spring" -> speedValue = getSpeedValue(it, springRandomMinSpeed.get(), springRandomMaxSpeed.get())
                "CosineInterpolation" -> speedValue =
                    getSpeedValue(it, cosineInterpolationRandomMinSpeed.get(), cosineInterpolationRandomMaxSpeed.get())

                "LogarithmicInterpolation" -> speedValue = getSpeedValue(
                    it,
                    logarithmicInterpolationRandomMinSpeed.get(),
                    logarithmicInterpolationRandomMaxSpeed.get()
                )

                "ElasticSpring" -> speedValue =
                    getSpeedValue(it, elasticSpringRandomMinSpeed.get(), elasticSpringRandomMaxSpeed.get())

                "Bezier" -> speedValue = getSpeedValue(it, bezierRandomMinSpeed.get(), bezierRandomMaxSpeed.get())
            }
        }
    }
}