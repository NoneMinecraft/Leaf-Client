/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils2.extensions.iterator
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@ModuleInfo(name = "RageBot", category = ModuleCategory.COMBAT)
class RageBot : Module() {
    private val range = FloatValue("MaxRange", 70F, 50F, 200F)
    private val autoRange = BoolValue("AutoRange", true)
    private val akRange = FloatValue("AutoRange-CustomRange-AKRange", 50F, 20F, 100F)
    private val m4Range = FloatValue("AutoRange-CustomRange-M4Range", 50F, 20F, 100F)
    private val awpRange = FloatValue("AutoRange-CustomRange-AWPRange", 70F, 20F, 100F)
    private val mp7Range = FloatValue("AutoRange-CustomRange-MP7Range", 45F, 20F, 100F)
    private val p250Range = FloatValue("AutoRange-CustomRange-P250Range", 35F, 20F, 100F)
    private val deagleRange = FloatValue("AutoRange-CustomRange-DeagleRange", 40F, 20F, 100F)
    private val shotgunRange = FloatValue("AutoRange-CustomRange-ShotGunRange", 20F, 20F, 100F)
    private val pitchOffset = FloatValue("PitchOffset", 0.2F, 0F, 5F)
    private val predictB = BoolValue("PredictB", true)
    private val targetPredict = BoolValue("TargetPredict", true)
    private val targetPredictSize = FloatValue("TargetPredictSize", 4.3F, 0F, 10F).displayable{targetPredict.get()}
    private val playerPredict = BoolValue("PlayerPredict", false)
    private val playerPredictSize = FloatValue("PlayerPredictSize", 4.3F, 0F, 10F).displayable{playerPredict.get()}
    private val targetThroughWallPredict = BoolValue("TargetThroughWallPredict", true)
    private val targetThroughWallPredictSize = FloatValue("TargetThroughWallPredictSize", 5F, 0F, 20F).displayable{targetThroughWallPredict.get()}
    private val playerThroughWallPredict = BoolValue("PlayerThroughWallPredict", false)
    private val playerThroughWallPredictSize = FloatValue("PlayerThroughWallPredictSize", 5F, 0F, 20F).displayable{playerThroughWallPredict.get()}
    private val targetThroughWallPredictFire = BoolValue("targetThroughWallPredictFire", true)
    private val eyeHeight = FloatValue("EyeHeight", 0.8F, -1F, 1F)
    private val fireMode = ListValue("FireMode", arrayOf("Legit","Packet"),"Packet")
    private val fireTick = IntegerValue("FireTick", 1, 0, 5).displayable{fireMode.get() == "Legit"}
    private val noSpreadValue = BoolValue("NoSpread", true)
    private val noSpreadMode = ListValue("NoSpreadMode", arrayOf("Switch","Packet"),"Switch")
    private val noSpreadTick = IntegerValue("NoSpreadBaseTick", 2, 0, 5)
    private val autoSneak = BoolValue("AutoSneak", true)
    private val whitelistAK = BoolValue("NoSpreadWhitelist-AK", true)
    private val whitelistM4 = BoolValue("NoSpreadWhitelist-M4", true)
    private val whitelistMP7 = BoolValue("NoSpreadWhitelist-MP7", true)
    private val whitelistShotgun = BoolValue("NoSpreadWhitelist-ShotGun", true)
    private val autoSneakMode = ListValue("AutoSneakMode", arrayOf("Legit","Packet"),"Packet")
    private val autoSneakTriggerMode = ListValue("AutoSneakTriggerMode", arrayOf("Always","OnlyFire"),"OnlyFire")
    private val autoSneakOnlyAwp = BoolValue("AutoSneakOnlyAwp", true)
    private val rotateValue = BoolValue("SilentRotate", false)
    private val fireLimit = BoolValue("FireLimit", false)
    private val posYValue = FloatValue("FireLimit-TargetVelocityY", 0.8F, -1F, 1F).displayable{fireLimit.get()}
    private val fallValue = FloatValue("FireLimit-TargetFallVelocity", 0.8F, -1F, 1F).displayable{fireLimit.get()}
    private val timeLimitedPrediction = BoolValue("FireLimit-TimeLimitedPrediction", false).displayable{fireLimit.get()}
    private val timeLimitedPredictionTicksValue = IntegerValue("FireLimit-TimeLimitedPredictionTicks", 5, 0, 40).displayable{fireLimit.get()}
    private val maxRandomRange = IntegerValue("FireLimit-MaxRandomRange", 5, 0, 40).displayable{fireLimit.get()}
    private val minRandomRange = IntegerValue("FireLimit-MainRandomRange", 1, 0, 40).displayable{fireLimit.get()}
    private val timeLimitedAwpOnly = BoolValue("FireLimit-AwpOnly", true).displayable{fireLimit.get()}
    private val jitter = BoolValue("Jitter", false)
    private val jitterYaw = BoolValue("JitterYaw", true).displayable{jitter.get()}
    private val jitterPitch = BoolValue("JitterPitch", false).displayable{jitter.get()}
    private val jitterFrequency = IntegerValue("JitterFrequency", 1, 1, 40).displayable{jitter.get()}
    private val jitterAmplitude = IntegerValue("JitterAmplitude", 1, 1, 40).displayable{jitter.get()}
    private val targetDebug = BoolValue("TargetDebug", false)
    private val targetDebugMaxHurtTime = IntegerValue("TargetDebug-MaxHurtTime", 10, 1, 10)
    private val targetDebugMinHurtTime = IntegerValue("TargetDebug-MinHurtTime", 10, 1, 10)
    private val stopOnLiquid = BoolValue("RayTraceBlocks-StopOnLiquid", true)
    private val ignoreBlockWithoutBoundingBox = BoolValue("RayTraceBlocks-IgnoreBlockWithoutBoundingBox", true)
    private val returnLastUncollidableBlock = BoolValue("RayTraceBlocks-ReturnLastUncollidableBlock", false)
    private val hitBoxValue = BoolValue("HitBox", true)
    private val head = BoolValue("HitBox-Head" ,true)
    private val chest = BoolValue("HitBox-Chest", true)
    private val feet = BoolValue("HitBox-Feet", true)
    private val priority = ListValue("Priority", arrayOf("Head","Chest","Feet"),"Head")
    private val baseEyeHeightDetectionOffset = FloatValue("BaseEyeHeightDetectionOffset", 0F, -1F, 1F)
    private val chestDetectionOffset = FloatValue("ChestDetectionOffset", 0.8F, -1F, 1F)
    private val feetDetectionOffset = FloatValue("FeetDetectionOffset", 0F, -1F, 1F)
    private val chestAimOffset = FloatValue("ChestAimOffset", 0.8F, -1F, 1F)
    private val feetAimOffset = FloatValue("FeetAimOffset", 0F, -1F, 1F)
    private val  boundingBox = BoolValue("BoundingBox", true)
    private val vec = BoolValue("Vec", true)
    private val customBoundingBox = BoolValue("CustomBoundingBox", true)
    private val boundingBoxMaxX = FloatValue("BoundingBoxMaxX", 0.4F, 0.1F, 1F)
    private val boundingBoxMinX = FloatValue("BoundingBoxMinX", 0.4F, 0.1F, 1F)
    private val boundingBoxMaxZ = FloatValue("BoundingBoxMaxZ", 0.4F, 0.1F, 1F)
    private val boundingBoxMinZ = FloatValue("BoundingBoxMinZ", 0.4F, 0.1F, 1F)
    private val boundingBoxMaxOffsetX = FloatValue("BoundingBoxMaxOffsetX", 0F, -1F, 1F)
    private val boundingBoxMinOffsetX = FloatValue("BoundingBoxMinOffsetX", 0F, -1F, 1F)
    private val boundingBoxMaxOffsetZ = FloatValue("BoundingBoxMaxOffsetZ", 0F, -1F, 1F)
    private val boundingBoxMinOffsetZ = FloatValue("BoundingBoxMinOffsetZ", 0F, -1F, 1F)
    private val boundingBoxOffsetY = FloatValue("BoundingBoxOffsetY", 0F, -1F, 1F)
    private val hitEffect = BoolValue("HitEffect", true)
    private val hitEffectMode = ListValue("HitEffectMode", arrayOf("N1","N2"),"N1")
    private val sneakYOffset = FloatValue("SneakYOffset", 0.2F, -1F, 1F)
    private val playerVecYVecYOffset = FloatValue("PlayerVecYVecYOffset", 0.2F, -1F, 1F)
    private val circleValue = BoolValue("Circle", true)
    private val circleRange = FloatValue("CircleRange", 2F, 0.1F, 100F).displayable { circleValue.get() }
    private val circleRedValue = IntegerValue("CircleRed", 255, 0, 255).displayable { circleValue.get() }
    private val circleGreenValue = IntegerValue("CircleGreen", 255, 0, 255).displayable { circleValue.get() }
    private val circleBlueValue = IntegerValue("CircleBlue", 255, 0, 255).displayable { circleValue.get() }
    private val circleAlphaValue = IntegerValue("CircleAlpha", 255, 0, 255).displayable { circleValue.get() }
    private val circleThicknessValue = FloatValue("CircleThickness", 2F, 1F, 5F).displayable { circleValue.get() }

    private var targetPlayer: EntityPlayer? = null
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
    override fun onDisable() {
        boxZ = 0
        boxX = 0
        jitterTick = 0
        jitterValue = 0
        time
        resettick = 0
        type = 0
        ticks = 0
        tick = 0
        mc.gameSettings.keyBindUseItem.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING,0))
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
    fun onUpdate(event: UpdateEvent) {
            val player = mc.thePlayer ?: return
            targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { (!autoRange.get() && it.getDistanceToEntityBox(player) <= range.get())
                    || (autoRange.get() && it.getDistanceToEntityBox(player) <= range.get()&&
                    (ak() && it.getDistanceToEntityBox(player) <= akRange.get()
                    || m4() && it.getDistanceToEntityBox(player) <= m4Range.get()
                    || mp7() && it.getDistanceToEntityBox(player) <= mp7Range.get()
                    || p250() &&!rifle() && !awp() && it.getDistanceToEntityBox(player) <= p250Range.get()
                    ||deagle() &&!rifle() && !awp() && it.getDistanceToEntityBox(player) <= deagleRange.get()
                    || shotgun() && it.getDistanceToEntityBox(player) <= shotgunRange.get()
                    || awp() && it.getDistanceToEntityBox(player) <= awpRange.get()))}
            .firstOrNull {hitBoxValue.get() && (chest.get() && canSeeChest(player,it)) || (head.get() && canSeeHead(player,it)) ||
                    (feet.get() && canSeeFeet(player,it)) || canSeePlayer2(player,it) || canSeePlayer3(player,it) || canSeePlayer4(player,it) || canSeePlayer(player,it)}
            targetPlayer?.let {
                if (hitEffect.get() && it.hurtTime == 10){
                    when (hitEffectMode.get()){
                        "N1"-> LiquidBounce.tipSoundManager.hitSound1.asyncPlay()
                        "N2"-> LiquidBounce.tipSoundManager.hitSound2.asyncPlay()
                    }
                }
            if (targetDebug.get() && it.hurtTime in targetDebugMinHurtTime.get()..targetDebugMaxHurtTime.get()) ChatPrint("§f[§bHit§f] Name:${it.name} Health:${it.health}")
            if (jitterPitch.get() && jitter.get()) jitterValue = if (jitterTick++ < jitterFrequency.get()) -jitterAmplitude.get() else { jitterTick = 0; jitterAmplitude.get() }
            if (jitterYaw.get() && jitter.get()) jitterValue2 = if (jitterTick2++ < jitterFrequency.get()) -jitterAmplitude.get() else { jitterTick2 = 0; jitterAmplitude.get() }
            if (autoSneak.get() && autoSneakTriggerMode.get() == "OnlyFire" && (!autoSneakOnlyAwp.get() || awp())) if (autoSneakMode.get() == "Packet")
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SNEAKING, 0)) else mc.gameSettings.keyBindSneak.pressed = true

            val targetVecY =
                if (head.get() && canSeeHead(player,it) && feet.get() && canSeeFeet(player,it) && chest.get() && canSeeChest(player,it))
                    if (priority.get() == "Head") it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                    else if (priority.get() == "Chest") it.posY +  chestAimOffset.get()
                    else if (priority.get() == "Feet") it.posY + feetAimOffset.get() else return
                else if(head.get() && canSeeHead(player,it) && chest.get() && canSeeChest(player,it) && !canSeeFeet(player,it) && !feet.get())
                    if (priority.get() == "Head") it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                    else if (priority.get() == "Chest") it.posY +  chestAimOffset.get() else return
                else if (head.get() && canSeeHead(player,it) && feet.get() && canSeeFeet(player,it) && !canSeeChest(player,it) && !chest.get())
                    if (priority.get() == "Head") it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                    else if (priority.get() == "Feet") it.posY + feetAimOffset.get() else return
                else if (chest.get() && canSeeChest(player,it) && feet.get() && canSeeFeet(player,it) && !canSeeHead(player,it) && !head.get())
                    if (priority.get() == "Chest") it.posY +  chestAimOffset.get()
                    else if (priority.get() == "Feet") it.posY + feetAimOffset.get() else return
                else if (head.get() && canSeeHead(player,it)) it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                else if (chest.get() && canSeeChest(player,it)) it.posY +  chestAimOffset.get()
                else if (feet.get() && canSeeFeet(player,it)) it.posY + feetAimOffset.get()
                else if (
                canSeePlayer2(player,it)
                || canSeePlayer(player,it)
                || canSeePlayer3(player,it)
                || canSeePlayer4(player,it)
            ) it.posY + it.eyeHeight * eyeHeight.get() - boundingBoxOffsetY.get() + it.posY - it.prevPosY
            else return

                val targetVecX =
                    if (!boundingBox.get() && !vec.get())
                        if (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX
                    else if (!canSeeHead(player,it))
                        if (canSeePlayer(player,it)) if (!customBoundingBox.get()) it.posX + boundingBoxMaxX.get() + boundingBoxMaxOffsetX.get() else it.posX + it.entityBoundingBox.maxX - boundingBoxMinOffsetX.get()
                        else if (canSeePlayer2(player,it)) if (!customBoundingBox.get()) it.posX - boundingBoxMinX.get() + boundingBoxMinOffsetX.get() else it.posX + it.entityBoundingBox.minX - boundingBoxMaxOffsetX.get()
                        else if (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX
                    else if  (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX

                val targetVecZ =
                    if (!boundingBox.get() && !vec.get())
                        if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ
                    else if (!canSeeHead(player,it))
                        if (canSeePlayer3(player,it)) if (!customBoundingBox.get()) it.posZ + boundingBoxMaxZ.get() + boundingBoxMaxOffsetZ.get() else it.posZ + it.entityBoundingBox.maxZ - boundingBoxMinOffsetZ.get()
                        else if(canSeePlayer4(player,it)) if (!customBoundingBox.get()) it.posZ - boundingBoxMinZ.get() + boundingBoxMinOffsetZ.get()  else it.posZ + it.entityBoundingBox.minZ - boundingBoxMaxOffsetZ.get()
                        else if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ
                    else if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ

                val targetVec = Vec3(targetVecX, targetVecY, targetVecZ)

                val playerVecY =if (mc.thePlayer.isSneaking) player.posY + player.eyeHeight - sneakYOffset.get() + playerVecYVecYOffset.get() else player.posY + player.eyeHeight + playerVecYVecYOffset.get()
                val playerVecX = if (playerPredict.get()) player.posX + (player.posX - player.prevPosX) * playerPredictSize.get() else player.posX
                val playerVecZ = if (playerPredict.get()) player.posZ + (player.posZ - player.prevPosZ) * playerPredictSize.get() else player.posZ


            val playerVec = Vec3(playerVecX, playerVecY, playerVecZ)
                val targetFuturePosition = predictFuturePosition(it)
                val rotation = if (predictB.get()) getRotationTo(playerVec, targetFuturePosition) else getRotationTo(playerVec, targetVec)
            val yaw = rotation.yaw + jitterValue2
            val pitch = rotation.pitch + pitchOffset.get() + jitterValue

                if (noSpreadValue.get() && noSpreadMode.get() == "Packet" && !awp()) { if (type == 0) if (resettick < noSpreadTick.get()) {resettick++ ;
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(if (rifle()) 0 else 1))} else {resettick = 0 ; type = 1} else {mc.netHandler.addToSendQueue(C09PacketHeldItemChange(3)); type = 0}}
                if (noSpreadValue.get() && noSpreadMode.get() == "Switch" && !awp()) { if (type == 0) if (resettick < noSpreadTick.get()) {resettick++ ;
                    mc.thePlayer.inventory.currentItem = if (rifle()) 0 else 1} else {resettick = 0 ; type = 1} else {mc.thePlayer.inventory.currentItem = 3 ; type = 0}}

            if (rotateValue.get())  RotationUtils.setTargetRotation(Rotation(yaw, pitch)) else{mc.thePlayer.rotationYaw = yaw ; mc.thePlayer.rotationPitch = pitch}
            when (fireMode.get()) {"Packet" -> { if (timeLimitedAwpOnly.get() && awp()) { if (it.onGround && timeLimitedPrediction.get()) {
                time = if (time < timeLimitedPredictionTicksValue.get() + Random.nextInt(minRandomRange.get(), maxRandomRange.get())) time + 1 else 0
                if (time == 0) if (targetThroughWallPredictFire.get() || canSeeHead(player,it))
                    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                }else if (!fireLimit.get() || (!it.onGround && (it.posY - it.prevPosY) in fallValue.get()..posYValue.get()))
                    if (targetThroughWallPredictFire.get() || canSeeHead(player,it))
                        mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)) }
            else mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)) }else -> { if (tick < fireTick.get()) tick++ else tick = 0
                mc.gameSettings.keyBindUseItem.pressed = tick < fireTick.get()}} }?: run {
                if (fireMode.get() == "Legit") mc.gameSettings.keyBindUseItem.pressed = false
                if (autoSneakMode.get() == "Packet") mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SNEAKING,0))
                else mc.gameSettings.keyBindSneak.pressed = false
                ticks = 0;tick = 0;time = 0}
                if (!autoSneakOnlyAwp.get() || awp()) {
                if (autoSneak.get() && autoSneakTriggerMode.get() == "Always") {
                when (autoSneakMode.get()) {"Packet" -> mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SNEAKING, 0))
                    else -> mc.gameSettings.keyBindSneak.pressed = true}}}}

    private fun getPredictedVec3(entity: EntityPlayer, predictSize: Double, predict: Boolean): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX = if (predict) entity.posX + (entity.posX - entity.prevPosX) * predictSize else entity.posX
        val vecZ = if (predict) entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize else entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getTargetFeet(entity: EntityPlayer, predictSize: Double, predict: Boolean): Vec3 {
        val vecY = entity.posY + feetDetectionOffset.get()
        val vecX = if (predict) entity.posX + (entity.posX - entity.prevPosX) * predictSize else entity.posX
        val vecZ = if (predict) entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize else entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getTargetChest(entity: EntityPlayer, predictSize: Double, predict: Boolean): Vec3 {
        val vecY = entity.posY + chestDetectionOffset.get()
        val vecX = if (predict) entity.posX + (entity.posX - entity.prevPosX) * predictSize else entity.posX
        val vecZ = if (predict) entity.posZ + (entity.posZ - entity.prevPosZ) * predictSize else entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3(entity: EntityPlayer): Vec3 {
        val vecY = if (mc.thePlayer.isSneaking) entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get() - sneakYOffset.get() else entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3A(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX = if (customBoundingBox.get()) entity.posX + boundingBoxMaxX.get() else entity.entityBoundingBox.maxX
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3B(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX = if (customBoundingBox.get()) entity.posX - boundingBoxMinX.get() else entity.entityBoundingBox.minX
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3C(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ = if (customBoundingBox.get()) entity.posZ + boundingBoxMaxZ.get() else entity.entityBoundingBox.maxZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3D(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ = if (customBoundingBox.get()) entity.posZ - boundingBoxMinZ.get() else entity.entityBoundingBox.minZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getVec3(player)
        val targetVec = getVec3A(target)
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeePlayer2(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getVec3(player)
        val targetVec = getVec3B(target)
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeePlayer3(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getVec3(player)
        val targetVec = getVec3C(target)
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeePlayer4(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getVec3(player)
        val targetVec = getVec3D(target)
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeeHead(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getVec3(player)
        val targetVec = getVec3(target)
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeeFeet(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getPredictedVec3(player, playerThroughWallPredictSize.get().toDouble(), playerThroughWallPredict.get())
        val targetVec = getTargetFeet(target, targetThroughWallPredictSize.get().toDouble(), targetThroughWallPredict.get())
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
    private fun canSeeChest(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = getPredictedVec3(player, playerThroughWallPredictSize.get().toDouble(), playerThroughWallPredict.get())
        val targetVec = getTargetChest(target, targetThroughWallPredictSize.get().toDouble(), targetThroughWallPredict.get())
        val result = world.rayTraceBlocks(playerVec, targetVec, stopOnLiquid.get(), ignoreBlockWithoutBoundingBox.get(), returnLastUncollidableBlock.get())
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
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
    fun predictFuturePosition(player: EntityPlayer): Vec3 {

        val velocityX = player.posX - player.prevPosX
        val velocityY = player.posY - player.prevPosY
        val velocityZ = player.posZ - player.prevPosZ
        val bbMaxX = player.entityBoundingBox.maxX
        val bbMaxZ = player.entityBoundingBox.maxZ
        val bbMinX = player.entityBoundingBox.minX
        val bbMinZ = player.entityBoundingBox.minZ
        val reduceX = if (player.posX + velocityX * targetPredictSize.get() > bbMaxX || player.posX + velocityX * targetPredictSize.get() < bbMinX) 0.01 else 0
        val reduceZ = if (player.posZ + velocityZ * targetPredictSize.get() > bbMaxZ || player.posZ + velocityZ * targetPredictSize.get() < bbMinZ) 0.01 else 0
        println("$bbMaxX   $bbMinX   $bbMinZ   $bbMaxZ")
        val futureX = player.posX + velocityX * targetPredictSize.get() - reduceX.toDouble()
        val futureY = player.posY + player.eyeHeight * eyeHeight.get()
        val futureZ = player.posZ + velocityZ * targetPredictSize.get() - reduceZ.toDouble()
        return Vec3(futureX, futureY, futureZ)
    }
    private fun awp(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.golden_hoe) {
                return true
            }
        }
        return false
    }
    private fun rifle(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (whitelistM4.get() && stack.item == Items.iron_hoe
                        || stack.item == Items.stone_hoe && whitelistAK.get()
                        || stack.item == Items.stone_shovel && whitelistMP7.get()
                        || stack.item == Items.diamond_shovel) && whitelistShotgun.get()) {
                return true
            }
        }
        return false
    }
    private fun ak(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.stone_hoe)) {
                return true
            }
        }
        return false
    }
    private fun m4(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.iron_hoe)) {
                return true
            }
        }
        return false
    }
    private fun mp7(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.stone_shovel)) {
                return true
            }
        }
        return false
    }
    private fun p250(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.wooden_pickaxe)) {
                return true
            }
        }
        return false
    }
    private fun deagle(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.golden_pickaxe)) {
                return true
            }
        }
        return false
    }
    private fun shotgun(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.diamond_shovel)) {
                return true
            }
        }
        return false
    }
}