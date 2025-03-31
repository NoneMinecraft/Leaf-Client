/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 * 所有rage模块以及utils均为None编写
 */
package net.nonemc.leaf.features.module.modules.rage

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.util.*
import net.nonemc.leaf.data.Rotation
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.Util.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.rage.rage.TargetPart
import net.nonemc.leaf.features.module.modules.rage.rage.WeaponType
import net.nonemc.leaf.features.module.modules.rage.rage.entity.AxisAlignedBB.Companion.serverBoundingBox
import net.nonemc.leaf.features.module.modules.rage.rage.entity.getCenter
import net.nonemc.leaf.features.module.modules.rage.rage.entity.setMaxY
import net.nonemc.leaf.features.module.modules.rage.rage.handle.*
import net.nonemc.leaf.features.module.modules.rage.rage.math.vs
import net.nonemc.leaf.features.module.modules.rage.rage.search.hasWeapon
import net.nonemc.leaf.features.module.modules.rage.rage.special.distanceOffset
import net.nonemc.leaf.features.module.modules.rage.rage.utils.*
import net.nonemc.leaf.utils.entity.EntityUtils
import net.nonemc.leaf.utils.rotation.RotationUtils
import net.nonemc.leaf.utils.extensions.getDistanceToEntityBox
import net.nonemc.leaf.utils.rotation.getRotationTo
import net.nonemc.leaf.value.*
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@ModuleInfo(name = "RageBot", category = ModuleCategory.Rage)
object RageBot : Module() {
    //HitBox
    val priority = ListValue("HitBox-Priority", arrayOf("Head", "Chest", "Feet"), "Head")
    private val headAimOffset = FloatValue("HeadAimOffset", 0F, -1F, 1F)
    private val chestAimOffset = FloatValue("ChestAimOffset", 0F, -1F, 1F)
    private val feetAimOffset = FloatValue("FeetAimOffset", 0F, -1F, 1F)

    //Pos
    val getPosMode = ListValue("TargetPosMode", arrayOf("Pos", "ServerPos", "Custom"), "ServerPos")
    val getPlayerPosMode = ListValue("PlayerPosMode", arrayOf("Pos", "ServerPos"), "Pos")
    val getVelocityMode = ListValue(
        "VelocityMode",
        arrayOf("Pos", "ServerPos", "MixServerPosAndPrevPos", "MixPosAndLastTickPos", "PracticalityMix", "Custom"),
        "Pos"
    )
    val scriptEngineMode = ListValue(
        "ScriptEngineMode",
        arrayOf("JavaScript"),
        "JavaScript"
    ).displayable { getVelocityMode.get() == "Custom" || getPosMode.get() == "Custom" }
    val getPosModeCustomCode =
        TextValue("CustomTargetPosCode", "serverPos").displayable { getPosMode.get() == "Custom" }
    val getVelocityModeCustomCode =
        TextValue("CustomVelocityCode", "serverPos - lastTickPos").displayable { getVelocityMode.get() == "Custom" }

    //Range
    private val range = FloatValue("MaxRange", 70F, 50F, 200F)
    private val autoRange = BoolValue("AutoRange", true)
    private val akRange = FloatValue("AutoRange-CustomRange-AKRange", 50F, 20F, 100F).displayable { autoRange.get() }
    private val m4Range = FloatValue("AutoRange-CustomRange-M4Range", 50F, 20F, 100F).displayable { autoRange.get() }
    private val awpRange = FloatValue("AutoRange-CustomRange-AWPRange", 70F, 20F, 100F).displayable { autoRange.get() }
    private val mp7Range = FloatValue("AutoRange-CustomRange-MP7Range", 45F, 20F, 100F).displayable { autoRange.get() }
    private val p250Range =
        FloatValue("AutoRange-CustomRange-P250Range", 35F, 20F, 100F).displayable { autoRange.get() }
    private val deagleRange =
        FloatValue("AutoRange-CustomRange-DeagleRange", 40F, 20F, 100F).displayable { autoRange.get() }
    private val shotgunRange =
        FloatValue("AutoRange-CustomRange-ShotGunRange", 20F, 20F, 100F).displayable { autoRange.get() }

    //Predict
    private val targetPredict = BoolValue("TargetPredict", true)
    private val predictMode =
        ListValue("PredictMode", arrayOf("Velocity"), "Velocity").displayable { targetPredict.get() }
    private val targetPredictY = BoolValue("TargetPredictY", false)
    val targetPredictSize = FloatValue("TargetPredictSize", 4.3F, 0F, 10F).displayable { targetPredict.get() }
    private val targetPredictMaxVelocity =
        FloatValue("TargetPredictMaxVelocity", 10.0F, 0.01F, 10F).displayable { targetPredict.get() }
    private val targetPredictMinVelocity =
        FloatValue("TargetPredictMinVelocity", 0.01F, 0.01F, 10F).displayable { targetPredict.get() }

    private val targetVisibilityPredict = BoolValue("TargetVisibilityPredict", true)
    private val targetVisibilityPredictSize =
        FloatValue("TargetVisibilityPredictSize", 1.5F, 1F, 10F).displayable { targetVisibilityPredict.get() }
    private val targetThroughWallPredictFire =
        BoolValue("TargetVisibilityPredictFire", true).displayable { targetVisibilityPredict.get() }

    private val playerPredict = BoolValue("PlayerPredict", false)
    private val playerPredictSize = FloatValue("PlayerPredictSize", 4.3F, 0F, 10F).displayable { playerPredict.get() }

    //Visibilityw
    val visibilitySensitivityTick = IntegerValue("VisibilitySensitivityTick", 0, 0, 10)
    val enableAccumulation = BoolValue("EnableSensitivityAccumulation", true)
    private val boundingBox = BoolValue("BoundingBoxDetection", true)
    private val size =
        FloatValue("BoundingBoxDetection-SizeReductionValue", 0F, -1F, 1F).displayable { boundingBox.get() }
    private val boundingBoxStep =
        FloatValue("BoundingBoxDetection-Step", 0.15F, 0.05F, 0.5F).displayable { boundingBox.get() }

    //Fire
    val fireMode = ListValue("FireMode", arrayOf("Legit", "Packet"), "Packet")
    val fireTick = IntegerValue("FireTick", 1, 0, 5).displayable { fireMode.get() == "Legit" }

    //NoSpread
    private val noSpreadValue = BoolValue("NoSpread", true)
    private val noSpreadMode = ListValue(
        "NoSpread-Mode",
        arrayOf("Switch", "Packet", "SwitchOffsets", "PacketOffsets"),
        "Switch"
    ).displayable { noSpreadValue.get() }
    val noSpreadTriggerMode =
        ListValue("NoSpread-TriggerMode", arrayOf("Fired", "Tick"), "Tick").displayable { noSpreadValue.get() }
    val noSpreadTick = IntegerValue("NoSpread-Tick", 2, 0, 5).displayable { noSpreadValue.get() }
    val noSpreadSwitchOffsetsPitchTick1 = FloatValue(
        "NoSpread-MinOffsetsPitch",
        0.1F,
        -1F,
        1F
    ).displayable { noSpreadValue.get() && (noSpreadMode.get() == "SwitchOffsets" || noSpreadMode.get() == "PacketOffsets") }
    val noSpreadSwitchOffsetsPitchTick2 = FloatValue(
        "NoSpread-MaxOffsetsPitch",
        0.0F,
        -1F,
        1F
    ).displayable { noSpreadValue.get() && (noSpreadMode.get() == "SwitchOffsets" || noSpreadMode.get() == "PacketOffsets") }
    val noSpreadSwitchOffsetsYawTick1 = FloatValue(
        "NoSpread-MinOffsetsYaw",
        0.0F,
        -2F,
        2F
    ).displayable { noSpreadValue.get() && (noSpreadMode.get() == "SwitchOffsets" || noSpreadMode.get() == "PacketOffsets") }
    val noSpreadSwitchOffsetsYawTick2 = FloatValue(
        "NoSpread-MaxOffsetsYaw",
        0.0F,
        -2F,
        2F
    ).displayable { noSpreadValue.get() && (noSpreadMode.get() == "SwitchOffsets" || noSpreadMode.get() == "PacketOffsets") }

    //FireLimit
    private val fireLimit = BoolValue("FireLimit", false)
    private val posYValue = FloatValue("FireLimit-TargetRiseVelocityY", 0.8F, -1F, 1F).displayable { fireLimit.get() }
    private val fallValue = FloatValue("FireLimit-TargetFallVelocity", 0.8F, -1F, 1F).displayable { fireLimit.get() }
    private val timeLimitedPrediction =
        BoolValue("FireLimit-TimeLimitedPrediction", false).displayable { fireLimit.get() }
    private val timeLimitedPredictionTicksValue =
        IntegerValue("FireLimit-TimeLimitedPredictionTicks", 5, 0, 40).displayable { fireLimit.get() }
    private val maxRandomRange = IntegerValue("FireLimit-MaxRandomRange", 5, 0, 40).displayable { fireLimit.get() }
    private val minRandomRange = IntegerValue("FireLimit-MinRandomRange", 1, 0, 40).displayable { fireLimit.get() }
    private val timeLimitedAwpOnly = BoolValue("FireLimit-AwpOnly", true).displayable { fireLimit.get() }

    //DistanceOffset
    private val distanceOffsetValue = BoolValue("DistanceOffset", true)
    private val distanceOffsetMultiplier =
        FloatValue("DistanceOffset-Multiplier", 0.1F, 0.01F, 2F).displayable { distanceOffsetValue.get() }
    private val distanceOffsetMaxRange =
        FloatValue("DistanceOffset-MaxRange", 75F, 1F, 100F).displayable { distanceOffsetValue.get() }

    //Sneak
    private val autoSneak = BoolValue("AutoSneak", true)
    private val autoSneakMode =
        ListValue("AutoSneak-Mode", arrayOf("Legit", "Packet"), "Legit").displayable { autoSneak.get() }
    private val autoSneakTriggerMode =
        ListValue("AutoSneak-TriggerMode", arrayOf("Always", "OnlyFire"), "OnlyFire").displayable { autoSneak.get() }
    private val autoSneakOnlyAwp = BoolValue("AutoSneak-OnlyAwp", true).displayable { autoSneak.get() }

    //delayControl
    private val delayControl = BoolValue("DelayControl", true)
    private val delayControlPrediction = BoolValue("DelayControlPrediction", true).displayable { delayControl.get() }
    private val delayControlPredictionMinDelayValue =
        IntegerValue("DelayControlPrediction-MinDelay", 250, 0, 1000).displayable { delayControl.get() }

    //Jitter
    private val jitter = BoolValue("Jitter", false)
    private val jitterYaw = BoolValue("JitterYaw", true).displayable { jitter.get() }
    private val jitterPitch = BoolValue("JitterPitch", false).displayable { jitter.get() }
    private val jitterFrequency = IntegerValue("JitterFrequency", 1, 1, 40).displayable { jitter.get() }
    private val jitterAmplitude = IntegerValue("JitterAmplitude", 1, 1, 40).displayable { jitter.get() }

    //RayTrace
    private val stopOnLiquid = BoolValue("RayTraceBlocks-StopOnLiquid", true)
    private val ignoreBlockWithoutBoundingBox = BoolValue("RayTraceBlocks-IgnoreBlockWithoutBoundingBox", true)
    private val returnLastUncollidableBlock = BoolValue("RayTraceBlocks-ReturnLastUncollidableBlock", false)

    //Rotate
    val rotateValue = BoolValue("SilentRotate", false)
    private val strictStrafe = BoolValue("StrictStrafe", true).displayable { rotateValue.get() }

    //Other
    private val sprint = BoolValue("Sprint", true)
    val pitchOffset = FloatValue("PitchOffset", 0.0F, -5F, 5F)
    private val sneakYOffset = FloatValue("SneakYOffset", 0.2F, -1F, 1F)
    private val playerVecYVecYOffset = FloatValue("PlayerYOffset", 0.2F, -1F, 1F)
    private val removeGUI = BoolValue("RemoveGUI", true)

    //Debug
    private val spreadDebug = BoolValue("SpreadDebug", false)
    private val velocityDebug = BoolValue("VelocityDebug", false)
    private val posDebug = BoolValue("PosDebug", false)
    private val targetDebug = BoolValue("TargetDebug", false)
    private val targetDebugMaxHurtTime =
        IntegerValue("TargetDebug-MaxHurtTime", 10, 1, 10).displayable { targetDebug.get() }
    private val targetDebugMinHurtTime =
        IntegerValue("TargetDebug-MinHurtTime", 10, 1, 10).displayable { targetDebug.get() }

    //Render
    private val hitEffect = BoolValue("HitEffect", true)
    private val hitEffectMode =
        ListValue("HitEffectMode", arrayOf("Lighting", "Critical", "Fire"), "Lighting").displayable { hitEffect.get() }
    private val lightingSoundValue = BoolValue("LightingSound", true).displayable { hitEffectMode.get() == "Lighting" }
    private val circleValue = BoolValue("Circle", true)
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() }
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }


    private var part: TargetPart = TargetPart.HEAD
    private var ac = 0
    private var tick = 0
    private var ticks = 0
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
    var rageBotTargetPlayer: EntityPlayer? = null
    override fun onDisable() {
        offsetYaw = 0.0f
        offsetPitch = 0.0f
        noSpreadTicks = 0
        ac = 0
        boxZ = 0
        boxX = 0
        jitterTick = 0
        jitterValue = 0
        jitterValue2 = 0
        jitterTick2 = 0
        resettick = 0
        ticks = 0
        tick = 0
        mc.gameSettings.keyBindUseItem.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)
        stopSneak()
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
        if (strictStrafe.get()) {
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
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S2DPacketOpenWindow && removeGUI.get()) {
            val windowTitle = packet.windowTitle ?: return
            if (windowTitle.unformattedText.contains("Items", ignoreCase = true)) {
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        updatePart()
        rageBotTargetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter {
                (!autoRange.get() && it.getDistanceToEntityBox(player) <= range.get())
                        || (autoRange.get() && it.getDistanceToEntityBox(player) <= range.get() &&
                        (hasWeapon(WeaponType.AK) && it.getDistanceToEntityBox(player) <= akRange.get()
                                || hasWeapon(WeaponType.M4) && it.getDistanceToEntityBox(player) <= m4Range.get()
                                || hasWeapon(WeaponType.MP7) && it.getDistanceToEntityBox(player) <= mp7Range.get()
                                || hasWeapon(WeaponType.P250) && !hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(
                            player
                        ) <= p250Range.get()
                                || hasWeapon(WeaponType.DEAGLE) && !hasWeapon(WeaponType.RIFLE) && !hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(
                            player
                        ) <= deagleRange.get()
                                || hasWeapon(WeaponType.SHOTGUN) && it.getDistanceToEntityBox(player) <= shotgunRange.get()
                                || hasWeapon(WeaponType.SNIPER) && it.getDistanceToEntityBox(player) <= awpRange.get()))
            }
            .firstOrNull {
                vs(canSeePlayer(player, it, part).first)
            }
        rageBotTargetPlayer?.let {
            var predictX = 0.0
            var predictY = 0.0
            var predictZ = 0.0
            handleNoSpreadMode()
            handleFire(player, it)
            resetSprint()
            displayHitEffect(it)
            displayDebug(it)
            setJitterValue()
            handleSneak(player)

            val result = canSeePlayer(player, it, part).second
            val size =
                if (delayControl.get() && delayControlPrediction.get() && ping() > delayControlPredictionMinDelayValue.get()) ping() / 200 else 0

            if (targetPredict.get() && velocityABSX(it) in targetPredictMinVelocity.get()..targetPredictMaxVelocity.get()
                && velocityABSY(it) in targetPredictMinVelocity.get()..targetPredictMaxVelocity.get()
                && velocityABSZ(it) in targetPredictMinVelocity.get()..targetPredictMaxVelocity.get()
            ) {
                when (predictMode.get()) {
                    "Velocity" -> {
                        predictX = velocityX(it) * (targetPredictSize.get() + size)
                        predictY = if (targetPredictY.get()) velocityY(it) * (targetPredictSize.get() + size) else 0.0
                        predictZ = velocityZ(it) * (targetPredictSize.get() + size)
                    }
                }
            } else {
                predictX = 0.0
                predictY = 0.0
                predictZ = 0.0
            }

            val targetVecX = result.xCoord + predictX
            val targetVecY = result.yCoord + predictY
            val targetVecZ = result.zCoord + predictZ

            val playerVecX = if (playerPredict.get()) {
                playerPosX() + velocityX(it) * playerPredictSize.get()
            } else playerPosX()

            val playerVecY = if (mc.thePlayer.isSneaking) {
                playerPosY() + player.eyeHeight - sneakYOffset.get() + playerVecYVecYOffset.get()
            } else playerPosY() + player.eyeHeight + playerVecYVecYOffset.get()

            val playerVecZ = if (playerPredict.get()) {
                playerPosZ() + velocityZ(it) * playerPredictSize.get()
            } else playerPosZ()

            val offset = if (distanceOffsetValue.get()) {
                distanceOffset(
                    it.getDistanceToEntityBox(player),
                    distanceOffsetMultiplier.get().toDouble(),
                    distanceOffsetMaxRange.get().toDouble()
                )
            } else 0.0

            val targetVec = Vec3(targetVecX, targetVecY, targetVecZ)
            val playerVec = Vec3(playerVecX, playerVecY, playerVecZ)
            val rotation = getRotationTo(playerVec, targetVec)
            val yaw = rotation.yaw + jitterValue2
            val pitch = rotation.pitch + pitchOffset.get() + offsetPitch + jitterValue + offset.toFloat()
            if (rotateValue.get()) {
                RotationUtils.setTargetRotation(Rotation(yaw, pitch))
            } else {
                mc.thePlayer.rotationYaw = yaw
                mc.thePlayer.rotationPitch = pitch
            }
        } ?: run {
            resetValue(player)
        }
        updateSneak(player)
    }

    private fun updateSneak(player: EntityPlayer) {
        if (!autoSneakOnlyAwp.get() || hasWeapon(WeaponType.SNIPER)) {
            if (autoSneak.get() && autoSneakTriggerMode.get() == "Always") {
                when (autoSneakMode.get()) {
                    "Packet" -> mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            player,
                            C0BPacketEntityAction.Action.START_SNEAKING,
                            0
                        )
                    )

                    else -> mc.gameSettings.keyBindSneak.pressed = true
                }
            }
        }
    }

    private fun resetSprint() {
        mc.thePlayer.isSprinting = sprint.get() && !mc.thePlayer.isSprinting
    }

    private fun displayDebug(it: EntityPlayer) {
        if (targetDebug.get() && it.hurtTime in targetDebugMinHurtTime.get()..targetDebugMaxHurtTime.get()) ChatPrint("§f[§bHit§f] Name:${it.name} Health:${it.health}")
        if (posDebug.get()) ChatPrint("PosX:${posX(it)} PosY:${posY(it)} PosZ:${posZ(it)}")
        if (velocityDebug.get()) ChatPrint(
            "VelocityX:${velocityABSX(it)} VelocityY:${velocityABSY(it)} VelocityZ:${
                velocityABSZ(
                    it
                )
            }"
        )
        if (spreadDebug.get()) ChatPrint("SpreadTick:$noSpreadTicks")
    }

    private fun updatePart() {
        part = when (priority.get().lowercase()) {
            "head" -> TargetPart.HEAD
            "chest" -> TargetPart.CHEST
            "feet" -> TargetPart.FEET
            else -> TargetPart.HEAD
        }
    }

    private fun displayHitEffect(it: EntityPlayer) {
        if (hitEffect.get() && it.hurtTime == 10) {
            when (hitEffectMode.get().lowercase()) {
                "lighting" -> {
                    mc.netHandler.handleSpawnGlobalEntity(
                        S2CPacketSpawnGlobalEntity(
                            EntityLightningBolt(
                                mc.theWorld,
                                it.posX,
                                it.posY,
                                it.posZ
                            )
                        )
                    )
                    if (lightingSoundValue.get()) {
                        mc.soundHandler.playSound(
                            PositionedSoundRecord.create(
                                ResourceLocation("random.explode"),
                                1.0f
                            )
                        )
                        mc.soundHandler.playSound(
                            PositionedSoundRecord.create(
                                ResourceLocation("ambient.weather.thunder"),
                                1.0f
                            )
                        )
                    }
                }

                "fire" -> mc.effectRenderer.emitParticleAtEntity(it, EnumParticleTypes.LAVA)
                "critical" -> mc.effectRenderer.emitParticleAtEntity(it, EnumParticleTypes.CRIT)
            }
        }
    }

    private fun resetValue(player: EntityPlayer) {
        if (fireMode.get() == "Legit") mc.gameSettings.keyBindUseItem.pressed = false
        if (autoSneakMode.get() == "Packet") {
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SNEAKING, 0))
        } else {
            stopSneak()
        }
        ticks = 0
        tick = 0
        time = 0
    }

    private fun handleFire(player: EntityPlayer, it: EntityPlayer) {
        when (fireMode.get()) {
            "Packet" -> {
                if (timeLimitedAwpOnly.get() && hasWeapon(WeaponType.SNIPER)) {
                    if (it.onGround && timeLimitedPrediction.get()) {
                        time = if (time < timeLimitedPredictionTicksValue.get() + Random.nextInt(
                                minRandomRange.get(),
                                maxRandomRange.get()
                            )
                        ) time + 1 else 0
                        if (time == 0) if (targetThroughWallPredictFire.get() || canSeePlayer(
                                player,
                                it,
                                TargetPart.HEAD
                            ).first
                        )
                            fire()
                    } else if (!fireLimit.get() || (!it.onGround && (it.posY - it.prevPosY) in fallValue.get()..posYValue.get()))
                        if (targetThroughWallPredictFire.get() || canSeePlayer(player, it, part).first)
                            fire()
                } else fire()
            }

            else -> {
                if (tick < fireTick.get()) tick++ else tick = 0
                mc.gameSettings.keyBindUseItem.pressed = tick < fireTick.get()
            }
        }
    }

    private fun setJitterValue() {
        if (jitterPitch.get() && jitter.get()) jitterValue =
            if (jitterTick++ < jitterFrequency.get()) -jitterAmplitude.get() else {
                jitterTick = 0; jitterAmplitude.get()
            }
        if (jitterYaw.get() && jitter.get()) jitterValue2 =
            if (jitterTick2++ < jitterFrequency.get()) -jitterAmplitude.get() else {
                jitterTick2 = 0; jitterAmplitude.get()
            }
    }

    private fun handleSneak(player: EntityPlayer) {
        if (autoSneak.get() && autoSneakTriggerMode.get() == "OnlyFire" && (!autoSneakOnlyAwp.get() || hasWeapon(
                WeaponType.SNIPER
            ))
        ) {
            if (autoSneakMode.get() == "Packet") {
                mc.netHandler.addToSendQueue(
                    C0BPacketEntityAction(
                        player,
                        C0BPacketEntityAction.Action.START_SNEAKING,
                        0
                    )
                )
            } else {
                mc.gameSettings.keyBindSneak.pressed = true
            }
        }
    }

    private fun handleNoSpreadMode() {
        if (noSpreadValue.get() && !hasWeapon(WeaponType.SNIPER)) {
            when (noSpreadMode.get()) {
                "Packet" -> handlePacketAction()
                "Switch" -> handleSwitchAction()
                "SwitchOffsets" -> handleSwitchOffsetsAction()
                "PacketOffsets" -> handlePacketOffsetsAction()
            }
        }
    }

    private fun canSeePlayer(
        player: EntityPlayer,
        target: EntityPlayer,
        priority: TargetPart,
    ): Pair<Boolean, Vec3> {
        val world = mc.theWorld
        val playerEyePos = Vec3(playerPosX(), playerPosY() + player.eyeHeight, playerPosZ())
        val size = size.get().toDouble()

        val baseBox = when (getPosMode.get()) {
            "ServerPos" -> serverBoundingBox(target)
            "Pos" -> target.entityBoundingBox
            else -> target.entityBoundingBox
        }

        val totalHeight = baseBox.maxY - baseBox.minY

        val partBoxes = mapOf(
            TargetPart.HEAD to baseBox
                .setMaxY(baseBox.minY + totalHeight * 0.75)
                .offset(0.0, totalHeight * 0.25, 0.0),

            TargetPart.CHEST to baseBox
                .setMaxY(baseBox.minY + totalHeight * 0.5)
                .offset(0.0, totalHeight * 0.25, 0.0),

            TargetPart.FEET to baseBox
                .setMaxY(baseBox.minY + totalHeight * 0.25)
        )

        val checkOrder = when (priority) {
            TargetPart.HEAD -> listOf(TargetPart.HEAD, TargetPart.CHEST, TargetPart.FEET)
            TargetPart.CHEST -> listOf(TargetPart.CHEST, TargetPart.HEAD, TargetPart.FEET)
            TargetPart.FEET -> listOf(TargetPart.FEET, TargetPart.CHEST, TargetPart.HEAD)
        }

        val (predictX, predictZ) = if (targetVisibilityPredict.get()) {
            velocityX(target) * targetVisibilityPredictSize.get() to
                    velocityZ(target) * targetVisibilityPredictSize.get()
        } else 0.0 to 0.0
        var bestPoint: Vec3? = null
        var minDistance = Double.MAX_VALUE

        for (part in checkOrder) {
            val currentBox = partBoxes[part]!!
                .offset(predictX, 0.0, predictZ)
                .expand(size, 0.0, size)
            for (layer in 0 until 3) {
                val step = boundingBoxStep.get() * (1 shl layer)
                var y = currentBox.maxY - step / 2
                while (y >= currentBox.minY) {
                    var x = currentBox.minX + step / 2
                    while (x <= currentBox.maxX) {
                        var z = currentBox.minZ + step / 2
                        while (z <= currentBox.maxZ) {
                            val point = Vec3(x, y, z)
                            if (world.rayTraceBlocks(
                                    playerEyePos, point,
                                    stopOnLiquid.get(),
                                    ignoreBlockWithoutBoundingBox.get(),
                                    returnLastUncollidableBlock.get()
                                )?.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                            ) {

                                val center = currentBox.getCenter()
                                val distanceScore = point.squareDistanceTo(center)
                                val heightScore = (y - currentBox.minY) / totalHeight
                                val totalScore = distanceScore * 0.4 + (1 - heightScore) * 0.6
                                if (totalScore < minDistance) {
                                    bestPoint = point
                                    minDistance = totalScore
                                }
                            }
                            z += step
                        }
                        x += step
                    }
                    y -= step
                }
                if (bestPoint != null && layer > 0) break
            }
            bestPoint?.let {
                return Pair(true, confirm(it, part, currentBox))
            }
        }

        return Pair(false, Vec3(0.0, 0.0, 0.0))
    }

    private fun confirm(point: Vec3, part: TargetPart, box: AxisAlignedBB): Vec3 {
        val verticalRatio = (point.yCoord - box.minY) / (box.maxY - box.minY)
        return when (part) {
            TargetPart.HEAD -> point.addVector(
                0.0,
                headAimOffset.get().toDouble() * (1 - verticalRatio),
                0.0
            )

            TargetPart.CHEST -> point.addVector(
                0.0,
                chestAimOffset.get().toDouble() * verticalRatio,
                0.0
            )

            TargetPart.FEET -> point.addVector(
                0.0,
                feetAimOffset.get().toDouble() * (1 - verticalRatio),
                0.0
            )
        }.also {
            if (mc.theWorld.rayTraceBlocks(
                    Vec3(playerPosX(), playerPosY() + mc.thePlayer.eyeHeight, playerPosZ()),
                    it
                )?.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
            ) {
                return@confirm point
            }
        }
    }

    private fun stopSneak() {
        mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING, 0))
    }
}