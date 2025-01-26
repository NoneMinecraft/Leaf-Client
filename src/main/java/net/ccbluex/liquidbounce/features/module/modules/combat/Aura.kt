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
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils.getRotationTo
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.*
import kotlin.math.*
import kotlin.random.Random

@ModuleInfo(name = "Aura", category = ModuleCategory.COMBAT)
object Aura : Module() {
    private val maxAttackDelay = IntegerValue("MaxAttackDelay", 100, 0, 1000)
    private val minAttackDelay = IntegerValue("MinAttackDelay", 50, 0, 1000)
    private val throughWallsRange = FloatValue("ThroughWallsRange", 3F, 0F, 6F)
    private val range = FloatValue("AttackRange", 3F, 0F, 6F)
    private val minHurtTime = IntegerValue("MinHurtTime", 9, 0, 10)
    private val maxHurtTime = IntegerValue("MaxHurtTime", 10, 0, 10)
    private val visibilityDetection = BoolValue("VisibilityDetection", true)
    val visibilityDetectionEntityBoundingBox = BoolValue("VisibilityDetectionEntityBoundingBox", true)
    private val visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue = BoolValue("VisibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue", true)
    val rotateValue = BoolValue("SilentRotate", false)
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
            "DataSimulationA",
            "DataSimulationB",
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
    val customSmoothCode = TextValue("CustomSmoothCode","ifelseifelseifelseifelse").displayable { smoothMode.get() == "Custom" }
    private val randomSpeedValue = BoolValue("RandomSpeed", true)
    private val randomSpeedFrequency = IntegerValue("RandomSpeedFrequency", 1, 1, 10).displayable { randomSpeedValue.get() }
    // Slerp
    private val slerpSpeed = FloatValue("SlerpTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Slerp" }
    private val slerpRandomMinSpeed = FloatValue("Slerp-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" }
    private val slerpRandomMaxSpeed = FloatValue("Slerp-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Slerp" }
    // Damping
    private val dampingSpeed = FloatValue("DampingSpeed", 0.5f, 0.01f, 1f).displayable { smoothMode.get() == "Damping" }
    private val dampingRandomMinSpeed = FloatValue("Damping-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" }
    private val dampingRandomMaxSpeed = FloatValue("Damping-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Damping" }
    // Sinusoidal
    private val sinusoidalSpeed = FloatValue("SinusoidalTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Sinusoidal" }
    private val sinusoidalRandomMinSpeed = FloatValue("Sinusoidal-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" }
    private val sinusoidalRandomMaxSpeed = FloatValue("Sinusoidal-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Sinusoidal" }
    // Spring
    private val springSpeed = FloatValue("SpringTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "Spring" }
    private val springRandomMinSpeed = FloatValue("Spring-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" }
    private val springRandomMaxSpeed = FloatValue("Spring-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "Spring" }
    // BezierEasing
    private val bezierEasingSpeed = FloatValue("BezierEasingTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "BezierEasing" }
    private val bezierEasingRandomMinSpeed = FloatValue("BezierEasing-RandomMinTurnSpeed", -0.1f, -5f, 5f).displayable { smoothMode.get() == "BezierEasing" }
    private val bezierEasingRandomMaxSpeed = FloatValue("BezierEasing-RandomMaxTurnSpeed", 0.1f, -5f, 5f).displayable { smoothMode.get() == "BezierEasing" }
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
    // ComplexBezier
    private val complexBezierSpeed = FloatValue("ComplexBezierTurnSpeed", 0.2f, 0.01f, 1f).displayable { smoothMode.get() == "ComplexBezier" }
    private val complexBezierRandomMinSpeed = FloatValue("ComplexBezier-RandomMinTurnSpeed", -0.1f, -5f, 10f).displayable { smoothMode.get() == "ComplexBezier" }
    private val complexBezierRandomMaxSpeed = FloatValue("ComplexBezier-RandomMaxTurnSpeed", 0.1f, -5f, 10f).displayable { smoothMode.get() == "ComplexBezier" }

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
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable { raycastValue.get() }
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable { raycastValue.get() }

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
    private val allowAttackWhenNotBlocking = BoolValue("AllowAttackWhenNotBlocking", false)
    private val autoBlockMode = ListValue("AutoBlockMode", arrayOf("C08", "KeyBind","None"), "C07")
    private val autoBlockTrigger = ListValue("AutoBlockTrigger", arrayOf("Range-Always", "Always","Range-Delay"), "Range-Always")
    private val autoBlockDelayValue = IntegerValue("AutoBlockDelay", 50, 1, 1000)
    private val autoBlockRange = FloatValue("AutoBlockMaxRange", 3F, 0F, 6F)
    private val autoBlockDetectsHeldItemAreOnlySwords = BoolValue("AutoBlockDetectsHeldItemAreOnlySwords", true)
    private val autoBlockDetectionGUIIsNullOnly = BoolValue("AutoBlockDetectionGUIIsNullOnly", true)
    private val callAttackEvent = BoolValue("CallAttackEvent", true)
    private val attackTargetEntityWithCurrentItem = BoolValue("AttackTargetEntityWithCurrentItem", true)
    private val debug = BoolValue("Debug", false)
    private val cpsDebug = BoolValue("CPSDebug", false)
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
    private var currentTarget: EntityLivingBase? = null
    override fun onDisable() {
        cpsUpdate = false
        allowStrictStrafe = false
        abreset = false
        sprintValue = true
        speedTick = 0
        strictStrafeValue = false
        mc.gameSettings.keyBindUseItem.pressed = false
        mc.gameSettings.keyBindAttack.pressed = false
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
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        if (raycastValue.get()) { //这个入没写完
            val raycastedEntity = RaycastUtils.raycastEntity(range.get().toDouble()) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand)
                        && (EntityUtils.isSelected(
                    it,
                    true
                ) || raycastIgnoredValue.get() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                    it,
                    it.entityBoundingBox
                ).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase &&
                !EntityUtils.isFriend(raycastedEntity)
            ) {
                currentTarget = raycastedEntity
            }
        }
        val target = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter {it != player && it == currentTarget && EntityUtils.isSelected(it, true)
                    && (it.getDistanceToEntityBox(player) <= range.get()) }
            .firstOrNull { (!visibilityDetection.get() && !visibility(player, it).first && it.getDistanceToEntityBox(player) <= throughWallsRange.get()) || visibility(player, it).first }
        target?.let {
            if (!hitable.get() || hitable(it, range.get().toDouble()) && attack.get() && it.hurtTime in minHurtTime.get()..maxHurtTime.get() && it.getDistanceToEntityBox(player) <= range.get()) {
                if (clickDelay.hasTimePassed(Random.nextLong(minAttackDelay.get().toLong(),maxAttackDelay.get().toLong()))) {
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
                    "Damping" -> speedValue = getSpeedValue(it, dampingRandomMinSpeed.get(), dampingRandomMaxSpeed.get())
                    "Sinusoidal" -> speedValue = getSpeedValue(it, sinusoidalRandomMinSpeed.get(), sinusoidalRandomMaxSpeed.get())
                    "Spring" -> speedValue = getSpeedValue(it, springRandomMinSpeed.get(), springRandomMaxSpeed.get())
                    "BezierEasing" -> speedValue = getSpeedValue(it, bezierEasingRandomMinSpeed.get(), bezierEasingRandomMaxSpeed.get())
                    "CosineInterpolation" -> speedValue = getSpeedValue(it, cosineInterpolationRandomMinSpeed.get(), cosineInterpolationRandomMaxSpeed.get())
                    "LogarithmicInterpolation" -> speedValue = getSpeedValue(it, logarithmicInterpolationRandomMinSpeed.get(), logarithmicInterpolationRandomMaxSpeed.get())
                    "ElasticSpring" -> speedValue = getSpeedValue(it, elasticSpringRandomMinSpeed.get(), elasticSpringRandomMaxSpeed.get())
                    "ComplexBezier" -> speedValue = getSpeedValue(it, complexBezierRandomMinSpeed.get(), complexBezierRandomMaxSpeed.get())
                }
            }
            val x = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get()) visibility(player, it).second.xCoord else it.posX
            val y = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get())visibility(player, it).second.yCoord else it.posY
            val z = if (visibilityDetectionEntityBoundingBoxAllowsCalculationTheSecondCoordValue.get() && visibilityDetectionEntityBoundingBox.get() && visibilityDetection.get()) visibility(player, it).second.zCoord else it.posZ
            val targetX = if (predictValue.get()) x + (it.posX - it.prevPosX) * predictSize.get() else x
            val targetY = if (predictValue.get()) y + targetPosYOffset.get()+(it.posY - it.prevPosY)* predictSize.get() else it.posY + targetPosYOffset.get()
            val targetZ = if (predictValue.get()) z + (it.posZ - it.prevPosZ) * predictSize.get() else z
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
                simulationYaw(if (rotateValue.get()) currentServerYaw else currentYaw, rotation.yaw
                    , if (rotateValue.get()) currentServerPitch else currentPitch, rotation.pitch
                )
                if (rotateValue.get()) RotationUtils.setTargetRotation(Rotation(yaw, pitch),20)
            }
            if (smoothMode.get() != "DataSimulationA" && smoothMode.get() != "DataSimulationB") {
                var currentSpeed = 0.0f
                if (speedValue < 0.0 && reverseDeflectionAllowedOnlyOutside.get() && hitable(it, range.get().toDouble())) speedValue = 0.0
                when (smoothMode.get()) {
                    "Slerp" -> currentSpeed = if (!isMove(it)) slerpSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else slerpSpeed.get() + speedValue.toFloat()
                    "Damping" -> currentSpeed = if (!isMove(it)) dampingSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else dampingSpeed.get() + speedValue.toFloat()
                    "Sinusoidal" -> currentSpeed = if (!isMove(it)) sinusoidalSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else sinusoidalSpeed.get() + speedValue.toFloat()
                    "Spring" -> currentSpeed = if (!isMove(it)) springSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else springSpeed.get() + speedValue.toFloat()
                    "BezierEasing" -> currentSpeed = if (!isMove(it)) bezierEasingSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else bezierEasingSpeed.get() + speedValue.toFloat()
                    "CosineInterpolation" -> currentSpeed = if (!isMove(it)) cosineInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else cosineInterpolationSpeed.get() + speedValue.toFloat()
                    "LogarithmicInterpolation" -> currentSpeed = if (!isMove(it)) logarithmicInterpolationSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else logarithmicInterpolationSpeed.get() + speedValue.toFloat()
                    "ElasticSpring" -> currentSpeed = if (!isMove(it)) elasticSpringSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else elasticSpringSpeed.get() + speedValue.toFloat()
                    "ComplexBezier" -> currentSpeed = if (!isMove(it)) complexBezierSpeed.get() + speedValue.toFloat() + StationaryAccelerateSpeed.get() else complexBezierSpeed.get() + speedValue.toFloat()
                }
                currentYaw = smoothYaw(currentYaw, rotation.yaw, currentSpeed)
                currentPitch = if (!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble()) || (noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())) smoothPitch(currentPitch, rotation.pitch, currentSpeed) else currentPitch
                currentServerYaw = smoothYaw(currentServerYaw, rotation.yaw, currentSpeed)
                currentServerPitch = if (!noFacingPitch.get() || !hitable(it, noFacingPitchMaxRange.get().toDouble()) || (noFacingPitchOnlyPlayerMove.get() && !isPlayerMoving())) smoothPitch(currentServerPitch, rotation.pitch, currentSpeed) else currentServerPitch
                val noiseValue = perlinNoise(playerX, playerY, playerZ, 1145)
                val jitterAmount = noiseValue * randomPitchJitterAmount.get()
                if (pitchJitter.get()) currentPitch += if (pitchJitterRandomMode.get() == "Perlin") jitterAmount.toFloat()
                else Random.nextDouble(randomPitchMinValue.get().toDouble(), randomPitchMaxValue.get().toDouble()).toFloat()
                if ((!noFacingRotations.get() || !hitable(it, noFacingRotationsMaxRange.get().toDouble()))) {
                    if (noBadPackets.get() && ((!rotateValue.get() && (currentPitch < -90.0 || currentPitch > 90.0 || currentYaw > 180.0 || currentYaw < -180)) || (rotateValue.get() && (currentServerPitch < -90.0 || currentServerPitch > 90.0 || currentServerYaw > 360.0 || currentServerYaw < -360.0)))) {
                        if (debug.get()) ChatPrint("[Aura]Blocked a bad packet: $currentYaw , $currentPitch , $currentServerYaw , $currentServerPitch")
                        return}
                    turn(currentYaw, currentPitch, currentServerYaw, currentServerPitch)
                } else if (silentRotateKeepLastRotation.get() && rotateValue.get()) RotationUtils.setTargetRotation(lastRotation)
            }
        } ?: run {
            allowStrictStrafe = false
            ABReset()
            sprintValue = true
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
            (!reverseDeflectionAllowedOnlyOutside.get() || !hitable(it, range.get().toDouble())) &&
            probability(extraReverseDeflectionRate.get())) 0.0 else maxSpeed.toDouble()
        return if (randomSpeedValue.get()) Random.nextDouble(min, max) else 0.0
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
            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
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
                val p1 = Random.nextDouble(0.1,0.2).toFloat()
                val p2 = Random.nextDouble(0.7,0.9).toFloat()
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
    private fun smoothPitch(current: Float, target: Float, speed: Float): Float {
        when (smoothMode.get()) {
            "Slerp" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                return current + delta * speed
            }
            "Sinusoidal" -> {
                val delta = MathHelper.wrapAngleTo180_float(target - current)
                val factor = Math.sin((speed * Math.PI) / 2).toFloat()
                return current + delta * factor
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

}