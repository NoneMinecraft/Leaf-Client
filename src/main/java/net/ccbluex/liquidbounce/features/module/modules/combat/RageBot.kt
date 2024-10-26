/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
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
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.random.Random

@ModuleInfo(name = "RageBot", category = ModuleCategory.COMBAT)
class RageBot : Module() {
    private val range = FloatValue("Range", 100F, 50F, 200F)
    private val pitchOffset = FloatValue("PitchOffset", 0.2F, 0F, 5F)
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
    private val noSpreadMode = ListValue("NoSpreadMode", arrayOf("Switch","Spoof"),"Switch")
    private val noSpreadTick = IntegerValue("NoSpreadBaseTick", 2, 0, 5)
    private val autoSneak = BoolValue("AutoSneak", true)
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
    private val head = BoolValue("HitBox-Head", true)
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
    private val boundingBoxMaxX = FloatValue("BoundingBoxMaxX", 0.4F, 0.1F, 1F)
    private val boundingBoxMinX = FloatValue("BoundingBoxMinX", 0.4F, 0.1F, 1F)
    private val boundingBoxMaxZ = FloatValue("BoundingBoxMaxZ", 0.4F, 0.1F, 1F)
    private val boundingBoxMinZ = FloatValue("BoundingBoxMinZ", 0.4F, 0.1F, 1F)
    private val boundingBoxMaxOffsetX = FloatValue("BoundingBoxMaxOffsetX", 0F, -1F, 1F)
    private val boundingBoxMinOffsetX = FloatValue("BoundingBoxMinOffsetX", 0F, -1F, 1F)
    private val boundingBoxMaxOffsetZ = FloatValue("BoundingBoxMaxOffsetZ", 0F, -1F, 1F)
    private val boundingBoxMinOffsetZ = FloatValue("BoundingBoxMinOffsetZ", 0F, -1F, 1F)
    private val boundingBoxOffsetY = FloatValue("BoundingBoxOffsetY", 0F, -1F, 1F)

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
    fun onUpdate(event: UpdateEvent) {
            val player = mc.thePlayer ?: return
            targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= range.get() }
            .firstOrNull {hitBoxValue.get() && (chest.get() && canSeeChest(player,it)) || (head.get() && canSeeRealPlayers(player,it)) ||
                    (feet.get() && canSeeFeet(player,it)) || canSeePlayer2(player,it) || canSeePlayer3(player,it) || canSeePlayer4(player,it) || canSeePlayer(player,it)}
            targetPlayer?.let {
            if (targetDebug.get() && it.hurtTime in targetDebugMinHurtTime.get()..targetDebugMaxHurtTime.get()) ChatPrint("§f[§bHit§f] Name:${it.name} Health:${it.health}")
            if (jitterPitch.get() && jitter.get()) jitterValue = if (jitterTick++ < jitterFrequency.get()) -jitterAmplitude.get() else { jitterTick = 0; jitterAmplitude.get() }
            if (jitterYaw.get() && jitter.get()) jitterValue2 = if (jitterTick2++ < jitterFrequency.get()) -jitterAmplitude.get() else { jitterTick2 = 0; jitterAmplitude.get() }
            if (autoSneak.get() && autoSneakTriggerMode.get() == "OnlyFire" && (!autoSneakOnlyAwp.get() || awp())) if (autoSneakMode.get() == "Packet")
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SNEAKING, 0)) else mc.gameSettings.keyBindSneak.pressed = true
            val targetVecY = if(head.get() && canSeeRealPlayers(player,it) && chest.get() && canSeeChest(player,it))
                if (priority.get() == "Head") it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
            else if (priority.get() == "Chest") it.posY +  chestAimOffset.get() else it.posY + feetAimOffset.get() else
                if (canSeePlayer2(player,it) || canSeePlayer(player,it) || canSeePlayer3(player,it) || canSeePlayer4(player,it)) it.posY + it.eyeHeight * eyeHeight.get() - boundingBoxOffsetY.get() + it.posY - it.prevPosY else
                    if (head.get() && canSeeRealPlayers(player,it) && feet.get() && canSeeFeet(player,it))
                    if (priority.get() == "Head") it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY
                else if (priority.get() == "Feet") it.posY + feetAimOffset.get()  else it.posY + feetAimOffset.get() else it.posY + feetAimOffset.get() ;
                if (head.get() && canSeeRealPlayers(player,it)) it.posY + it.eyeHeight * eyeHeight.get() + it.posY - it.prevPosY else if (feet.get() && canSeeFeet(player,it)) it.posY + feetAimOffset.get()
                else if (chest.get() && canSeeChest(player,it)) it.posY + chestAimOffset.get() else it.posY + feetAimOffset.get()
                val targetVecX =if (!boundingBox.get() && vec.get()) if (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX else
                    if (!canSeeRealPlayers(player,it)) if (canSeeRealPlayers(player,it)) it.posX + boundingBoxMaxX.get() + boundingBoxMaxOffsetX.get() else if (canSeePlayer2(player,it)) it.posX - boundingBoxMinX.get() + boundingBoxMinOffsetX.get() else
                        if  (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX else if  (targetPredict.get()) it.posX + (it.posX - it.prevPosX) * targetPredictSize.get() else it.posX
                val targetVecZ =if (!boundingBox.get() && vec.get()) if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ else
                    if (!canSeeRealPlayers(player,it)) if (canSeePlayer3(player,it)) it.posZ + boundingBoxMaxZ.get() + boundingBoxMaxOffsetZ.get() else if(canSeePlayer4(player,it)) it.posZ - boundingBoxMinZ.get() + boundingBoxMinOffsetZ.get() else
                        if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ else if (targetPredict.get()) it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get() else it.posZ
                val targetVec = Vec3(targetVecX, targetVecY, targetVecZ)
            val playerVecY = player.posY + player.eyeHeight
            val playerVecX = if (playerPredict.get()) player.posX + (player.posX - player.prevPosX) * playerPredictSize.get() else player.posX
            val playerVecZ = if (playerPredict.get()) player.posZ + (player.posZ - player.prevPosZ) * playerPredictSize.get() else player.posZ
            val playerVec = Vec3(playerVecX, playerVecY, playerVecZ)
            val rotation = getRotationTo(playerVec, targetVec)
            val yaw = rotation.yaw + jitterValue2                                  //主播别看了
            val pitch = rotation.pitch + pitchOffset.get() + jitterValue
            if (noSpreadValue.get() && noSpreadMode.get() == "Switch" && !awp()) { if (type == 0) if (resettick < noSpreadTick.get()) {resettick++ ;
                mc.thePlayer.inventory.currentItem = if (rifle()) 0 else 1} else {resettick = 0 ; type = 1} else {mc.thePlayer.inventory.currentItem = 3 ; type = 0}}
            if (rotateValue.get())  RotationUtils.setTargetRotation(Rotation(yaw, pitch)) else{mc.thePlayer.rotationYaw = yaw ; mc.thePlayer.rotationPitch = pitch}
            when (fireMode.get()) {"Packet" -> { if (timeLimitedAwpOnly.get() && awp()) { if (it.onGround && timeLimitedPrediction.get()) {
                time = if (time < timeLimitedPredictionTicksValue.get() + Random.nextInt(minRandomRange.get(), maxRandomRange.get())) time + 1 else 0
                if (time == 0) if (targetThroughWallPredictFire.get() || canSeeRealPlayers(player,it))
                    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                }else if (!fireLimit.get() || (!it.onGround && (it.posY - it.prevPosY) in fallValue.get()..posYValue.get()))
                    if (targetThroughWallPredictFire.get() || canSeeRealPlayers(player,it))
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
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3A(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX + boundingBoxMaxX.get()
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3B(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX - boundingBoxMinX.get()
        val vecZ =  entity.posZ
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3C(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ =  entity.posZ + boundingBoxMaxZ.get()
        return Vec3(vecX, vecY, vecZ)
    }
    private fun getVec3D(entity: EntityPlayer): Vec3 {
        val vecY = entity.posY + entity.eyeHeight + baseEyeHeightDetectionOffset.get()
        val vecX =  entity.posX
        val vecZ =  entity.posZ - boundingBoxMinZ.get()
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
    private fun canSeeRealPlayers(player: EntityPlayer, target: EntityPlayer): Boolean {
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
            if (stack != null && (stack.item == Items.iron_hoe || stack.item == Items.stone_hoe || stack.item == Items.stone_shovel)) {
                return true
            }
        }
        return false
    }
}