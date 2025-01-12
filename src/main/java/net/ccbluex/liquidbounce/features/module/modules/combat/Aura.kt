//All the code was written by N0ne.
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.aura.PerlinNoise
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.RaycastUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.math.*
import kotlin.random.Random

@ModuleInfo(name = "Aura", category = ModuleCategory.COMBAT)
object Aura : Module() {
    private val attackDelay = IntegerValue("AttackDelay", 100, 0, 1000)
    private val range = FloatValue("AttackRange", 3F, 1F, 6F)
    private val minHurtTime = IntegerValue("MinHurtTime", 9, 0, 10)
    private val maxHurtTime = IntegerValue("MaxHurtTime", 10, 0, 10)
    private val rotateValue = BoolValue("SilentRotate", false)
    private val noBadPackets = BoolValue("NoBadPackets", true)
    private val playerPosYOffset = FloatValue("PlayerPosYOffset", 0F, -1F, 1F)
    private val targetPosYOffset = FloatValue("TargetPosYOffset", 0F, -1F, 1F)
    private val hitable = BoolValue("Hitable", true)
    private val attack = BoolValue("Attack", true)
    private val sprint = BoolValue("Sprint", true)
    private val reverseDeflectionAllowedOnlyOutside = BoolValue("ReverseDeflectionAllowedOnlyOutside", true)
    private val extraReverseDeflectionRate = IntegerValue("ExtraReverseDeflectionRate", 50 , 0 , 100)
    private val smoothMode = ListValue(
        "SmoothMode", arrayOf(
            "None",
            "Slerp",
            "Damping",
            "Sinusoidal",
            "Spring",
            "BezierEasing",
            "CosineInterpolation",
            "LogarithmicInterpolation",
            "ElasticSpring",
            "ComplexBezier",
            "Custom"
        ),
        "Slerp"
    )
    private val customSmoothCode = TextValue("CustomSmoothCode","ifelseifelseifelseifelse").displayable { smoothMode.get() == "Custom" }
    private val randomSpeedValue = BoolValue("RandomSpeed", true)
    private val randomSpeedFrequency = IntegerValue("RandomSpeedFrequency", 1, 1, 10).displayable { randomSpeedValue.get() }
    // Slerp
    private val slerpSpeed = FloatValue("SlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Slerp" }
    private val slerpRandomMinSpeed = FloatValue("Slerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" }
    private val slerpRandomMaxSpeed = FloatValue("Slerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" }
    // lerpAngle
    private val lerpAngleSpeed = FloatValue("LerpAngleTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "lerpAngle" }
    private val lerpAngleRandomMinSpeed = FloatValue("LerpAngle-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "lerpAngle" }
    private val lerpAngleRandomMaxSpeed = FloatValue("LerpAngle-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "lerpAngle" }
    // Damping

    private val dampingSpeed = FloatValue("DampingSpeed", 0.5f, 0.01f, 1f).displayable { smoothMode.get() == "Damping" }
    private val dampingRandomMinSpeed = FloatValue("Damping-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" }
    private val dampingRandomMaxSpeed = FloatValue("Damping-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" }
    // Sinusoidal
    private val sinusoidalSpeed = FloatValue("SinusoidalTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Sinusoidal" }
    private val sinusoidalRandomMinSpeed = FloatValue("Sinusoidal-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" }
    private val sinusoidalRandomMaxSpeed = FloatValue("Sinusoidal-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" }
    // Exponential
    private val exponentialSpeed = FloatValue("ExponentialTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Exponential" }
    private val exponentialRandomMinSpeed = FloatValue("Exponential-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Exponential" }
    private val exponentialRandomMaxSpeed = FloatValue("Exponential-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Exponential" }
    // Spring
    private val springSpeed = FloatValue("SpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Spring" }
    private val springRandomMinSpeed = FloatValue("Spring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" }
    private val springRandomMaxSpeed = FloatValue("Spring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" }
    // BezierEasing
    private val bezierEasingSpeed = FloatValue("BezierEasingTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "BezierEasing" }
    private val bezierEasingRandomMinSpeed = FloatValue("BezierEasing-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "BezierEasing" }
    private val bezierEasingRandomMaxSpeed = FloatValue("BezierEasing-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "BezierEasing" }
    // LissajousCurve
    private val lissajousCurveSpeed = FloatValue("LissajousCurveTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "LissajousCurve" }
    private val lissajousCurveRandomMinSpeed = FloatValue("LissajousCurve-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "LissajousCurve" }
    private val lissajousCurveRandomMaxSpeed = FloatValue("LissajousCurve-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "LissajousCurve" }
    // CosineInterpolation
    private val cosineInterpolationSpeed = FloatValue("CosineInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "CosineInterpolation" }
    private val cosineInterpolationRandomMinSpeed = FloatValue("CosineInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation" }
    private val cosineInterpolationRandomMaxSpeed = FloatValue("CosineInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "CosineInterpolation" }
    // LogarithmicInterpolation
    private val logarithmicInterpolationSpeed = FloatValue("LogarithmicInterpolationTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "LogarithmicInterpolation" }
    private val logarithmicInterpolationRandomMinSpeed = FloatValue("LogarithmicInterpolation-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation" }
    private val logarithmicInterpolationRandomMaxSpeed = FloatValue("LogarithmicInterpolation-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "LogarithmicInterpolation" }
    // ElasticSpring
    private val elasticSpringSpeed = FloatValue("ElasticSpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "ElasticSpring" }
    private val elasticSpringRandomMinSpeed = FloatValue("ElasticSpring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" }
    private val elasticSpringRandomMaxSpeed = FloatValue("ElasticSpring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "ElasticSpring" }
    // FittsLaw
    private val fittsLawSpeed = FloatValue("FittsLawTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "FittsLaw" }
    private val fittsLawRandomMinSpeed = FloatValue("FittsLaw-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "FittsLaw" }
    private val fittsLawRandomMaxSpeed = FloatValue("FittsLaw-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "FittsLaw" }
    // CubicHermiteSpline
    private val cubicHermiteSplineSpeed = FloatValue("CubicHermiteSplineTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "CubicHermiteSpline" }
    private val cubicHermiteSplineRandomMinSpeed = FloatValue("CubicHermiteSpline-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "CubicHermiteSpline" }
    private val cubicHermiteSplineRandomMaxSpeed = FloatValue("CubicHermiteSpline-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "CubicHermiteSpline" }
    // ComplexBezier
    private val complexBezierSpeed = FloatValue("ComplexBezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "ComplexBezier" }
    private val complexBezierRandomMinSpeed = FloatValue("ComplexBezier-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "ComplexBezier" }
    private val complexBezierRandomMaxSpeed = FloatValue("ComplexBezier-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "ComplexBezier" }

    private val elasticity = FloatValue("ElasticSpring-Elasticity", 0.3f, 0.01f, 1f).displayable{smoothMode.get() == "ElasticSpring"}
    private val dampingFactor2 = FloatValue("ElasticSpring-DampingFactor", 0.5f, 0.01f, 1f).displayable{smoothMode.get() == "ElasticSpring"}

    private val predictValue = BoolValue("Predict", true)
    private val predictSize = FloatValue("PredictSize", 2f, 1f, 5f).displayable{predictValue.get()}
    private val noFacingRotations = BoolValue("NoFacingRotations", true)
    private val silentRotateKeepLastRotation = BoolValue("NoFacingRotations-SilentRotateKeepLastRotation", true).displayable{noFacingRotations.get()}
    private val noFacingRotationsMaxRange = FloatValue("NoFacingPitchMaxRange", 1F, 0F, 6F).displayable{noFacingRotations.get()}

    private val noFacingPitch = BoolValue("NoFacingPitch", true)
    private val noFacingPitchOnlyPlayerMove = BoolValue("NoFacingOnlyPlayerMove", true).displayable{noFacingPitch.get()}
    private val noFacingPitchMaxRange = FloatValue("NoFacingMaxRange", 1F, 0F, 6F).displayable{noFacingPitch.get()}

    val strafe = BoolValue("StrictStrafe", true)
    private val fakeSwing = BoolValue("FakeSwing", true)
    private val pitchJitter = BoolValue("PitchJitter", true)
    private val pitchJitterRandomMode = ListValue("PitchJitterRandomMode", arrayOf("Random", "Perlin"), "Perlin")
    private val randomPitchJitterAmount = FloatValue("PerlinNoiseRandomPitchJitterAmount", 2f, 0.01f, 5f)
    private val randomPitchMinValue = FloatValue("RandomPitchMinValue", -1f, -5f, 5f)
    private val randomPitchMaxValue = FloatValue("RandomPitchMaxValue", 1f, -5f, 5f)
    private val StationaryAccelerateSpeed = FloatValue("WhenTargetStationaryAccelerateSpeed", 0.1f, 0.0f, 1f)
    private val swingMode = ListValue("SwingMode", arrayOf("SwingItem", "C0A","None"), "SwingItem")
    private val attackMode = ListValue("AttackMode", arrayOf("C02", "KeyBindAttack","None"), "C02")
    private val callAttackEvent = BoolValue("CallAttackEvent", true)
    private val attackTargetEntityWithCurrentItem = BoolValue("AttackTargetEntityWithCurrentItem", true)
    private val debug = BoolValue("Debug", false)
    private var speedValue = 0.0
    private var speedTick = 0
    var sprintValue = true //Sprint
    var strictStrafeValue = false //EntityLivingBase
    private var lastRotation = Rotation(0.0F,0.0F)
    private val clickDelay = MSTimer()
    override fun onDisable() {
        sprintValue = true
        speedTick = 0
        strictStrafeValue = false
        mc.gameSettings.keyBindAttack.pressed = false
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        val target = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter {it != player && EntityUtils.isSelected(it, true)
                    && (it.getDistanceToEntityBox(player) <= range.get()) }
            .firstOrNull { visibility(player, it).first }
        target?.let {
            mc.thePlayer.inventory
            sprintValue = sprint.get()
            if (speedTick < randomSpeedFrequency.get()) speedTick++ else {
                speedTick = 0
                when (smoothMode.get()) {
                    "Slerp" -> speedValue = getSpeedValue(it, slerpRandomMinSpeed.get(), slerpRandomMaxSpeed.get())
                    "lerpAngle" -> speedValue = getSpeedValue(it, lerpAngleRandomMinSpeed.get(), lerpAngleRandomMaxSpeed.get())
                    "Damping" -> speedValue = getSpeedValue(it, dampingRandomMinSpeed.get(), dampingRandomMaxSpeed.get())
                    "Sinusoidal" -> speedValue = getSpeedValue(it, sinusoidalRandomMinSpeed.get(), sinusoidalRandomMaxSpeed.get())
                    "Exponential" -> speedValue = getSpeedValue(it, exponentialRandomMinSpeed.get(), exponentialRandomMaxSpeed.get())
                    "Spring" -> speedValue = getSpeedValue(it, springRandomMinSpeed.get(), springRandomMaxSpeed.get())
                    "BezierEasing" -> speedValue = getSpeedValue(it, bezierEasingRandomMinSpeed.get(), bezierEasingRandomMaxSpeed.get())
                    "LissajousCurve" -> speedValue = getSpeedValue(it, lissajousCurveRandomMinSpeed.get(), lissajousCurveRandomMaxSpeed.get())
                    "CosineInterpolation" -> speedValue = getSpeedValue(it, cosineInterpolationRandomMinSpeed.get(), cosineInterpolationRandomMaxSpeed.get())
                    "LogarithmicInterpolation" -> speedValue = getSpeedValue(it, logarithmicInterpolationRandomMinSpeed.get(), logarithmicInterpolationRandomMaxSpeed.get())
                    "ElasticSpring" -> speedValue = getSpeedValue(it, elasticSpringRandomMinSpeed.get(), elasticSpringRandomMaxSpeed.get())
                    "FittsLaw" -> speedValue = getSpeedValue(it, fittsLawRandomMinSpeed.get(), fittsLawRandomMaxSpeed.get())
                    "CubicHermiteSpline" -> speedValue = getSpeedValue(it, cubicHermiteSplineRandomMinSpeed.get(), cubicHermiteSplineRandomMaxSpeed.get())
                    "ComplexBezier" -> speedValue = getSpeedValue(it, complexBezierRandomMinSpeed.get(), complexBezierRandomMaxSpeed.get())
                }
            }
            val targetX = if (predictValue.get()) it.posX + (it.posX - it.prevPosX) * predictSize.get() else it.posX
            val targetY = if (predictValue.get()) it.posY + targetPosYOffset.get()+(it.posY - it.prevPosY)* predictSize.get() else it.posY + targetPosYOffset.get()
            val targetZ = if (predictValue.get()) it.posZ + (it.posZ - it.prevPosZ) * predictSize.get() else it.posZ
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
            var currentSpeed = 0.0f
            if (speedValue < 0.0 && reverseDeflectionAllowedOnlyOutside.get() && hitable(it, range.get().toDouble())) {
                speedValue = 0.0
                if (debug.get()) ChatPrint("[Aura]Reset Speed")
            }
            when(smoothMode.get()){
                "Slerp" -> currentSpeed = if (!isMove(it)) slerpSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else slerpSpeed.get() + speedValue.toFloat()
                "lerpAngle" -> currentSpeed = if (!isMove(it)) lerpAngleSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else lerpAngleSpeed.get() + speedValue.toFloat()
                "Damping" -> currentSpeed = if (!isMove(it)) dampingSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else dampingSpeed.get() + speedValue.toFloat()
                "Sinusoidal" -> currentSpeed = if (!isMove(it)) sinusoidalSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else sinusoidalSpeed.get() + speedValue.toFloat()
                "Exponential" -> currentSpeed = if (!isMove(it)) exponentialSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else exponentialSpeed.get() + speedValue.toFloat()
                "Spring" -> currentSpeed = if (!isMove(it)) springSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else springSpeed.get() + speedValue.toFloat()
                "BezierEasing" -> currentSpeed = if (!isMove(it)) bezierEasingSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else bezierEasingSpeed.get() + speedValue.toFloat()
                "LissajousCurve" -> currentSpeed = if (!isMove(it)) lissajousCurveSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else lissajousCurveSpeed.get() + speedValue.toFloat()
                "CosineInterpolation" -> currentSpeed = if (!isMove(it)) cosineInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else cosineInterpolationSpeed.get() + speedValue.toFloat()
                "LogarithmicInterpolation" -> currentSpeed = if (!isMove(it)) logarithmicInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else logarithmicInterpolationSpeed.get() + speedValue.toFloat()
                "ElasticSpring" -> currentSpeed = if (!isMove(it)) elasticSpringSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else elasticSpringSpeed.get() + speedValue.toFloat()
                "FittsLaw" -> currentSpeed = if (!isMove(it)) fittsLawSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else fittsLawSpeed.get() + speedValue.toFloat()
                "CubicHermiteSpline" -> currentSpeed = if (!isMove(it)) cubicHermiteSplineSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else cubicHermiteSplineSpeed.get() + speedValue.toFloat()
                "ComplexBezier" -> currentSpeed = if (!isMove(it)) complexBezierSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else complexBezierSpeed.get() + speedValue.toFloat()
            }
            val noiseValue = perlinNoise(playerX,playerY,playerZ,1145)
            val jitterAmount = noiseValue * randomPitchJitterAmount.get()
            currentYaw = smooth(currentYaw, rotation.yaw, currentSpeed)
            currentPitch =  if(!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble()) ||(noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())) smooth(currentPitch, rotation.pitch, currentSpeed) else currentPitch
            currentServerYaw = smooth(currentServerYaw, rotation.yaw, currentSpeed)
            currentServerPitch = if(!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble())||(noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving()))  smooth(currentServerPitch, rotation.pitch, currentSpeed) else currentServerPitch
            if (pitchJitter.get())currentPitch += if (pitchJitterRandomMode.get() == "Perlin") jitterAmount.toFloat()
            else Random.nextDouble(randomPitchMinValue.get().toDouble(),randomPitchMaxValue.get().toDouble()).toFloat()
            if (!hitable.get() || hitable(it, range.get().toDouble()) && attack.get() && it.hurtTime in minHurtTime.get()..maxHurtTime.get() && it.getDistanceToEntityBox(player) <= range.get()) {
                if (clickDelay.hasTimePassed(attackDelay.get().toLong())) {
                    clickDelay.reset()
                    attack(it)
                }
            } else if (fakeSwing.get()) mc.thePlayer.swingItem()

            if ((!noFacingRotations.get() || !hitable(it, noFacingRotationsMaxRange.get().toDouble()))) {
                if (noBadPackets.get() &&
                    ((!rotateValue.get() && (currentPitch < -90.0
                            || currentPitch > 90.0 || currentYaw == Float.MAX_VALUE
                            || currentYaw == Float.MIN_VALUE
                            || currentYaw == Float.NEGATIVE_INFINITY
                            ||currentYaw == Float.POSITIVE_INFINITY))
                            || (rotateValue.get() && (currentServerPitch < -90.0
                            || currentServerPitch > 90.0 || currentServerYaw == Float.MAX_VALUE
                            || currentServerYaw == Float.MIN_VALUE
                            || currentServerYaw == Float.NEGATIVE_INFINITY
                            ||currentServerYaw == Float.POSITIVE_INFINITY)))) {
                    if (debug.get()) ChatPrint("[Aura]Blocked a bad packet: $currentYaw , $currentPitch , $currentServerYaw , $currentServerPitch")
                    return
                }
                if (!rotateValue.get()) {
                    mc.thePlayer.rotationYaw = currentYaw
                    mc.thePlayer.rotationPitch = currentPitch
                }else {
                    RotationUtils.setTargetRotation(Rotation(currentServerYaw, currentServerPitch))
                }
            }else{
              if (silentRotateKeepLastRotation.get() && rotateValue.get()) RotationUtils.setTargetRotation(lastRotation)
            }
        } ?: run {
            sprintValue = true
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (strafe.get() && rotateValue.get() && mc.thePlayer != null) {
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
    private fun isMove(it:EntityPlayer):Boolean{
        return  (it.posX - it.prevPosX) != 0.0 || (it.posY - it.prevPosY) != 0.0 || (it.posZ - it.prevPosZ) != 0.0
    }
    private fun attack(it:EntityPlayer){
        val event = AttackEvent(it)
        if (callAttackEvent.get()) LiquidBounce.eventManager.callEvent(event)
        if (attackTargetEntityWithCurrentItem.get()) mc.thePlayer.attackTargetEntityWithCurrentItem(it)
        if (swingMode.get() == "SwingItem") mc.thePlayer.swingItem() else if (swingMode.get() == "C0A")  mc.netHandler.addToSendQueue(C0APacketAnimation())
        if (attackMode.get() == "C02") mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))
        else if (attackMode.get() == "KeyBindAttack") mc.gameSettings.keyBindAttack.pressed = true
        if (debug.get()) ChatPrint("[Aura]Attack")
    }
    private fun perlinNoise(x: Double, y: Double, z: Double, seed: Int): Double {
        val perlin = PerlinNoise(seed)
        return perlin.noise(x, y, z)
    }
    private fun hitable(targetEntity: Entity, blockReachDistance: Double): Boolean {
        return RaycastUtils.raycastEntity(
            blockReachDistance
        ) { entity: Entity -> entity === targetEntity } != null
    }
    private fun getSpeedValue(it: EntityPlayer, minSpeed: Float, maxSpeed: Float): Double {
        val min = minSpeed.toDouble()
        val max = if (extraReverseDeflectionRate.get() != 0 && minSpeed < 0 &&
            (!reverseDeflectionAllowedOnlyOutside.get() || !hitable(it, range.get().toDouble())) &&
            probability(extraReverseDeflectionRate.get())) 0.0 else maxSpeed.toDouble()
        return if (randomSpeedValue.get()) Random.nextDouble(min, max) else 0.0
    }
    private fun getRotationTo(from: Vec3, to: Vec3): Rotation {
        val diffX = to.xCoord - from.xCoord
        val diffY = to.yCoord - from.yCoord
        val diffZ = to.zCoord - from.zCoord
        val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ)
        val yaw = (MathHelper.atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90.0f
        val pitch = -(MathHelper.atan2(diffY, dist.toDouble()) * 180.0 / Math.PI).toFloat()
        return Rotation(yaw, pitch)
    }
    private fun customCode(current: Float, target: Float, speed: Float): Double {
        val engine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript")
        val formattedExpression = customSmoothCode.get()
            .replace("current", current.toString())
            .replace("target", target.toString())
            .replace("speed", speed.toString())
        return try {
            engine.eval(formattedExpression) as Double
        } catch (e: ScriptException) {
            ChatPrint("Error (return 0.0) : $e")
            e.printStackTrace()
            0.0
        }
    }
    private fun probability(probability: Int): Boolean {
        if (probability !in 0..100) return true
        return Random.nextInt(0, 100) < probability
    }
    private fun isPlayerMoving(): Boolean {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f)
    }
    private fun smooth(current: Float, target: Float, speed: Float): Float {
        when (smoothMode.get()) {
            "Slerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * speed
            }
            "Damping" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val dampingFactor = Math.pow(dampingSpeed.get().toDouble(), speed.toDouble()).toFloat()
                return current + delta * dampingFactor
            }
            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
            }
            "Spring" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val springFactor = Math.exp(-speed.toDouble()) * Math.cos(speed.toDouble() * Math.PI).toFloat()
                return current + delta * springFactor.toFloat()
            }
            "BezierEasing" -> {
                val t = speed / 10f
                val p0 = 0f
                val p1 = 0.25f
                val p2 = 0.75f
                val p3 = 1f
                val factor = (1 - t).pow(3) * p0 + 3 * (1 - t).pow(2) * t * p1 + 3 * (1 - t) * t.pow(2) * p2 + t.pow(3) * p3
                return current + (MathHelper.wrapAngleTo180_float(target - current)) * factor
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
            "ComplexBezier" -> {
                val t = speed / 10f
                val p0 = 0f
                val p1 = 0.1f
                val p2 = 0.9f
                val p3 = 1f
                val factor = (1 - t).pow(3) * p0 + 3 * (1 - t).pow(2) * t * p1 + 3 * (1 - t) * t.pow(2) * p2 + t.pow(3) * p3
                return current + (MathHelper.wrapAngleTo180_float(target - current)) * factor
            }
            "Custom" ->{
               return customCode(current,target, speed).toFloat()
            }
            else -> return target
        }
    }
    private fun visibility(player: EntityPlayer, target: EntityPlayer): Pair<Boolean, Vec3> {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX,player.posY + player.eyeHeight,player.posZ)
        val minX = target.entityBoundingBox.minX
        val minZ = target.entityBoundingBox.minZ
        val maxX = target.entityBoundingBox.maxX
        val maxZ = target.entityBoundingBox.maxZ
        val minY = target.entityBoundingBox.minY
        val corners = listOf(
            Vec3(target.posX,target.posY,target.posZ),
            Vec3(maxX-0.05,minY+0.8,maxZ-0.05),
            Vec3(maxX-0.05,minY+0.8,minZ-0.05),
            Vec3(minX-0.05,minY+0.8,maxZ-0.05),
            Vec3(minX-0.05,minY+0.8,minZ-0.05))
            for (corner in corners) {
                val result = world.rayTraceBlocks(playerVec, corner, false, false, false,)
                if (result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) return Pair(true, corner)
            }
        return Pair(false, Vec3(0.0,0.0,0.0))
    }
}