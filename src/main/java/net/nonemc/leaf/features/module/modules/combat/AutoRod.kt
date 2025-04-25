package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.entity.EntityTypeLib
import net.nonemc.leaf.libs.extensions.getDistanceToEntityBox
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import kotlin.math.*

@ModuleInfo(name = "AutoRod", category = ModuleCategory.COMBAT)
class AutoRod : Module() {
    private val initialVelocity = FloatValue("InitialVelocity", 0.25f, 0f, 1f)
    private val gravity = FloatValue("Gravity", 0.04f, 0f, 0.1f)
    private val eyeOffset = FloatValue("EyeOffset", 1.62f, -5f, 5f)
    private val maxRange = FloatValue("MaxRange", 5f, 0f, 15f)
    private val minRange = FloatValue("MinRange", 3f, 0f, 15f)
    private val predictSize = FloatValue("PredictSize", 2F, 0F, 10F)
    private val delay = IntegerValue("Delay", 500, 1, 2000)
    private val sleepDelay = IntegerValue("SleepDelay", 500, 1, 2000)
    val rotateValue = BoolValue("SilentRotate", false)
    val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 10, 0, 20).displayable { rotateValue.get() }
    private val basicSimulationSpeed = FloatValue("Rotation-BasicSimulationSpeed", 0.2f, 0.01f, 1f)
    private val basicSimulationBaseSpeed = FloatValue("Rotation-BasicSimulation-BaseSpeed", 180f, 50f, 360f)
    private val basicSimulationSpeedMultLarge = FloatValue("Rotation-BasicSimulation-SpeedMult-Large", 1.8f, 1.0f, 3.0f)
    private val basicSimulationSpeedMultMedium = FloatValue("Rotation-BasicSimulation-SpeedMult-Medium", 1.2f, 0.8f, 2.0f)
    private val basicSimulationSpeedMultSmall = FloatValue("Rotation-BasicSimulation-SpeedMult-Small", 0.8f, 0.5f, 1.5f)
    private val basicSimulationBaseAccel = FloatValue("Rotation-BasicSimulation-BaseAcceleration", 720f, 300f, 1200f)
    private val basicSimulationAccelMultLarge = FloatValue("Rotation-BasicSimulation-AccelMult-Large", 2.0f, 1.0f, 4.0f)
    private val basicSimulationAccelMultMedium = FloatValue("Rotation-BasicSimulation-AccelMult-Medium", 1.5f, 0.8f, 3.0f)
    private val basicSimulationAccelMultSmall = FloatValue("Rotation-BasicSimulation-AccelMult-Small", 1.0f, 0.5f, 2.0f)
    private val basicSimulationPBase = FloatValue("Rotation-BasicSimulation-PID-P-Base", 1.8f, 0f, 50.0f)
    private val basicSimulationPAttenuation = FloatValue("Rotation-BasicSimulation-PID-P-Attenuation", 0.6f, 0f, 50.0f)
    private val basicSimulationIBase = FloatValue("Rotation-BasicSimulation-PID-I-Base", 0.08f, 0f, 50f)
    private val basicSimulationIAttenuation = FloatValue("Rotation-BasicSimulation-PID-I-Attenuation", 0.9f, 0f, 50.0f)
    private val basicSimulationDBase = FloatValue("Rotation-BasicSimulation-PID-D-Base", 0.3f, 0f, 50.0f)
    private val basicSimulationDAttenuation = FloatValue("Rotation-BasicSimulation-PID-D-Attenuation", 0.8f, 0.5f, 50.0f)
    private val basicSimulationJitterHigh = FloatValue("Rotation-BasicSimulation-Jitter-High", 1.8f, 0.0f, 3.0f)
    private val basicSimulationJitterMedium = FloatValue("Rotation-BasicSimulation-Jitter-Medium", 1.2f, 0.0f, 2.5f)
    private val basicSimulationJitterLow = FloatValue("Rotation-BasicSimulation-Jitter-Low", 0.6f, 0.0f, 2.0f)
    private val basicSimulationDampingFactor = FloatValue("Rotation-BasicSimulation-Damping", 0.2f, 0.0f, 0.5f)
    private val basicSimulationFatigueDecay = FloatValue("Rotation-BasicSimulation-FatigueDecay", 0.97f, 0.9f, 1.0f)
    private val basicSimulationFatigueRecover = FloatValue("Rotation-BasicSimulation-FatigueRecover", 0.4f, 0.1f, 0.9f)
    private val basicSimulationOvershootDecay = FloatValue("Rotation-BasicSimulation-OvershootDecay", 0.6f, 0.3f, 1.0f)
    private val basicSimulationOvershootThreshold = FloatValue("Rotation-BasicSimulation-OvershootThreshold", 0.2f, 0.05f, 0.5f)
    private val basicSimulationFineTuningThreshold = FloatValue("Rotation-BasicSimulation-FineTuningThreshold", 3f, 1f, 10f)
    private val basicSimulationMicroJitter = FloatValue("Rotation-BasicSimulation-MicroJitter", 0.3f, 0.1f, 1.0f)
    private val basicSimulationAdjustMinDelta = FloatValue("Rotation-BasicSimulation-AdjustMinDelta", 10f, 0f, 180f)
    private val basicSimulationAdjustMaxDelta = FloatValue("Rotation-BasicSimulation-AdjustMaxDelta", 20f, 0f, 180f)
    private val basicSimulationAdjustTime = IntegerValue("Rotation-BasicSimulation-AdjustTime", 1000, 1, 5000)
    private val basicSimulationAdjustMaxRandom = FloatValue("Rotation-BasicSimulation-AdjustMaxRandom", 0.5f, -5f, 5f)
    private val basicSimulationAdjustMinRandom = FloatValue("Rotation-BasicSimulation-AdjustMinRandom", -0.5f, -5f, 5f)
    private val basicSimulationPDynamicGain = FloatValue("Rotation-BasicSimulation-P-DynamicGain", 1.5f, 0.5f, 3.0f)
    private val basicSimulationPNonlinearity = FloatValue("Rotation-BasicSimulation-P-Nonlinearity", 2.4f, 1.0f, 5.0f)
    private val basicSimulationPResponseCurve = FloatValue("Rotation-BasicSimulation-P-ResponseCurve", 0.7f, 0.3f, 1.5f)
    private val basicSimulationIAdaptiveThreshold = FloatValue("Rotation-BasicSimulation-I-AdaptiveThreshold", 15f, 5f, 30f)
    private val basicSimulationINonlinearDecay = FloatValue("Rotation-BasicSimulation-I-NonlinearDecay", 0.85f, 0.5f, 0.95f)
    private val basicSimulationIAntiWindup = FloatValue("Rotation-BasicSimulation-I-AntiWindup", 0.4f, 0.1f, 0.9f)
    private val basicSimulationIDynamicClamp = FloatValue("Rotation-BasicSimulation-I-DynamicClamp", 50f, 10f, 100f)
    private val basicSimulationDNoiseFilter = FloatValue("Rotation-BasicSimulation-D-NoiseFilter", 0.6f, 0.3f, 0.9f)
    private val basicSimulationDNonlinearBoost = FloatValue("Rotation-BasicSimulation-D-NonlinearBoost", 1.8f, 1.0f, 3.0f)
    private val basicSimulationDErrorSensitivity = FloatValue("Rotation-BasicSimulation-D-ErrorSensitivity", 0.4f, 0.1f, 1.0f)
    private val basicSimulationErrorDeadzone = FloatValue("Rotation-BasicSimulation-ErrorDeadzone", 0.5f, 0.1f, 2.0f)
    private val basicSimulationErrorSaturation = FloatValue("Rotation-BasicSimulation-ErrorSaturation", 45f, 30f, 90f)
    private val basicSimulationDynamicCoupling = FloatValue("Rotation-BasicSimulation-DynamicCoupling", 0.7f, 0.3f, 1.2f)
    private val basicSimulationLastDerivativeValue = FloatValue("Rotation-BasicSimulation-LastDerivativeValue", 1f, 0f, 90f)

    private val adTime = MSTimer()
    private val d = MSTimer()
    private val sleep = MSTimer()
    private var allow = true
    override fun onDisable() {
        allow = true
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        for (entity in mc.theWorld.playerEntities) {
            if (EntityTypeLib.isSelected(entity, true) && entity != null) {
                val distance = entity.getDistanceToEntityBox(player)
                if (distance in minRange.get()..maxRange.get()) {
                    val playerVec = player.positionVector
                    val targetVec = Vec3(
                        entity.posX + (entity.posX - entity.prevPosX) * predictSize.get(),
                        entity.posY,
                        entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize.get()
                    )

                    val rotation = getRotation(playerVec, targetVec)
                    if (rotateValue.get()) {
                        RotationBaseLib.setTargetRotation(Rotation(
                            calculateRotation(mc.thePlayer.rotationYaw, rotation!!.yaw),
                            calculateRotation(mc.thePlayer.rotationPitch, rotation.pitch)),
                            keepDirectionTickValue.get()
                        )
                    } else {
                        mc.thePlayer.rotationYaw = calculateRotation(mc.thePlayer.rotationYaw, rotation!!.yaw)
                        mc.thePlayer.rotationPitch = calculateRotation(mc.thePlayer.rotationPitch, rotation.pitch)
                    }

                    if (entity.hurtTime == 0) {
                        if (d.hasTimePassed(delay.get().toLong())) {
                            d.reset()
                            switchFishingRod()
                            send()
                        }
                    }
                    if (entity.hurtTime != 0) {
                        if (sleep.hasTimePassed(sleepDelay.get().toLong())) {
                            sleep.reset()
                            back()
                        }
                    }
                }
            }
        }
    }

    private var basicSimulationLastUpdateTime: Long = 0L
    private var basicSimulationLastError: Float = 0f
    private var basicSimulationIntegral: Float = 0f
    private var bsSpeed: Float = 0f
    private var fatigueFactor: Float = 1f

    private fun calculateRotation(currentYaw: Float, targetYaw: Float): Float {
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

        val decay = if (abs(error) > basicSimulationIAdaptiveThreshold.get()) {
            1 - basicSimulationINonlinearDecay.get() *
                    (abs(error) - basicSimulationIAdaptiveThreshold.get()) / basicSimulationIAdaptiveThreshold.get()
        } else 1f

        basicSimulationIntegral = (basicSimulationIntegral * decay) +
                (error * dt * 0.5f * (1 - basicSimulationIAntiWindup.get() *
                        tanh(abs(basicSimulationIntegral) / basicSimulationIDynamicClamp.get())))
        basicSimulationIntegral = basicSimulationIntegral.coerceIn(
            -basicSimulationIDynamicClamp.get(),
            basicSimulationIDynamicClamp.get()
        )

        val derivative = if (dt > 0) (error - basicSimulationLastError) / dt else 0f
        val filteredDerivative = basicSimulationDNoiseFilter.get() * derivative +
                (1 - basicSimulationDNoiseFilter.get()) * basicSimulationLastDerivativeValue.get()
        val dBoost =
            1 + basicSimulationDNonlinearBoost.get() * tanh(abs(error) * basicSimulationDErrorSensitivity.get())

        val p = adaptiveP * (1 - basicSimulationPAttenuation.get() * exp(-abs(delta) / 30.0).toFloat())
        val i = basicSimulationIBase.get() * (1 - basicSimulationIAttenuation.get() * exp(-abs(delta) / 15.0).toFloat())
        val d =
            basicSimulationDBase.get() * dBoost * (1 - basicSimulationDAttenuation.get() * exp(-abs(delta) / 20.0).toFloat())

        val dynamicCoupling = 1 + basicSimulationDynamicCoupling.get() *
                (1 - exp(-(abs(bsSpeed) / 180.0).toFloat()))
        val output = ((p * error) + (i * basicSimulationIntegral) + (d * filteredDerivative)) * dynamicCoupling

        val desiredSpeed = output.coerceIn(-maxSpeed, maxSpeed)
        val maxAcceleration = acceleration * dt
        val speedChange = (desiredSpeed - bsSpeed).coerceIn(-maxAcceleration, maxAcceleration)
        bsSpeed += speedChange

        val dampingFactor = 1 - basicSimulationDampingFactor.get() * exp(-abs(error) / 45.0).toFloat()
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
    private fun send(){
        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
    }

    private fun getRotation(start: Vec3, target: Vec3): Rotation? {
        val dx = target.xCoord - start.xCoord
        val dy = target.yCoord - start.yCoord
        val dz = target.zCoord - start.zCoord
        val horizontalDist = sqrt(dx * dx + dz * dz)
        val yaw = (Math.toDegrees(atan2(dz, dx)) - 90.0).toFloat().mod(360.0f)
        if (horizontalDist == 0.0) {
            return Rotation(
                yaw,
                if (dy > 0) -90.0f else 90.0f
            )
        }
        val initialVelocity = initialVelocity.get()
        val gravity = gravity.get()
        val verticalOffset = eyeOffset.get()
        val adjustedY = target.yCoord - (start.yCoord + verticalOffset)
        val a = gravity * horizontalDist * horizontalDist / (2 * initialVelocity * initialVelocity)
        val b = -horizontalDist
        val c = a + adjustedY
        val discriminant = b * b - 4 * a * c
        if (discriminant < 0) return null
        val sqrtDisc = sqrt(discriminant)
        val tanTheta1 = (-b + sqrtDisc) / (2 * a)
        val tanTheta2 = (-b - sqrtDisc) / (2 * a)
        val angles = listOf(tanTheta1, tanTheta2)
            .map { -Math.toDegrees(atan(it)) }
            .filter { it in -90.0..90.0 }
            .sortedBy { abs(it) }

        return Rotation(
            yaw,
            angles.firstOrNull()?.toFloat() ?: return null
        )
    }
    private fun back() {
        val player = mc.thePlayer ?: return
        val inventory = player.inventory
        inventory.currentItem = 0
    }
    private fun switchFishingRod() {
        val player = mc.thePlayer ?: return
        val inventory = player.inventory
        for (slot in 0..8) {
            val stack: ItemStack? = inventory.getStackInSlot(slot)
            if (isFishingRod(stack)) {
                inventory.currentItem = slot
                return
            }
        }
    }

    private fun isFishingRod(stack: ItemStack?): Boolean {
        return stack != null &&
                stack.item is ItemFishingRod
    }
}