/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 * 所有rage模块以及utils均为None编写
 */
package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.Sprint
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.TargetPart
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.WeaponType
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.actions.*
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.getPart
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.math.vs
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.render.render3DLine
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.search.hasWeapon
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.special.distanceOffset
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.special.spreadOffset
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils.*
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MovementUtils.jumpMotion
import net.ccbluex.liquidbounce.utils.MovementUtils.movingYaw
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import net.minecraft.util.*
import org.lwjgl.opengl.GL11
import org.spongepowered.asm.mixin.Overwrite
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@ModuleInfo(name = "RageBot", category = ModuleCategory.Rage)
object RageBot : Module() {

    private var lastServerPos: Double? = null
    val pitchOffset = FloatValue("PitchOffset", 0.2F, -0.5F, 5F)
    val getPosMode = ListValue("PosMode", arrayOf("Pos", "ServerPos", "Mix","Custom"), "ServerPos")
    val getPlayerPosMode = ListValue("PlayerPosMode", arrayOf("Pos", "ServerPos", "Mix"), "ServerPos")
    val getVelocityMode = ListValue("VelocityMode", arrayOf("Pos", "ServerPos","MixServerPosAndPrevPos","MixPosAndLastTickPos","PracticalityMix","Custom"), "Pos")
    val scriptEngineMode = ListValue("ScriptEngineMode", arrayOf("JavaScript"), "JavaScript")
    val getVelocityModeCustomCode = TextValue("CustomVelocityCode","serverPos - lastTickPos")
    val getPosModeCustomCode = TextValue("CustomPosCode","serverPos")

    val visibilitySensitivityTick = IntegerValue("visibilitySensitivityTick", 1, 0, 10)
    val enableAccumulation = BoolValue("EnableAccumulation", true)
    private val eyeHeight = FloatValue("TargetEyeHeight", 0.8F, -1F, 1F)
    private val range = FloatValue("MaxRange", 70F, 50F, 200F)
    val rotateValue = BoolValue("SilentRotate", false)
    private val autoRange = BoolValue("AutoRange", true)
    private val akRange = FloatValue("AutoRange-CustomRange-AKRange", 50F, 20F, 100F).displayable {autoRange.get()}
    private val m4Range = FloatValue("AutoRange-CustomRange-M4Range", 50F, 20F, 100F).displayable {autoRange.get()}
    private val awpRange = FloatValue("AutoRange-CustomRange-AWPRange", 70F, 20F, 100F).displayable {autoRange.get()}
    private val mp7Range = FloatValue("AutoRange-CustomRange-MP7Range", 45F, 20F, 100F).displayable {autoRange.get()}
    private val p250Range = FloatValue("AutoRange-CustomRange-P250Range", 35F, 20F, 100F).displayable {autoRange.get()}
    private val deagleRange = FloatValue("AutoRange-CustomRange-DeagleRange", 40F, 20F, 100F).displayable {autoRange.get()}
    private val shotgunRange = FloatValue("AutoRange-CustomRange-ShotGunRange", 20F, 20F, 100F).displayable {autoRange.get()}

    private val targetPredict = BoolValue("TargetPredict", true)
    val targetPredictSize = FloatValue("TargetPredictSize", 4.3F, 0F, 10F).displayable { targetPredict.get() }
    private val targetPredictMaxVelocity = FloatValue("TargetPredictMaxVelocity", 10.0F, 0.01F, 10F).displayable { targetPredict.get() }
    private val targetPredictMinVelocity = FloatValue("TargetPredictMinVelocity", 10.0F, 0.01F, 10F).displayable { targetPredict.get() }
    private val playerPredict = BoolValue("PlayerPredict", false)
    private val playerPredictSize = FloatValue("PlayerPredictSize", 4.3F, 0F, 10F).displayable { playerPredict.get() }

    private val targetVisibilityPredict = BoolValue("TargetVisibilityPredict", true)
    private val targetVisibilityPredictSize = FloatValue("TargetVisibilityPredictSize", 1.5F, 1F, 10F).displayable { targetVisibilityPredict.get() }
    private val PlayerVisibilityPredict = BoolValue("PlayerVisibilityPredict", false)
    private val playerVisibilityPredictSize = FloatValue("PlayerVisibilityPredictSize", 1.5F, 1F, 10F).displayable { PlayerVisibilityPredict.get() }
    private val targetThroughWallPredictFire = BoolValue("TargetVisibilityPredictFire", true).displayable { targetPredict.get() }

    val fireMode = ListValue("FireMode", arrayOf("Legit", "Packet"), "Packet")
    val fireTick = IntegerValue("FireTick", 1, 0, 5).displayable { fireMode.get() == "Legit" }

    private val noSpreadValue = BoolValue("NoSpread", true)
    private val noSpreadMode = ListValue("NoSpreadMode", arrayOf("Switch", "Packet","SwitchOffsets","PacketOffsets"), "Switch").displayable {noSpreadValue.get()}
    val noSpreadTriggerMode = ListValue("NoSpreadTriggerMode", arrayOf("Fired", "Tick"), "Tick").displayable {noSpreadValue.get()}
    val noSpreadSwitchOffsetsPitchTick1 = FloatValue("noSpreadSwitchOffsetsPitchTick1", 0.1F, -1F, 1F).displayable {noSpreadValue.get()}
    val noSpreadSwitchOffsetsPitchTick2 = FloatValue("noSpreadSwitchOffsetsPitchTick2", 0.0F, -1F, 1F).displayable {noSpreadValue.get()}
    val noSpreadSwitchOffsetsYawTick1 = FloatValue("noSpreadSwitchOffsetsYawTick1", 0.0F, -2F, 2F).displayable {noSpreadValue.get()}
    val noSpreadSwitchOffsetsYawTick2 = FloatValue("noSpreadSwitchOffsetsYawTick2", 0.0F, -2F, 2F).displayable {noSpreadValue.get()}
    val noSpreadTick = IntegerValue("NoSpreadBaseTick", 2, 0, 5).displayable {noSpreadValue.get()}

    private val autoSneak = BoolValue("AutoSneak", true)
    private val autoSneakMode = ListValue("AutoSneakMode", arrayOf("Legit", "Packet"), "Packet").displayable {autoSneak.get()}
    private val autoSneakTriggerMode = ListValue("AutoSneakTriggerMode", arrayOf("Always", "OnlyFire"), "OnlyFire").displayable {autoSneak.get()}
    private val autoSneakOnlyAwp = BoolValue("AutoSneakOnlyAwp", true).displayable {autoSneak.get()}

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

    private val hitBoxValue = BoolValue("HitBox", true)
    val head = BoolValue("HitBox-Head", true).displayable { hitBoxValue.get() }
    val chest = BoolValue("HitBox-Chest", true).displayable { hitBoxValue.get() }
    val feet = BoolValue("HitBox-Feet", true).displayable { hitBoxValue.get() }
    val priority = ListValue("Priority", arrayOf("Head", "Chest", "Feet"), "Head").displayable { hitBoxValue.get() }
    private val baseEyeHeightDetectionOffset = FloatValue("BaseEyeHeightDetectionOffset", 0F, -1F, 1F).displayable { hitBoxValue.get() }
    private val chestDetectionOffset = FloatValue("ChestDetectionOffset", 0.8F, -1F, 1F).displayable { hitBoxValue.get() }
    private val feetDetectionOffset = FloatValue("FeetDetectionOffset", 0F, -1F, 1F).displayable { hitBoxValue.get() }
    private val chestAimOffset = FloatValue("ChestAimOffset", 0.8F, -1F, 1F).displayable { hitBoxValue.get() }
    private val feetAimOffset = FloatValue("FeetAimOffset", 0F, -1F, 1F).displayable { hitBoxValue.get() }

    private val boundingBox = BoolValue("BoundingBoxDetection", true)
    private val boundingBoxMode = ListValue("BoundingBoxDetectionMode", arrayOf("Full","4Points","8Points","12Points","4PointsCenters"), "12Points").displayable { boundingBox.get() }
    private val size = FloatValue("BoundingBoxDetectionSize", 0F, -1F, 1F).displayable { boundingBox.get() }
    private val boundingBoxOffsetY = FloatValue("BoundingBoxOffsetY", 0F, -1F, 1F).displayable { boundingBox.get() }

    private val distanceOffsetValue = BoolValue("DistanceOffset", true)
    private val distanceOffsetMultiplier = FloatValue("DistanceOffsetMultiplier", 0.1F, 0.01F, 0.99F).displayable { distanceOffsetValue.get() }
    private val distanceOffsetMaxRange = FloatValue("DistanceOffsetMaxRange", 75F, 1F, 100F).displayable { distanceOffsetValue.get() }

    private val sneakYOffset = FloatValue("SneakYOffset", 0.2F, -1F, 1F)
    private val playerVecYVecYOffset = FloatValue("PlayerVecYVecYOffset", 0.2F, -1F, 1F)

    private val stopOnLiquid = BoolValue("RayTraceBlocks-StopOnLiquid", true)
    private val ignoreBlockWithoutBoundingBox = BoolValue("RayTraceBlocks-IgnoreBlockWithoutBoundingBox", true)
    private val returnLastUncollidableBlock = BoolValue("RayTraceBlocks-ReturnLastUncollidableBlock", false)

    val delayControl = BoolValue("DelayControl", true)
    val delayControlPrediction = BoolValue("DelayControlPrediction", true)
    val delayControlPosMode = BoolValue("DelayControlPosMode", true)
    val delayControlPosModeMinDelayValue = IntegerValue("DelayControlPosModeMinDelayValue", 80, 1, 200)
    val delayControlPredictionModeMinDelayValue = IntegerValue("DelayControlPredictionModeMinDelayValue", 80, 1, 200)

    val acceleration = BoolValue("Acceleration", false)
    private val timeTick = IntegerValue("AccelerationLong", 2, 1, 10)

    val moveFix = BoolValue("MoveFix", true)
    private val sprint = BoolValue("Sprint", true)

    private val spreadDebug = BoolValue("SpreadDebug", false)
    private val accelerationDebug  = BoolValue("AccelerationDebug", false)
    private val velocityDebug = BoolValue("VelocityDebug", false)
    private val posDebug = BoolValue("PosDebug", false)
    private val targetDebug = BoolValue("TargetDebug", false)
    private val targetDebugMaxHurtTime = IntegerValue("TargetDebug-MaxHurtTime", 10, 1, 10)
    private val targetDebugMinHurtTime = IntegerValue("TargetDebug-MinHurtTime", 10, 1, 10)

    private val hitEffect = BoolValue("HitEffect", true)
    private val hitEffectMode = ListValue("HitEffectMode", arrayOf("Lighting", "Critical","Fire"), "N1").displayable { hitEffect.get() }
    private val lightingSoundValue = BoolValue("LightingSound", true).displayable { hitEffectMode.get() == "Lighting" }
    private val circleValue = BoolValue("Circle", true)
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() }
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }

    private val lineValue = BoolValue("Line", true)
    private val lineYOffset = FloatValue("LineYOffset", 1F, -10F, 10F)
    private val lineRedValue = IntegerValue("LineRed", 255, 0, 255).displayable { lineValue.get() }
    private val lineGreenValue = IntegerValue("LineGreen", 255, 0, 255).displayable { lineValue.get() }
    private val lineBlueValue = IntegerValue("LineBlue", 255, 0, 255).displayable { lineValue.get() }
    private val lineAlphaValue = IntegerValue("LineAlpha", 255, 0, 255).displayable { lineValue.get() }
    private val lineThicknessValue = FloatValue("LineThickness", 2F, 1F, 5F).displayable { lineValue.get() }

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
    var noSpreadTicks = 0
    var offsetPitch = 0.0f
    var offsetYaw = 0.0f
    override fun onDisable() {
        offsetPitch = 0.0f
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
        if (lineValue.get()) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(lineThicknessValue.get())
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDepthMask(false)
            rageBotTargetPlayer?.let { render3DLine(it, Color(lineRedValue.get(), lineGreenValue.get(), lineBlueValue.get()),lineAlphaValue.get(), lineYOffset.get().toDouble()) }
            rageBotTargetPlayer?.let {
                render3DLine(
                    it, Color(lineRedValue.get(), lineGreenValue.get(), lineBlueValue.get()), lineAlphaValue.get(),
                    lineYOffset.get().toDouble() + spreadOffset((noSpreadTick.get()))
                )
            }
        }
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
    fun onStrafe(event: StrafeEvent) {
        if (moveFix.get()) {
            val (yaw) = RotationUtils.targetRotation ?: return
            var strafe = event.strafe
            var forward = event.forward
            val friction = event.friction

            var f = strafe * strafe + forward * forward

            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt_float(f)

                if (f < 1.0F) {
                    f = 1.0F
                }

                f = friction / f
                strafe *= f
                forward *= f

                val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
            }
            event.cancelEvent()
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
                            || hasWeapon(WeaponType.P250)  &&!hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(player) <= p250Range.get()
                            ||hasWeapon(WeaponType.DEAGLE)  &&!hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(player) <= deagleRange.get()
                            || hasWeapon(WeaponType.SHOTGUN) && it.getDistanceToEntityBox(player) <= shotgunRange.get()
                            || hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(player) <= awpRange.get()))}
            .firstOrNull {
                hitBoxValue.get() && (chest.get() && canSeePart(player, it, TargetPart.CHEST) ||
                                (head.get() && canSeePart(player, it, TargetPart.HEAD)) ||
                                (feet.get() && canSeePart(player, it, TargetPart.FEET) ||
                                        canSeePlayer(player, it).first))
                vs(canSeePlayer(player, it).first)
            }
        rageBotTargetPlayer?.let {
            if (sprint.get() && !mc.thePlayer.isSprinting) mc.thePlayer.isSprinting = true else mc.thePlayer.isSprinting = false
            if (hitEffect.get() && it.hurtTime == 10) {
                when(hitEffectMode.get().lowercase()) {
                    "lighting" -> {
                        mc.netHandler.handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity(EntityLightningBolt(mc.theWorld, it.posX, it.posY, it.posZ)))
                        if(lightingSoundValue.get()) {
                            mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.explode"), 1.0f))
                            mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("ambient.weather.thunder"), 1.0f))
                        }
                    }
                    "fire" ->
                        mc.effectRenderer.emitParticleAtEntity(it, EnumParticleTypes.LAVA)
                    "critical" -> mc.effectRenderer.emitParticleAtEntity(it, EnumParticleTypes.CRIT)
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
                    WeaponType.SNIPER
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
                canSeeHead -> it.posY + it.eyeHeight * eyeHeight.get() + velocityY(it)
                canSeeChest -> it.posY + chestAimOffset.get()
                canSeeFeet -> it.posY + feetAimOffset.get()
                canSeePlayer(player, it).first ->  canSeePlayer(player, it).second.yCoord - boundingBoxOffsetY.get() + velocityY(it)
                else -> return}

            val result = canSeePlayer(player, it).second

            val size = if (delayControl.get() && delayControlPrediction.get() && ping() > delayControlPredictionModeMinDelayValue.get()) ping() / 200 else 0

            val targetVecX=
                if (!canSeePart(player, it, TargetPart.HEAD) && !canSeePart(player, it, TargetPart.CHEST) && !canSeePart(player, it, TargetPart.FEET) && canSeePlayer(player, it).first && boundingBox.get()) result.xCoord
            else if (targetPredict.get() && velocityABSX(it) in targetPredictMinVelocity.get()..targetPredictMaxVelocity.get())
                if (acceleration.get()) acceleration(it, (targetPredictSize.get().toDouble() + size),timeTick.get()).xCoord else posX(it) + velocityX(it) * (targetPredictSize.get() + size)
                else posX(it)

            val targetVecZ= if (!canSeePart(player, it, TargetPart.HEAD) && !canSeePart(player, it, TargetPart.CHEST) && !canSeePart(player, it, TargetPart.FEET) && canSeePlayer(player, it).first && boundingBox.get()) result.zCoord
            else if (targetPredict.get() && velocityABSZ(it) in targetPredictMinVelocity.get()..targetPredictMaxVelocity.get())
               if (acceleration.get()) acceleration(it, (targetPredictSize.get().toDouble() + size),timeTick.get()).zCoord else posZ(it) + velocityZ(it) * (targetPredictSize.get() + size)
            else posZ(it)

            val targetVec = Vec3(targetVecX, targetVecY, targetVecZ)

            val playerVecY = if (mc.thePlayer.isSneaking) playerPosY() + player.eyeHeight - sneakYOffset.get() + playerVecYVecYOffset.get() else playerPosY() + player.eyeHeight + playerVecYVecYOffset.get()
            val playerVecX = if (playerPredict.get()) playerPosX() + velocityX(it) * playerPredictSize.get() else playerPosX()
            val playerVecZ = if (playerPredict.get()) playerPosZ() + velocityZ(it) * playerPredictSize.get() else playerPosZ()

            if(posDebug.get()) ChatPrint("PosX:${posX(it)} PosY:${posY(it)} PosZ:${posZ(it)}")
            if(velocityDebug.get()) ChatPrint("VelocityX:${velocityABSX(it)} VelocityY:${velocityABSY(it)} VelocityZ:${velocityABSZ(it)}")
            val playerVec = Vec3(playerVecX, playerVecY, playerVecZ)
            val offset = if (distanceOffsetValue.get()) distanceOffset(it.getDistanceToEntityBox(player), distanceOffsetMultiplier.get().toDouble(), distanceOffsetMaxRange.get().toDouble()) else 0.0
            if (noSpreadValue.get() && !hasWeapon(WeaponType.SNIPER)) {
                if(spreadDebug.get()) ChatPrint("SpreadTick:$noSpreadTicks")
                when (noSpreadMode.get()) {
                    "Packet" -> handlePacketAction()
                    "Switch" -> handleSwitchAction()
                    "SwitchOffsets" -> handleSwitchOffsetsAction()
                    "PacketOffsets" -> handlePacketOffsetsAction()
                }
            }

            val rotation = getRotationTo(playerVec, targetVec)
            val yaw = rotation.yaw + jitterValue2
            val pitch = rotation.pitch + pitchOffset.get() + offsetPitch + jitterValue + offset.toFloat()
            if (rotateValue.get()){ RotationUtils.setTargetRotation(Rotation(yaw, pitch))
            }else {
                mc.thePlayer.rotationYaw = yaw; mc.thePlayer.rotationPitch = pitch
            }
            when (fireMode.get()) {
                "Packet" -> {
                    if (timeLimitedAwpOnly.get() && hasWeapon(WeaponType.SNIPER)) {
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
        if (!autoSneakOnlyAwp.get() || hasWeapon(WeaponType.SNIPER)) {
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
    fun update(currentServerPos:Double):Boolean{
        if (lastServerPos == null || currentServerPos != lastServerPos) {
            lastServerPos = currentServerPos
            return true
        }else return false
    }
    private fun canSeePart(player: EntityPlayer, target: EntityPlayer, part: TargetPart): Boolean {
        val world = mc.theWorld
        val playerVec =
            getPredictedVec3(player, playerVisibilityPredictSize.get().toDouble(), PlayerVisibilityPredict.get())
        val targetVec = when (part) {
            TargetPart.HEAD -> getTargetPart(target,
                getPart.HEAD, targetVisibilityPredictSize.get().toDouble(),targetVisibilityPredict.get())
            TargetPart.FEET -> getTargetPart(target,
                getPart.FEET, targetVisibilityPredictSize.get().toDouble(),targetVisibilityPredict.get())
            TargetPart.CHEST -> getTargetPart(target,
                getPart.CHEST, targetVisibilityPredictSize.get().toDouble(),targetVisibilityPredict.get())
        }
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }

    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Pair<Boolean, Vec3> {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX,player.posY + player.eyeHeight + baseEyeHeightDetectionOffset.get(),player.posZ)
        val size = size.get().toDouble()
        val minX = target.entityBoundingBox.minX + if (size > 0) size else 0.0
        val minZ = target.entityBoundingBox.minZ + if (size > 0) size else 0.0
        val maxX = target.entityBoundingBox.maxX - if (size > 0) size else 0.0
        val maxZ = target.entityBoundingBox.maxZ - if (size > 0) size else 0.0
        val minY = target.entityBoundingBox.minY + if (size > 0) size else 0.0
        val maxY = target.entityBoundingBox.maxY + if (size > 0) size else 0.0
        val x = target.posX
        val z = target.posZ
        val corners = when(boundingBoxMode.get()) {
            "12Points" -> listOf(
                Vec3(minX, target.entityBoundingBox.minY + chestDetectionOffset.get(), minZ),
                Vec3(minX, target.entityBoundingBox.minY + chestDetectionOffset.get(), maxZ),
                Vec3(maxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), minZ),
                Vec3(maxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), maxZ),
                Vec3(minX, target.entityBoundingBox.minY, minZ),
                Vec3(minX, target.entityBoundingBox.minY, maxZ),
                Vec3(maxX, target.entityBoundingBox.minY, minZ),
                Vec3(maxX, target.entityBoundingBox.minY, maxZ),
                Vec3(minX, target.entityBoundingBox.maxY, minZ),
                Vec3(minX, target.entityBoundingBox.maxY, maxZ),
                Vec3(maxX, target.entityBoundingBox.maxY, minZ),
                Vec3(maxX, target.entityBoundingBox.maxY, maxZ)
            )
            "8Points" -> listOf(
                Vec3(minX, target.entityBoundingBox.minY, minZ),
                Vec3(minX, target.entityBoundingBox.minY, maxZ),
                Vec3(maxX, target.entityBoundingBox.minY, minZ),
                Vec3(maxX, target.entityBoundingBox.minY, maxZ),
                Vec3(minX, target.entityBoundingBox.maxY, minZ),
                Vec3(minX, target.entityBoundingBox.maxY, maxZ),
                Vec3(maxX, target.entityBoundingBox.maxY, minZ),
                Vec3(maxX, target.entityBoundingBox.maxY, maxZ)
            )
            "4Points" -> listOf(
                Vec3(minX, target.entityBoundingBox.minY + chestDetectionOffset.get(), minZ),
                Vec3(minX, target.entityBoundingBox.minY + chestDetectionOffset.get(), maxZ),
                Vec3(maxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), minZ),
                Vec3(maxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), maxZ),
            )
            "4PointsCenters" -> listOf(
                Vec3(maxX, target.entityBoundingBox.minY + chestDetectionOffset.get(), z),
                Vec3(minX, target.entityBoundingBox.minY + chestDetectionOffset.get(), z),
                Vec3(x, target.entityBoundingBox.minY + chestDetectionOffset.get(), maxZ),
                Vec3(x, target.entityBoundingBox.minY + chestDetectionOffset.get(), minZ),
            )
            else -> return Pair(false,Vec3(0.0,0.0,0.0))
        }
        if (boundingBoxMode.get() != "Full") {
            for (corner in corners) {
                val result = world.rayTraceBlocks(
                    playerVec,
                    corner,
                    stopOnLiquid.get(),
                    ignoreBlockWithoutBoundingBox.get(),
                    returnLastUncollidableBlock.get()
                )
                if (result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) return Pair(
                    true,
                    corner
                )
            }
        }else{

        }

        return Pair(false, Vec3(0.0,0.0,0.0))
    }
    fun moveFix(yaw : Float) {
        if (mc.thePlayer.isSprinting) {
            mc.thePlayer.motionX -= (MathHelper.sin(yaw / 180f * 3.1415927f) * 0.1f).toDouble()
            mc.thePlayer.motionZ += (MathHelper.cos(yaw / 180f * 3.1415927f) * 0.1f).toDouble()
        }
    }
    var Stime = 0
    var Dtime = 0
    var SvelocityX = 0.0
    var SvelocityY = 0.0
    var SvelocityZ = 0.0
    var EvelocityX = 0.0
    var EvelocityY = 0.0
    var EvelocityZ = 0.0
    var DvelocityX = 0.0
    var DvelocityY = 0.0
    var DvelocityZ = 0.0
    var got = false
    var aX = 0.0
    var aY = 0.0
    var aZ = 0.0

   private fun acceleration(player: EntityPlayer, n: Double, time:Int): Vec3 {
        if (Stime <= time) {
            if (!got){
                got = true
                SvelocityX = velocityX(player)
                SvelocityY = velocityY(player)
                SvelocityZ = velocityZ(player)
            }
            //正在
            Stime++
        } else {
            EvelocityX = velocityX(player)
            EvelocityY = velocityY(player)
            EvelocityZ = velocityZ(player)
            //结束
            Stime = 0

            got = false
            DvelocityX = EvelocityX - SvelocityX
            DvelocityY = EvelocityY - SvelocityY
            DvelocityZ = EvelocityZ - SvelocityZ
            Dtime = time - Stime
             aX = DvelocityX / Dtime
             aY = DvelocityY / Dtime
             aZ = DvelocityZ / Dtime

        }

       if (accelerationDebug.get()) ChatPrint("$aX , $aY , $aZ")


        val serverPosX = posX(player)
        val serverPosY = posY(player)
        val serverPosZ = posZ(player)

        val velocityX = velocityX(player)
        val velocityY = velocityY(player)
        val velocityZ = velocityZ(player)


        val predictedPosX = serverPosX + (velocityX + aX)*n
        val predictedPosY = serverPosY + (velocityY + aY)*n
        val predictedPosZ = serverPosZ + (velocityZ + aZ)*n


        return Vec3(predictedPosX, predictedPosY, predictedPosZ)
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