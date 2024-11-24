/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 * 所有rage模块以及utils均为None编写
 */
package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.TargetPart
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.WeaponType
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.actions.*
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.getPart
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.render.render3DLine
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.search.hasWeapon
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.special.distanceOffset
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.special.spreadOffset
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils.getRotationTo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@ModuleInfo(name = "RageBot", category = ModuleCategory.Rage)
object RageBot : Module() {
    private val range = FloatValue("MaxRange", 70F, 50F, 200F)
    private val autoRange = BoolValue("AutoRange", true)
    private val akRange = FloatValue("AutoRange-CustomRange-AKRange", 50F, 20F, 100F)
    private val m4Range = FloatValue("AutoRange-CustomRange-M4Range", 50F, 20F, 100F)
    private val awpRange = FloatValue("AutoRange-CustomRange-AWPRange", 70F, 20F, 100F)
    private val mp7Range = FloatValue("AutoRange-CustomRange-MP7Range", 45F, 20F, 100F)
    private val p250Range = FloatValue("AutoRange-CustomRange-P250Range", 35F, 20F, 100F)
    private val deagleRange = FloatValue("AutoRange-CustomRange-DeagleRange", 40F, 20F, 100F)
    private val shotgunRange = FloatValue("AutoRange-CustomRange-ShotGunRange", 20F, 20F, 100F)
    val pitchOffset = FloatValue("PitchOffset", 0.2F, -0.5F, 5F)
    private val targetPredict = BoolValue("TargetPredict", true)
    val targetPredictSize = FloatValue("TargetPredictSize", 4.3F, 0F, 10F).displayable { targetPredict.get() }
    private val playerPredict = BoolValue("PlayerPredict", false)
    private val playerPredictSize = FloatValue("PlayerPredictSize", 4.3F, 0F, 10F).displayable { playerPredict.get() }
    private val targetThroughWallPredict = BoolValue("TargetThroughWallPredict", true)
    private val targetThroughWallPredictSize = FloatValue("TargetThroughWallPredictSize", 5F, 0F, 20F).displayable { targetThroughWallPredict.get() }
    private val playerThroughWallPredict = BoolValue("PlayerThroughWallPredict", false)
    private val playerThroughWallPredictSize = FloatValue("PlayerThroughWallPredictSize", 5F, 0F, 20F).displayable { playerThroughWallPredict.get() }
    private val targetThroughWallPredictFire = BoolValue("targetThroughWallPredictFire", true)
    private val eyeHeight = FloatValue("EyeHeight", 0.8F, -1F, 1F)
     val fireMode = ListValue("FireMode", arrayOf("Legit", "Packet"), "Packet")
     val fireTick = IntegerValue("FireTick", 1, 0, 5).displayable { fireMode.get() == "Legit" }
    private val noSpreadValue = BoolValue("NoSpread", true)
    private val noSpreadMode = ListValue("NoSpreadMode", arrayOf("Switch", "Packet"), "Switch")
     val noSpreadTick = IntegerValue("NoSpreadBaseTick", 2, 0, 5)
    private val autoSneak = BoolValue("AutoSneak", true)
    private val autoSneakMode = ListValue("AutoSneakMode", arrayOf("Legit", "Packet"), "Packet")
    private val autoSneakTriggerMode = ListValue("AutoSneakTriggerMode", arrayOf("Always", "OnlyFire"), "OnlyFire")
    private val autoSneakOnlyAwp = BoolValue("AutoSneakOnlyAwp", true)
     val rotateValue = BoolValue("SilentRotate", false)
    private val fireLimit = BoolValue("FireLimit", false)
    private val posYValue = FloatValue("FireLimit-TargetVelocityY", 0.8F, -1F, 1F).displayable { fireLimit.get() }
    private val fallValue = FloatValue("FireLimit-TargetFallVelocity", 0.8F, -1F, 1F).displayable { fireLimit.get() }
    private val timeLimitedPrediction = BoolValue("FireLimit-TimeLimitedPrediction", false).displayable { fireLimit.get() }
    private val timeLimitedPredictionTicksValue = IntegerValue("FireLimit-TimeLimitedPredictionTicks", 5, 0, 40).displayable { fireLimit.get() }
    private val maxRandomRange = IntegerValue("FireLimit-MaxRandomRange", 5, 0, 40).displayable { fireLimit.get() }
    private val minRandomRange = IntegerValue("FireLimit-MainRandomRange", 1, 0, 40).displayable { fireLimit.get() }
    private val timeLimitedAwpOnly = BoolValue("FireLimit-AwpOnly", true).displayable { fireLimit.get() }
    private val jitter = BoolValue("Jitter", false)
    private val jitterYaw = BoolValue("JitterYaw", true).displayable { jitter.get() }
    private val jitterPitch = BoolValue("JitterPitch", false).displayable { jitter.get() }
    private val jitterFrequency = IntegerValue("JitterFrequency", 1, 1, 40).displayable { jitter.get() }
    private val jitterAmplitude = IntegerValue("JitterAmplitude", 1, 1, 40).displayable { jitter.get() }
    private val targetDebug = BoolValue("TargetDebug", false)
    private val targetDebugMaxHurtTime = IntegerValue("TargetDebug-MaxHurtTime", 10, 1, 10)
    private val targetDebugMinHurtTime = IntegerValue("TargetDebug-MinHurtTime", 10, 1, 10)
    private val stopOnLiquid = BoolValue("RayTraceBlocks-StopOnLiquid", true)
    private val ignoreBlockWithoutBoundingBox = BoolValue("RayTraceBlocks-IgnoreBlockWithoutBoundingBox", true)
    private val returnLastUncollidableBlock = BoolValue("RayTraceBlocks-ReturnLastUncollidableBlock", false)
    private val hitBoxValue = BoolValue("HitBox", true)
     val head = BoolValue("HitBox-Head", true)
     val chest = BoolValue("HitBox-Chest", true)
     val feet = BoolValue("HitBox-Feet", true)
     val priority = ListValue("Priority", arrayOf("Head", "Chest", "Feet"), "Head")
    private val baseEyeHeightDetectionOffset = FloatValue("BaseEyeHeightDetectionOffset", 0F, -1F, 1F)
    private val chestDetectionOffset = FloatValue("ChestDetectionOffset", 0.8F, -1F, 1F)
    private val feetDetectionOffset = FloatValue("FeetDetectionOffset", 0F, -1F, 1F)
    private val chestAimOffset = FloatValue("ChestAimOffset", 0.8F, -1F, 1F)
    private val feetAimOffset = FloatValue("FeetAimOffset", 0F, -1F, 1F)
    private val boundingBox = BoolValue("BoundingBox", true)
    private val size = FloatValue("BoundingBoxSize", 0F, -1F, 1F)
    private val boundingBoxOffsetY = FloatValue("BoundingBoxOffsetY", 0F, -1F, 1F)
    private val hitEffect = BoolValue("HitEffect", true)
    private val hitEffectMode = ListValue("HitEffectMode", arrayOf("N1", "N2"), "N1")
    private val sneakYOffset = FloatValue("SneakYOffset", 0.2F, -1F, 1F)
    private val playerVecYVecYOffset = FloatValue("PlayerVecYVecYOffset", 0.2F, -1F, 1F)
    private val circleValue = BoolValue("Circle", true)
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() }
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }
    private val distanceOffsetValue = BoolValue("DistanceOffset", true)
    private val distanceOffsetMultiplier = FloatValue("DistanceOffsetMultiplier", 0.1F, 0.01F, 0.99F)
    private val distanceOffsetMaxRange = FloatValue("DistanceOffsetMaxRange", 75F, 1F, 100F)
    private val lineYOffset = FloatValue("LineYOffset", 1F, -10F, 10F)
    private var ac = 0
    var rageBotTargetPlayer: EntityPlayer? = null
    private var tick = 0
    private var ticks = 0
    private var type = 0
    private var time = 0
    private var resettick = 0
    private var jitterValue = 0
    private var jitterValue2 = 0
    private var jitterTick = 0
    private var jitterTick2 = 0
    private var boxX = 0
    private var boxZ = 0
    private var noSpreadTicks = 0

    override fun onDisable() {
        noSpreadTicks = 0
        ac = 0
        boxZ = 0
        boxX = 0
        jitterTick = 0
        jitterValue = 0
        resettick = 0
        type = 0
        ticks = 0
        tick = 0
        mc.gameSettings.keyBindUseItem.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING, 0))
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(2F)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        rageBotTargetPlayer?.let { render3DLine(it,Color.RED,lineYOffset.get().toDouble()) }
        rageBotTargetPlayer?.let { render3DLine(it,Color.RED,lineYOffset.get().toDouble()+ spreadOffset((noSpreadTick.get()))) }
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
                GL11.glVertex2f(
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
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        rageBotTargetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { (!autoRange.get() && it.getDistanceToEntityBox(player) <= range.get())
                    || (autoRange.get() && it.getDistanceToEntityBox(player) <= range.get()&&
                    (hasWeapon(WeaponType.AK)  && it.getDistanceToEntityBox(player) <= akRange.get()
                            || hasWeapon(WeaponType.M4)  && it.getDistanceToEntityBox(player) <= m4Range.get()
                            || hasWeapon(WeaponType.MP7)  && it.getDistanceToEntityBox(player) <= mp7Range.get()
                            || hasWeapon(WeaponType.P250)  &&!hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.AWP) && it.getDistanceToEntityBox(player) <= p250Range.get()
                            ||hasWeapon(WeaponType.DEAGLE)  &&!hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.AWP) && it.getDistanceToEntityBox(player) <= deagleRange.get()
                            || hasWeapon(WeaponType.SHOTGUN) && it.getDistanceToEntityBox(player) <= shotgunRange.get()
                            || hasWeapon(WeaponType.AWP) && it.getDistanceToEntityBox(player) <= awpRange.get()))}
            .firstOrNull {
                hitBoxValue.get() && (
                        chest.get() && canSeePart(player, it, TargetPart.CHEST) ||
                                (head.get() && canSeePart(player, it, TargetPart.HEAD)) ||
                                (feet.get() && canSeePart(player, it, TargetPart.FEET) ||
                                        canSeePlayer(player, it).first))
            }
        rageBotTargetPlayer?.let {
            if (hitEffect.get() && it.hurtTime == 10) {
                when (hitEffectMode.get()) {
                    "N1" -> LiquidBounce.tipSoundManager.hitSound1.asyncPlay()
                    "N2" -> LiquidBounce.tipSoundManager.hitSound2.asyncPlay()
                }
            }
            if (targetDebug.get() && it.hurtTime in targetDebugMinHurtTime.get()..targetDebugMaxHurtTime.get()) ChatPrint(
                "§f[§bHit§f] Name:${it.name} Health:${it.health}"
            )
            if (jitterPitch.get() && jitter.get()) jitterValue =
                if (jitterTick++ < jitterFrequency.get()) -jitterAmplitude.get() else {
                    jitterTick = 0; jitterAmplitude.get()
                }
            if (jitterYaw.get() && jitter.get()) jitterValue2 =
                if (jitterTick2++ < jitterFrequency.get()) -jitterAmplitude.get() else {
                    jitterTick2 = 0; jitterAmplitude.get()
                }
            if (autoSneak.get() && autoSneakTriggerMode.get() == "OnlyFire" && (!autoSneakOnlyAwp.get() || hasWeapon(
                    WeaponType.AWP
                ))
            ) if (autoSneakMode.get() == "Packet")
                mc.netHandler.addToSendQueue(
                    C0BPacketEntityAction(
                        player,
                        C0BPacketEntityAction.Action.START_SNEAKING,
                        0
                    )
                ) else mc.gameSettings.keyBindSneak.pressed = true

            val canSeeHead = head.get() && canSeePart(player, it, TargetPart.HEAD)
            val canSeeChest = chest.get() && canSeePart(player, it, TargetPart.CHEST)
            val canSeeFeet = feet.get() && canSeePart(player, it, TargetPart.FEET)
            val targetVecY = when {
                canSeeHead && canSeeChest && canSeeFeet -> getPriorityPosition(it, priority.get()).takeIf { it.isFinite() } ?: return
                canSeeHead && canSeeChest -> getPriorityPosition(it, priority.get().takeIf { it != "Feet" } ?: "Chest")
                canSeeHead && canSeeFeet -> getPriorityPosition(it, priority.get().takeIf { it != "Chest" } ?: "Feet")
                canSeeChest && canSeeFeet -> getPriorityPosition(it, priority.get().takeIf { it != "Head" } ?: "Feet")
                canSeeHead -> it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                canSeeChest -> it.posY + chestAimOffset.get()
                canSeeFeet -> it.posY + feetAimOffset.get()
                canSeePlayer(player, it).first ->  canSeePlayer(player, it).second.yCoord - boundingBoxOffsetY.get() + it.posY - it.prevPosY
                else -> return}

            val result = canSeePlayer(player, it).second
            val targetVecX = if (!canSeePart(player, it, TargetPart.HEAD) && !canSeePart(player, it, TargetPart.CHEST) && !canSeePart(player, it,
                    TargetPart.FEET
                )) if (canSeePlayer(player, it).first && boundingBox.get()) result.xCoord
            else if (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX
            else if (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX
            val targetVecZ = if (!canSeePart(player, it, TargetPart.HEAD)) if (canSeePlayer(player, it).first&&boundingBox.get()) result.zCoord
            else if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ
            else  if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ
            val targetVec = Vec3(targetVecX, targetVecY, targetVecZ)
            val playerVecY = if (mc.thePlayer.isSneaking) player.posY + player.eyeHeight - sneakYOffset.get() + playerVecYVecYOffset.get() else player.posY + player.eyeHeight + playerVecYVecYOffset.get()
            val playerVecX = if (playerPredict.get()) player.posX + (player.posX - player.prevPosX) * playerPredictSize.get() else player.posX
            val playerVecZ = if (playerPredict.get()) player.posZ + (player.posZ - player.prevPosZ) * playerPredictSize.get() else player.posZ


            val playerVec = Vec3(playerVecX, playerVecY, playerVecZ)
            val rotation = getRotationTo(playerVec, targetVec)
            val yaw = rotation.yaw + jitterValue2

            val offset = if (distanceOffsetValue.get()) distanceOffset(it.getDistanceToEntityBox(player), distanceOffsetMultiplier.get().toDouble(), distanceOffsetMaxRange.get().toDouble()) else 0.0

            val pitch = rotation.pitch + pitchOffset.get() + jitterValue + offset.toFloat()

            if (noSpreadValue.get() && noSpreadMode.get() == "Packet" && !hasWeapon(WeaponType.AWP)) {
                if (noSpreadTicks <= noSpreadTick.get()) {
                    packetSwitch()
                    noSpreadTicks++
                } else {
                    backPacket(3)
                    noSpreadTicks = 0
                }
            }

            if (noSpreadValue.get() && noSpreadMode.get() == "Switch" && !hasWeapon(WeaponType.AWP)) {
                if (noSpreadTicks <= noSpreadTick.get()) {
                    switch()
                    noSpreadTicks++
                } else {
                    back(3)
                    noSpreadTicks = 0
                }
            }

            if (rotateValue.get()) RotationUtils.setTargetRotation(Rotation(yaw, pitch)) else {
                mc.thePlayer.rotationYaw = yaw; mc.thePlayer.rotationPitch = pitch
            }
            when (fireMode.get()) {
                "Packet" -> {
                    if (timeLimitedAwpOnly.get() && hasWeapon(WeaponType.AWP)) {
                        if (it.onGround && timeLimitedPrediction.get()) {
                            time = if (time < timeLimitedPredictionTicksValue.get() + Random.nextInt(minRandomRange.get(), maxRandomRange.get())) time + 1 else 0
                            if (time == 0) if (targetThroughWallPredictFire.get() || canSeePart(player, it,
                                    TargetPart.HEAD
                                ))
                                fire()
                        } else if (!fireLimit.get() || (!it.onGround && (it.posY - it.prevPosY) in fallValue.get()..posYValue.get()))
                            if (targetThroughWallPredictFire.get() || canSeePart(player, it, TargetPart.HEAD))
                                fire()
                    } else fire()
                }

                else -> {
                    if (tick < fireTick.get()) tick++ else tick = 0
                    mc.gameSettings.keyBindUseItem.pressed = tick < fireTick.get()
                }
            }

        } ?: run {
            if (fireMode.get() == "Legit") mc.gameSettings.keyBindUseItem.pressed = false
            if (autoSneakMode.get() == "Packet") mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SNEAKING, 0))
            else mc.gameSettings.keyBindSneak.pressed = false
            ticks = 0;tick = 0;time = 0
        }
        if (!autoSneakOnlyAwp.get() || hasWeapon(WeaponType.AWP)) {
            if (autoSneak.get() && autoSneakTriggerMode.get() == "Always") {
                when (autoSneakMode.get()) {
                    "Packet" -> mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SNEAKING, 0))
                    else -> mc.gameSettings.keyBindSneak.pressed = true
                }
            }
        }
    }

    private fun getPredictedVec3(entity: EntityPlayer, predictSize: Double, predict: Boolean): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX = if (predict) entity.posX + (entity.posX - entity.prevPosX) * predictSize else entity.posX
        val vecZ = if (predict) entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize else entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getTargetPart(
        entity: EntityPlayer,
        part: getPart,
        predictSize: Double,
        predict: Boolean
    ): Vec3 {
        val vecX = if (predict) entity.posX + (entity.posX - entity.prevPosX) * predictSize else entity.posX
        val vecZ = if (predict) entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize else entity.posZ
        val vecY = when (part) {
            getPart.HEAD -> if (mc.thePlayer.isSneaking) entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get() - sneakYOffset.get() else entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
            getPart.FEET -> entity.posY + feetDetectionOffset.get()
            getPart.CHEST -> entity.posY + chestDetectionOffset.get()
        }
        return Vec3(vecX, vecY, vecZ)
    }
    private fun canSeePart(player: EntityPlayer, target: EntityPlayer, part: TargetPart): Boolean {
        val world = mc.theWorld
        val playerVec =
            getPredictedVec3(player, playerThroughWallPredictSize.get().toDouble(), playerThroughWallPredict.get())
        val targetVec = when (part) {
            TargetPart.HEAD -> getTargetPart(target,
                getPart.HEAD, targetThroughWallPredictSize.get().toDouble(),targetThroughWallPredict.get())
            TargetPart.FEET -> getTargetPart(target,
                getPart.FEET, targetThroughWallPredictSize.get().toDouble(),targetThroughWallPredict.get())
            TargetPart.CHEST -> getTargetPart(target,
                getPart.CHEST, targetThroughWallPredictSize.get().toDouble(),targetThroughWallPredict.get())
        }
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Pair<Boolean, Vec3> {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX,player.posY + player.eyeHeight + baseEyeHeightDetectionOffset.get(),player.posZ)
        val size = size.get().toDouble()
        val adjustedMinX = target.entityBoundingBox.minX + if (size > 0) size else 0.0
        val adjustedMinZ = target.entityBoundingBox.minZ + if (size > 0) size else 0.0
        val adjustedMaxX = target.entityBoundingBox.maxX - if (size > 0) size else 0.0
        val adjustedMaxZ = target.entityBoundingBox.maxZ - if (size > 0) size else 0.0
        val corners = listOf(
            Vec3(adjustedMinX, target.entityBoundingBox.minY + chestDetectionOffset.get(), adjustedMinZ),
            Vec3(adjustedMinX, target.entityBoundingBox.minY + chestDetectionOffset.get(), adjustedMaxZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), adjustedMinZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), adjustedMaxZ),
            Vec3(adjustedMinX, target.entityBoundingBox.minY, adjustedMinZ),
            Vec3(adjustedMinX, target.entityBoundingBox.minY, adjustedMaxZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.minY, adjustedMinZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.minY, adjustedMaxZ),
            Vec3(adjustedMinX, target.entityBoundingBox.maxY, adjustedMinZ),
            Vec3(adjustedMinX, target.entityBoundingBox.maxY, adjustedMaxZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.maxY, adjustedMinZ),
            Vec3(adjustedMaxX, target.entityBoundingBox.maxY, adjustedMaxZ)
        )
        for (corner in corners) {
            val result = world.rayTraceBlocks(playerVec, corner, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
            if (result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) return Pair(true, corner)
        }
        return Pair(false, Vec3(0.0,0.0,0.0))
    }
    private fun getPriorityPosition(it: Entity, priority: String): Double {
        return when (priority) {
            "Head" -> it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
            "Chest" -> it.posY + chestAimOffset.get()
            "Feet" -> it.posY + feetAimOffset.get()
            else -> Double.NaN
        }
    }

}