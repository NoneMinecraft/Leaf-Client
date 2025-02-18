//仅供参考,新killaura见Aura.kt
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.client.HUD
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe
import net.ccbluex.liquidbounce.features.module.modules.player.DelayBound
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RaycastUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import net.minecraft.util.MathHelper
import net.minecraft.world.WorldSettings
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

@ModuleInfo(name = "KillAura", category = ModuleCategory.COMBAT, keyBind = Keyboard.KEY_R)
class KillAura : Module() {
    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCpsValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCpsValue.get()
            if (i > newValue) set(i)

            attackDelay = getAttackDelay(minCpsValue.get(), this.get())
        }
    }

    private val minCpsValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCpsValue.get()
            if (i < newValue) set(i)

            attackDelay = getAttackDelay(this.get(), maxCpsValue.get())
        }
    }

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val combatDelayValue = BoolValue("1.9CombatDelay", false)

    // Range
    val rangeValue = object : FloatValue("Range", 3.7f, 1f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)

        }
    }
    private val throughWallsRangeValue = object : FloatValue("ThroughWallsRange", 1.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)
    private val swingRangeValue = object : FloatValue("SwingRange", 5f, 0f, 15f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
            if (maxRange > newValue) set(maxRange)
        }
    }
    private val discoverRangeValue = FloatValue("DiscoverRange", 6f, 0f, 15f)

    private val blinkCheck = BoolValue("BlinkCheck", true)
    private val noScaffValue = BoolValue("NoScaffold", true)
    private val noFlyValue = BoolValue("NoFly", false)

    // Modes
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Fov", "LivingTime", "Armor", "HurtResistance", "HurtTime", "HealthAbsorption", "RegenAmplifier"), "Armor")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Bypass
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val attackTimingValue = ListValue("AttackTiming", arrayOf("All", "Pre", "Post", "Both"), "All")
    private val keepSprintValue = BoolValue("KeepSprint", true)

    private val noBadPacketsValue = BoolValue("NoBadPackets", false)

    // AutoBlock
    val autoBlockValue = ListValue("AutoBlock", arrayOf("Range", "Fake", "Off"), "Off")

    // vanilla will send block packet at pre
    private val blockTimingValue =
        ListValue("BlockTiming", arrayOf("Pre", "Post", "Both"), "Both").displayable { autoBlockValue.equals("Range") }
    private val autoBlockRangeValue = object : FloatValue("AutoBlockRange", 2.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }.displayable { autoBlockValue.equals("Range") }
    private val autoBlockPacketValue = ListValue("AutoBlockPacket", arrayOf("AfterTick", "AfterAttack", "Vanilla", "Hypixel"), "AfterTick").displayable { autoBlockValue.equals("Range") }
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", true).displayable { autoBlockValue.equals("Range") }
    private val blockRateValue = IntegerValue("BlockRate", 100, 1, 100).displayable { autoBlockValue.equals("Range") }

    // Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable { raycastValue.get() }
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable { raycastValue.get() }

    // Bypass
    private val aacValue = BoolValue("AAC", true)
    // TODO: Divide AAC Opinion into three separated opinions

    // Rotations
    private val rotationModeValue = ListValue(
        "RotationMode",
        arrayOf("None", "LiquidBounce", "ForceCenter", "SmoothCenter", "SmoothLiquid", "LockView", "OldMatrix"),
        "LiquidBounce"
    )
    // TODO: RotationMode Bypass Intave

    private val maxTurnSpeedValue: FloatValue = object : FloatValue("MaxTurnSpeed", 360f, 1f, 360f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeedValue.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeedValue: FloatValue = object : FloatValue("MinTurnSpeed", 360f, 1f, 360f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeedValue.get()
            if (v < newValue) set(v)
        }
    }

    private val rotationSmoothModeValue = ListValue("SmoothMode", arrayOf("Custom", "Line", "Quad", "Sine", "QuadSine"), "Custom")

    private val rotationSmoothValue = FloatValue("CustomSmooth", 2f, 1f, 10f).displayable { rotationSmoothModeValue.equals("Custom") }

    private val randomCenterModeValue = ListValue("RandomCenter", arrayOf("Off", "Cubic", "Horizonal", "Vertical"), "Off")
    private val randomCenRangeValue = FloatValue("RandomRange", 0.0f, 0.0f, 1.2f)

    private val silentRotationValue = BoolValue("SilentRotation", true).displayable { !rotationModeValue.equals("None") }
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Silent").displayable { silentRotationValue.get() && !rotationModeValue.equals("None") }
    private val strafeOnlyGroundValue = BoolValue("StrafeOnlyGround", true).displayable { rotationStrafeValue.displayable && !rotationStrafeValue.equals("Off") }
    private val rotationRevValue = BoolValue("RotationReverse", false).displayable { !rotationModeValue.equals("None") }
    private val rotationRevTickValue = IntegerValue("RotationReverseTick", 5, 1, 20).displayable { !rotationModeValue.equals("None") }
    private val keepDirectionValue = BoolValue("KeepDirection", true).displayable { !rotationModeValue.equals("None") }
    private val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 15, 1, 20).displayable { !rotationModeValue.equals("None") }
    private val hitableValue = BoolValue("AlwaysHitable", true).displayable { !rotationModeValue.equals("None") }
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    // Predict
    private val predictValue = BoolValue("Predict", true).displayable { !rotationModeValue.equals("None") }

    private val maxPredictSizeValue: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSizeValue.get()
            if (v > newValue) set(v)
        }
    }.displayable { predictValue.displayable && predictValue.get() } as FloatValue

    private val minPredictSizeValue: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSizeValue.get()
            if (v < newValue) set(v)
        }
    }.displayable { predictValue.displayable && predictValue.get() } as FloatValue

    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true).displayable { failRateValue.get() != 0f }
    private val noInventoryAttackValue = ListValue("NoInvAttack", arrayOf("Spoof", "CancelRun", "Off"), "Off")

    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val switchDelayValue = IntegerValue("SwitchDelay", 15, 1, 2000).displayable { targetModeValue.equals("Switch") }
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50).displayable { targetModeValue.equals("Multi") }

    // Visuals
    private val markValue = ListValue("Mark", arrayOf("Liquid", "FDP", "Block", "Jello", "Sims", "Lies", "Circle", "None"), "Jello")
    private val circleValue = BoolValue("Circle", true)
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }
    private val circleRadiusValue = FloatValue("CircleRadius", 1.0F,0.5F, 3.0F).displayable { markValue.equals("Circle") }

    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    private var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private var lastPacketSent = false
    private var packetSent = false
    private val prevTargetEntities = mutableListOf<Int>()
    private val discoveredTargets = mutableListOf<EntityLivingBase>()
    private val inRangeDiscoveredTargets = mutableListOf<EntityLivingBase>()
    val canFakeBlock: Boolean
        get() = inRangeDiscoveredTargets.isNotEmpty()

    // Attack delay
    private val attackTimer = MSTimer()
    private val switchTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Swing
    private val swingTimer = MSTimer()
    private var swingDelay = 0L
    private var canSwing = false

    // Fake block status
    var blockingStatus = false
    private var espAnimation = 0.0
    private var isUp = true

    var strictStrafe = false

    val displayBlocking: Boolean
        get() = blockingStatus || (autoBlockValue.equals("Fake") && canFakeBlock)

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        strictStrafe = false
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        strictStrafe = false
        LiquidBounce.moduleManager[TargetStrafe::class.java]!!.doStrafe = false
        target = null
        currentTarget = null
        hitable = false
        packetSent = false
        prevTargetEntities.clear()
        discoveredTargets.clear()
        inRangeDiscoveredTargets.clear()
        attackTimer.reset()
        clicks = 0
        canSwing = false
        swingTimer.reset()

        stopBlocking()
        RotationUtils.setTargetRotationReverse(RotationUtils.serverRotation, 0, 0)
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            packetSent = false
        }
        if (mc.thePlayer.isRiding) {
            return

        }
        if (attackTimingValue.equals("All") || attackTimingValue.equals("Both") ||
            (attackTimingValue.equals("Pre") && event.eventState == EventState.PRE) ||
            (attackTimingValue.equals("Post") && event.eventState == EventState.POST)
        ) {
            runAttackLoop()
        }

        if (autoBlockValue.equals("Range") && event.eventState == EventState.POST && autoBlockPacketValue.equals("Hypixel")) {
            if (mc.thePlayer.swingProgressInt == 1) {
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            } else if (mc.thePlayer.swingProgressInt == 2) {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()))
            }
        }

        if (blockTimingValue.equals("Both") ||
            (blockTimingValue.equals("Pre") && event.eventState == EventState.PRE) ||
            (blockTimingValue.equals("Post") && event.eventState == EventState.POST)
        ) {
            if (packetSent && noBadPacketsValue.get()) {
                return
            }
            // AutoBlock
            if (autoBlockValue.equals("Range") && discoveredTargets.isNotEmpty() && (!autoBlockPacketValue.equals("AfterAttack")
                        || discoveredTargets.any { mc.thePlayer.getDistanceToEntityBox(it) > maxRange }) && canBlock
            ) {
                val target = this.target ?: discoveredTargets.first()
                if (mc.thePlayer.getDistanceToEntityBox(target) <= autoBlockRangeValue.get()) {
                    startBlocking(
                        target,
                        interactAutoBlockValue.get() && (mc.thePlayer.getDistanceToEntityBox(target) < maxRange)
                    )
                } else {
                    if (!mc.thePlayer.isBlocking) {
                        stopBlocking()
                    }
                }
            }
        }

        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            return
        }

        if (rotationStrafeValue.equals("Off")) {
            update()
        }
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (cancelRun) return
        strictStrafe = false
        if(!LiquidBounce.moduleManager[TargetStrafe::class.java]!!.modifyStrafe(event)) {
            strictStrafe = true
        }
        if (rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding) {
            strictStrafe = false
            return
        }

        // if(event.eventState == EventState.PRE)
        update()

        if (strafeOnlyGroundValue.get() && !mc.thePlayer.onGround) {
            strictStrafe = false
            return
        }

        // TODO: Fix Rotation issue on Strafe POST Event

        if (discoveredTargets.isNotEmpty() && RotationUtils.targetRotation != null) {
            when (rotationStrafeValue.get().lowercase()) {
                "strict" -> {
                    if(strictStrafe) {
                        strictStrafe = false
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
                "silent" -> {
                    if(strictStrafe) {
                        strictStrafe = false
                        RotationUtils.targetRotation.applyStrafeToPlayer(event)
                        event.cancelEvent()
                    }
                }
            }
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.equals("CancelRun") && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get()))
        ) {
            return
        }

        // Update target
        updateTarget()

        if (discoveredTargets.isEmpty()) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.equals("Switch") && (currentTarget != null && EntityUtils.isSelected(
                currentTarget!!,
                true
            ))
        ) {
            target = currentTarget
        }

        LiquidBounce.moduleManager[TargetStrafe::class.java]!!.targetEntity = currentTarget?:return
        LiquidBounce.moduleManager[TargetStrafe::class.java]!!.doStrafe = LiquidBounce.moduleManager[TargetStrafe::class.java]!!.toggleStrafe()
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate() {
        if ((!strafeOnlyGroundValue.get() || mc.thePlayer.onGround) && !rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding) {
            strictStrafe = true
        }else {
            strictStrafe = false
        }
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            inRangeDiscoveredTargets.clear()
            return
        }

        if (noInventoryAttackValue.equals("CancelRun") && (mc.currentScreen is GuiContainer ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (!rotationStrafeValue.equals("Off") && !mc.thePlayer.isRiding) {
            return
        }

        if (mc.thePlayer.isRiding) {
            update()
        }

        if (attackTimingValue.equals("All")) {
            runAttackLoop()
        }
    }

    /**
     * Render event
     */
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

            for (i in 0..360 step 5) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(),
                    (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get())
                )
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            inRangeDiscoveredTargets.clear()
        }
        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) && currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = getAttackDelay(minCpsValue.get(), maxCpsValue.get())
        }

        discoveredTargets.forEach {
            when (markValue.get().lowercase()) {
                "liquid" -> {
                    RenderUtils.drawPlatform(
                        it,
                        if (it.hurtTime <= 0) Color(37, 126, 255, 170) else Color(255, 0, 0, 170)
                    )
                }
                "block" -> {
                    val bb = it.entityBoundingBox
                    it.entityBoundingBox = bb.expand(0.2, 0.2, 0.2)
                    RenderUtils.drawEntityBox(
                        it,
                        if (it.hurtTime <= 0) if (it == target) Color(255, 0, 0, 170) else Color(255, 0, 0, 170) else Color(255, 0, 0, 170),
                        true,
                        true,
                        4f
                    )
                    it.entityBoundingBox = bb
                }
                "fdp" -> {
                    val drawTime = (System.currentTimeMillis() % 1500).toInt()
                    val drawMode = drawTime > 750
                    var drawPercent = drawTime / 750.0
                    // true when goes up
                    if (!drawMode) {
                        drawPercent = 1 - drawPercent
                    } else {
                        drawPercent -= 1
                    }
                    drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                    mc.entityRenderer.disableLightmap()
                    GL11.glPushMatrix()
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_DEPTH_TEST)

                    val bb = it.entityBoundingBox
                    val radius = ((bb.maxX - bb.minX) + (bb.maxZ - bb.minZ)) * 0.5f
                    val height = bb.maxY - bb.minY
                    val x =
                        it.lastTickPosX + (it.posX - it.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
                    val y =
                        (it.lastTickPosY + (it.posY - it.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + height * drawPercent
                    val z =
                        it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                    mc.entityRenderer.disableLightmap()
                    GL11.glLineWidth((radius * 8f).toFloat())
                    GL11.glBegin(GL11.GL_LINE_STRIP)
                    for (i in 0..360 step 10) {
                        RenderUtils.glColor(
                            Color.getHSBColor(
                                if (i < 180) {
                                    HUD.rainbowStartValue.get() + (HUD.rainbowStopValue.get() - HUD.rainbowStartValue.get()) * (i / 180f)
                                } else {
                                    HUD.rainbowStartValue.get() + (HUD.rainbowStopValue.get() - HUD.rainbowStartValue.get()) * (-(i - 360) / 180f)
                                }, 0.7f, 1.0f
                            )
                        )
                        GL11.glVertex3d(x - sin(i * Math.PI / 180F) * radius, y, z + cos(i * Math.PI / 180F) * radius)
                    }
                    GL11.glEnd()

                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glPopMatrix()
                }
                "jello" -> {
                    val drawTime = (System.currentTimeMillis() % 2000).toInt()
                    val drawMode=drawTime>1000
                    var drawPercent=drawTime/1000.0
                    //true when goes up
                    if(!drawMode){
                        drawPercent=1-drawPercent
                    }else{
                        drawPercent-=1
                    }
                    drawPercent=EaseUtils.easeInOutQuad(drawPercent)
                    val points = mutableListOf<Vec3>()
                    val bb=it.entityBoundingBox
                    val radius=bb.maxX-bb.minX
                    val height=bb.maxY-bb.minY
                    val posX = it.lastTickPosX + (it.posX - it.lastTickPosX) * mc.timer.renderPartialTicks
                    var posY = it.lastTickPosY + (it.posY - it.lastTickPosY) * mc.timer.renderPartialTicks
                    if(drawMode){
                        posY-=0.5
                    }else{
                        posY+=0.5
                    }
                    val posZ = it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * mc.timer.renderPartialTicks
                    for(i in 0..360 step 7){
                        points.add(Vec3(posX - sin(i * Math.PI / 180F) * radius,posY+height*drawPercent,posZ + cos(i * Math.PI / 180F) * radius))
                    }
                    points.add(points[0])
                    //draw
                    mc.entityRenderer.disableLightmap()
                    GL11.glPushMatrix()
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_DEPTH_TEST)
                    GL11.glBegin(GL11.GL_LINE_STRIP)
                    val baseMove=(if(drawPercent>0.5){1-drawPercent}else{drawPercent})*2
                    val min=(height/60)*20*(1-baseMove)*(if(drawMode){-1}else{1})
                    for(i in 0..20) {
                        var moveFace=(height/60F)*i*baseMove
                        if(drawMode){
                            moveFace=-moveFace
                        }
                        val firstPoint=points[0]
                        GL11.glVertex3d(
                            firstPoint.xCoord - mc.renderManager.viewerPosX, firstPoint.yCoord - moveFace - min - mc.renderManager.viewerPosY,
                            firstPoint.zCoord - mc.renderManager.viewerPosZ
                        )
                        GL11.glColor4f(1F, 1F, 1F, 0.7F*(i/20F))
                        for (vec3 in points) {
                            GL11.glVertex3d(
                                vec3.xCoord - mc.renderManager.viewerPosX, vec3.yCoord - moveFace - min - mc.renderManager.viewerPosY,
                                vec3.zCoord - mc.renderManager.viewerPosZ
                            )
                        }
                        GL11.glColor4f(0F,0F,0F,0F)
                    }
                    GL11.glEnd()
                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glPopMatrix()
                }
                "lies" -> {
                    val everyTime = 3000
                    val drawTime = (System.currentTimeMillis() % everyTime).toInt()
                    val drawMode = drawTime > (everyTime / 2)
                    var drawPercent = drawTime / (everyTime / 2.0)
                    // true when goes up
                    if (!drawMode) {
                        drawPercent = 1 - drawPercent
                    } else {
                        drawPercent -= 1
                    }
                    drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                    mc.entityRenderer.disableLightmap()
                    GL11.glPushMatrix()
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_DEPTH_TEST)
                    GL11.glDisable(GL11.GL_CULL_FACE)
                    GL11.glShadeModel(7425)
                    mc.entityRenderer.disableLightmap()

                    val bb = it.entityBoundingBox
                    val radius = ((bb.maxX - bb.minX) + (bb.maxZ - bb.minZ)) * 0.5f
                    val height = bb.maxY - bb.minY
                    val x =
                        it.lastTickPosX + (it.posX - it.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
                    val y =
                        (it.lastTickPosY + (it.posY - it.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + height * drawPercent
                    val z =
                        it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                    val eased = (height / 3) * (if (drawPercent > 0.5) {
                        1 - drawPercent
                    } else {
                        drawPercent
                    }) * (if (drawMode) {
                        -1
                    } else {
                        1
                    })
                    for (i in 5..360 step 5) {
                        val color = Color.getHSBColor(
                            if (i < 180) {
                                HUD.rainbowStartValue.get() + (HUD.rainbowStopValue.get() - HUD.rainbowStartValue.get()) * (i / 180f)
                            } else {
                                HUD.rainbowStartValue.get() + (HUD.rainbowStopValue.get() - HUD.rainbowStartValue.get()) * (-(i - 360) / 180f)
                            }, 0.7f, 1.0f
                        )
                        val x1 = x - sin(i * Math.PI / 180F) * radius
                        val z1 = z + cos(i * Math.PI / 180F) * radius
                        val x2 = x - sin((i - 5) * Math.PI / 180F) * radius
                        val z2 = z + cos((i - 5) * Math.PI / 180F) * radius
                        GL11.glBegin(GL11.GL_QUADS)
                        RenderUtils.glColor(color, 0f)
                        GL11.glVertex3d(x1, y + eased, z1)
                        GL11.glVertex3d(x2, y + eased, z2)
                        RenderUtils.glColor(color, 150f)
                        GL11.glVertex3d(x2, y, z2)
                        GL11.glVertex3d(x1, y, z1)
                        GL11.glEnd()
                    }

                    GL11.glEnable(GL11.GL_CULL_FACE)
                    GL11.glShadeModel(7424)
                    GL11.glColor4f(1f, 1f, 1f, 1f)
                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glPopMatrix()
                }
                "circle" -> {
                    if (espAnimation > target!!.eyeHeight + 0.4 || espAnimation < 0) {
                        isUp = !isUp
                    }
                    if (isUp) {
                        espAnimation += 0.05 * 60 / Minecraft.getDebugFPS()
                    } else {
                        espAnimation -= 0.05 * 60 / Minecraft.getDebugFPS()
                    }
                    if (isUp) {
                        esp(target!!, event.partialTicks, circleRadiusValue.get())
                    } else {
                        esp(target!!, event.partialTicks, circleRadiusValue.get())
                    }
                }
                "sims" -> {
                    val radius = 0.15f
                    val side = 4
                    GL11.glPushMatrix()
                    GL11.glTranslated(
                        it.lastTickPosX + (it.posX - it.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX,
                        (it.lastTickPosY + (it.posY - it.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + it.height * 1.1,
                        it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                    )
                    GL11.glRotatef(-it.width, 0.0f, 1.0f, 0.0f)
                    GL11.glRotatef((mc.thePlayer.ticksExisted + mc.timer.renderPartialTicks) * 5, 0f, 1f, 0f)
                    RenderUtils.glColor(if (it.hurtTime <= 0) Color(80, 255, 80) else Color(255, 0, 0))
                    RenderUtils.enableSmoothLine(1.5F)
                    val c = Cylinder()
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                    c.draw(0F, radius, 0.3f, side, 1)
                    c.drawStyle = 100012
                    GL11.glTranslated(0.0, 0.0, 0.3)
                    c.draw(radius, 0f, 0.3f, side, 1)
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
                    GL11.glTranslated(0.0, 0.0, -0.3)
                    c.draw(0F, radius, 0.3f, side, 1)
                    GL11.glTranslated(0.0, 0.0, 0.3)
                    c.draw(radius, 0F, 0.3f, side, 1)
                    RenderUtils.disableSmoothLine()
                    GL11.glPopMatrix()
                }
            }
        }
    }

    /**
     * Handle entity move
     */
//    @EventTarget
//    fun onEntityMove(event: EntityMovementEvent) {
//        val movedEntity = event.movedEntity
//
//        if (target == null || movedEntity != currentTarget)
//            return
//
//        updateHitable()
//    }

    private fun esp(entity : EntityLivingBase, partialTicks : Float, radius : Float) {
        GL11.glPushMatrix()
        GL11.glDisable(3553)
        RenderUtils.startSmooth()
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glLineWidth(1.0F)
        GL11.glBegin(3)
        val x: Double = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val y: Double = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val z: Double = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ
        for (i in 0..360) {
            val rainbow = Color(Color.HSBtoRGB((mc.thePlayer.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
            GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
            GL11.glVertex3d(x + radius * cos(i * 6.283185307179586 / 45.0), y + espAnimation, z + radius * sin(i * 6.283185307179586 / 45.0))
        }
        GL11.glEnd()
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        RenderUtils.endSmooth()
        GL11.glEnable(3553)
        GL11.glPopMatrix()
    }

    private fun runAttackLoop() {
        if (clicks <= 0 && canSwing && swingTimer.hasTimePassed(swingDelay)) {
            swingTimer.reset()
            swingDelay = getAttackDelay(minCpsValue.get(), maxCpsValue.get())
            runSwing()
            return
        }

        try {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        } catch (e: java.lang.IllegalStateException) {
            return
        }
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRateValue.get()
        val openInventory = noInventoryAttackValue.equals("Spoof") && mc.currentScreen is GuiInventory
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        if (hitable && !failHit) {
            if (openInventory) {
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
            }

            // Attack
            if (!targetModeValue.equals("Multi")) {
                attackEntity(currentTarget!!)
            } else {
                inRangeDiscoveredTargets.forEachIndexed { index, entity ->
                    if (limitedMultiTargetsValue.get() == 0 || index < limitedMultiTargetsValue.get()) {
                        attackEntity(entity)
                    }
                }
            }

            if (targetModeValue.equals("Switch")) {
                    prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
            } else {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
            }

            if (target == currentTarget) {
                target = null
            }

            // Open inventory
            if (openInventory) {
                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
            }
        } else if (fakeSwingValue.get()) {
            runSwing()
        }
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.equals("Switch")

        // Find possible targets
        discoveredTargets.clear()

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !EntityUtils.isSelected(
                    entity,
                    true
                ) || (switchMode && prevTargetEntities.contains(entity.entityId))
            ) {
                continue
            }

            val distance = mc.thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= discoverRangeValue.get() && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime) {
                discoveredTargets.add(entity)
            }
        }

        // Sort targets by priority
        when (priorityValue.get().lowercase()) {
            "distance" -> discoveredTargets.sortBy { mc.thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> discoveredTargets.sortBy { it.health } // Sort by health
            "fov" -> discoveredTargets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> discoveredTargets.sortBy { -it.ticksExisted } // Sort by existence
            "armor" -> discoveredTargets.sortBy { it.totalArmorValue } // Sort by armor
            "hurtresistanttime" -> discoveredTargets.sortBy { it.hurtResistantTime } // Sort by armor
            "hurttime" -> discoveredTargets.sortBy { it.hurtTime } // Sort by hurt time
            "healthabsorption" -> discoveredTargets.sortBy { it.health + it.absorptionAmount } // Sort by full health with absorption effect
            "regenamplifier" -> discoveredTargets.sortBy { if (it.isPotionActive(Potion.regeneration)) it.getActivePotionEffect(Potion.regeneration).amplifier else -1 }
        }

        inRangeDiscoveredTargets.clear()
        inRangeDiscoveredTargets.addAll(discoveredTargets.filter { mc.thePlayer.getDistanceToEntityBox(it) < getRange(it) })

        // Cleanup last targets when no targets found and try again
        if (inRangeDiscoveredTargets.isEmpty() && prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
            return
        }

        // Find best target
        for (entity in discoveredTargets) {
            // Update rotations to current target
            if (!updateRotations(entity)) { // when failed then try another target
                continue
            }

            // Set target to current entity
            if (mc.thePlayer.getDistanceToEntityBox(entity) < maxRange) {
                target = entity
                canSwing = false
                LiquidBounce.moduleManager[TargetStrafe::class.java]!!.targetEntity = target?:return
                LiquidBounce.moduleManager[TargetStrafe::class.java]!!.doStrafe = LiquidBounce.moduleManager[TargetStrafe::class.java]!!.toggleStrafe()
                return
            }
        }

        target = null
        LiquidBounce.moduleManager[TargetStrafe::class.java]!!.doStrafe = false
        canSwing = discoveredTargets.find { mc.thePlayer.getDistanceToEntityBox(it) < swingRangeValue.get() } != null
    }

    private fun runSwing() {
        val swing = swingValue.get()
        if (swing.equals("packet", true)) {
            mc.netHandler.addToSendQueue(C0APacketAnimation())
        } else if (swing.equals("normal", true)) {
            mc.thePlayer.swingItem()
        }
    }


    private fun attackEntity(entity: EntityLivingBase) {
        if (packetSent && noBadPacketsValue.get()) {
            throw java.lang.IllegalStateException("Attack canceled because of bad packets protection")
        }
        // Call attack event
        val event = AttackEvent(entity)
        LiquidBounce.eventManager.callEvent(event)
        if (event.isCancelled) {
            return
        }

        // Stop blocking
        if (!autoBlockPacketValue.equals("Vanilla") && (mc.thePlayer.isBlocking || blockingStatus)) {
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
            blockingStatus = false
            if (noBadPacketsValue.get()) {
                packetSent = true
                throw java.lang.IllegalStateException("Attack canceled because of bad packets protection")
            }
        }

        // Attack target
        runSwing()

        packetSent = true
        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        if (keepSprintValue.get()) {
            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F) {
                mc.thePlayer.onEnchantmentCritical(entity)
            }
        } else {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
            }
        }

        // Start blocking after attack
        if (mc.thePlayer.isBlocking || (autoBlockValue.equals("Range") && canBlock)) {
            if (autoBlockPacketValue.equals("AfterTick")) {
                return
            }

            if (!(blockRateValue.get() > 0 && Random().nextInt(100) <= blockRateValue.get())) {
                return
            }

            startBlocking(entity, interactAutoBlockValue.get())
        }
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if (rotationModeValue.equals("None")) {
            return true
        }

        var boundingBox = entity.entityBoundingBox

        if (predictValue.get() && rotationModeValue.get() != "Test") {
            boundingBox = boundingBox.offset(
                (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(
                    minPredictSizeValue.get(),
                    maxPredictSizeValue.get()
                ),
                (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(
                    minPredictSizeValue.get(),
                    maxPredictSizeValue.get()
                ),
                (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(
                    minPredictSizeValue.get(),
                    maxPredictSizeValue.get()
                )
            )
        }
        val rModes = when (rotationModeValue.get()) {
            "LiquidBounce", "SmoothLiquid", "Derp" -> "LiquidBounce"
            "ForceCenter", "SmoothCenter", "OldMatrix", "Spin", "FastSpin" -> "CenterLine"
            "LockView" -> "CenterSimple"
            "Test" -> "HalfUp"
            else -> "LiquidBounce"
        }

        val (_, directRotation) =
            RotationUtils.calculateCenter(
                rModes,
                randomCenterModeValue.get(),
                (randomCenRangeValue.get()).toDouble(),
                boundingBox,
                predictValue.get() && rotationModeValue.get() != "Test",
                mc.thePlayer.getDistanceToEntityBox(entity) <= throughWallsRangeValue.get()
            ) ?: return false

        if (rotationModeValue.get() == "OldMatrix") directRotation.pitch = (89.9).toFloat()

        var diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, directRotation)
        if (diffAngle < 0) diffAngle = -diffAngle
        if (diffAngle > 180.0) diffAngle = 180.0

        val calculateSpeed = when (rotationSmoothModeValue.get()) {
            "Custom" -> diffAngle / rotationSmoothValue.get()
            "Line" -> (diffAngle / 360) * maxTurnSpeedValue.get() + (1 - diffAngle / 360) * minTurnSpeedValue.get()
            //"Quad" -> Math.pow((diffAngle / 180.0), 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow((diffAngle / 180.0), 2.0)) * minTurnSpeedValue.get()
            "Quad" -> (diffAngle / 360.0).pow(2.0) * maxTurnSpeedValue.get() + (1 - (diffAngle / 360.0).pow(2.0)) * minTurnSpeedValue.get()
            "Sine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * maxTurnSpeedValue.get() + (cos(diffAngle / 360 * Math.PI) * 0.5 + 0.5) * minTurnSpeedValue.get()
            //"QuadSine" -> Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0)) * minTurnSpeedValue.get()
            "QuadSine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5).pow(2.0) * maxTurnSpeedValue.get() + (1 - (-cos(
                diffAngle / 180 * Math.PI
            ) * 0.5 + 0.5).pow(2.0)) * minTurnSpeedValue.get()
            else -> 360.0
        }

        val rotation = when (rotationModeValue.get()) {
            "LiquidBounce", "ForceCenter" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, directRotation,
                (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get()).toFloat()
            )
            "LockView" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (180.0).toFloat()
            )
            "SmoothCenter", "SmoothLiquid", "OldMatrix" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            "Test" -> RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )
            else -> return true
        }

        if (silentRotationValue.get()) {
            if (rotationRevTickValue.get() > 0 && rotationRevValue.get()) {
                if (keepDirectionValue.get()) {
                    RotationUtils.setTargetRotationReverse(
                        rotation,
                        keepDirectionTickValue.get(),
                        rotationRevTickValue.get()
                    )
                } else {
                    RotationUtils.setTargetRotationReverse(rotation, 1, rotationRevTickValue.get())
                }
            } else {
                if (keepDirectionValue.get()) {
                    RotationUtils.setTargetRotation(rotation, keepDirectionTickValue.get())
                } else {
                    RotationUtils.setTargetRotation(rotation, 4)
                }
            }
        } else {
            rotation.toPlayer(mc.thePlayer)
        }
        return true
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if (hitableValue.get()) {
            hitable = true
            return
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeedValue.get() <= 0F) {
            hitable = true
            return
        }

        val reach = maxRange.toDouble()

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (EntityUtils.isSelected(
                            it,
                            true
                        ) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                            it,
                            it.entityBoundingBox
                        ).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase &&
                !EntityUtils.isFriend(raycastedEntity)
            ) {
                currentTarget = raycastedEntity
            }

            hitable = if (!rotationModeValue.equals("None")) currentTarget == raycastedEntity else true
        } else {
            hitable = RotationUtils.isFaced(currentTarget, reach)
        }
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (autoBlockValue.equals("Range") && mc.thePlayer.getDistanceToEntityBox(interactEntity) > autoBlockRangeValue.get()) {
            return
        }

        if (blockingStatus) {
            return
        }

        if (packetSent && noBadPacketsValue.get()) {
            return
        }

        if (interact) {
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
        blockingStatus = true
        packetSent = true
    }

    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        if (blockingStatus) {
            if (packetSent && noBadPacketsValue.get()) {
                return
            }
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    if (MovementUtils.isMoving()) BlockPos(-1, -1, -1) else BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
            blockingStatus = false
            packetSent = true
        }
    }

    /**
     * Attack Delay
     */
    private fun getAttackDelay(minCps: Int, maxCps: Int): Long {
        var delay = TimeUtils.randomClickDelay(minCps.coerceAtMost(maxCps), minCps.coerceAtLeast(maxCps))
        if (combatDelayValue.get()) {
            var value = 4.0
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                when (mc.thePlayer.inventory.getCurrentItem().item) {
                    is ItemSword -> {
                        value -= 2.4
                    }
                    is ItemPickaxe -> {
                        value -= 2.8
                    }
                    is ItemAxe -> {
                        value -= 3
                    }
                }
            }
            delay = delay.coerceAtLeast((1000 / value).toLong())
        }
        return delay
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer)
                || (blinkCheck.get() && LiquidBounce.moduleManager[DelayBound::class.java]!!.state) || LiquidBounce.moduleManager[FreeCam::class.java]!!.state ||
                (noScaffValue.get() && LiquidBounce.moduleManager[Scaffold::class.java]!!.state) || (noFlyValue.get() && LiquidBounce.moduleManager[Fly::class.java]!!.state)

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 3

    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())

    private fun getRange(entity: Entity) =
        (if (mc.thePlayer.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get()) - if (mc.thePlayer.isSprinting) rangeSprintReducementValue.get() else 0F

    /**
     * HUD Tag
     */

    override val tag: String
        get() = targetModeValue.get()


    /*     private val simpleArrayList = BoolValue("SimpleArrayListTag", false)
        init{ if (simpleArrayList.equals(true)) {
                var tagVal = get() = "${targetModeValue.get()}, ${priorityValue.get()}, " + "${minCpsValue.get()}-${maxCpsValue.get()}, " + "$maxRange${ if (!autoBlockValue.equals("Off")) { "-${autoBlockRangeValue.get()}"} else {""}}-${discoverRangeValue.get()}"
            } else {
                var tagVal = get() = targetModeValue.get()
            }
        }
            override val tag: String
                get() = tagVal
    */
}
