/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
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
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

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
    var targetPlayer: EntityPlayer? = null
    var tick = 0
    var ticks = 0
    var type = 0
    var resettick = 0
    var autoSwitchTick = 0
    var target: EntityPlayer? = null

    override fun onDisable() {
        resettick = 0
        type = 0
        ticks = 0
        tick = 0
        mc.gameSettings.keyBindUseItem.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        autoSwitchTick = 0
        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING,0))
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return

        targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= range.get() }
            .firstOrNull { canSeePlayer(player, it) }
        targetPlayer?.let {
            if (autoSneak.get() && autoSneakTriggerMode.get() == "OnlyFire"){
                if (!autoSneakOnlyAwp.get() || awp()) {
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
            val targetVec = if(targetPredict.get()) {
                Vec3(
                    it.posX + (it.posX - it.prevPosX) * targetPredictSize.get(),
                    (it.posY + it.eyeHeight * eyeHeight.get()) + it.posY - it.prevPosY,
                    it.posZ + (it.posZ - it.prevPosZ) * targetPredictSize.get()
                )
            } else {
                Vec3(
                    it.posX,
                    (it.posY + it.eyeHeight * eyeHeight.get()) + it.posY - it.prevPosY,
                    it.posZ
                )
            }
            val playerVec = if(playerPredict.get()) {
                Vec3(
                    player.posX + (player.posX - player.prevPosX) * playerPredictSize.get(),
                    player.posY + player.eyeHeight,
                    player.posZ + (player.posZ - player.prevPosZ) * playerPredictSize.get()
                )
            } else {
                Vec3(
                    player.posX,
                    player.posY + player.eyeHeight,
                    player.posZ
                )
            }
            val rotation = getRotationTo(playerVec, targetVec)

            val yaw = rotation.yaw
            val pitch = rotation.pitch + pitchOffset.get()

            if (noSpreadValue.get()) {
                if (noSpreadMode.get() == "Switch" && !awp()) {
                    when (type) {
                        0 -> {
                            if (resettick < noSpreadTick.get()) {
                                resettick++
                                if (rifle()) mc.thePlayer.inventory.currentItem = 0 else mc.thePlayer.inventory.currentItem = 1
                            }else {
                                resettick = 0
                                type = 1
                            }
                        }
                        1 -> {
                            mc.thePlayer.inventory.currentItem = 3
                            type = 0
                        }
                    }
                }
            }
            if (rotateValue.get())  RotationUtils.setTargetRotation(Rotation(yaw, pitch)) else{
                mc.thePlayer.rotationYaw = yaw
                mc.thePlayer.rotationPitch = pitch
            }


            if (fireMode.get() == "Packet") mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)) else{
                if (tick < fireTick.get()) {
                    tick ++
                    mc.gameSettings.keyBindUseItem.pressed = true
                }else{
                    tick = 0
                    mc.gameSettings.keyBindUseItem.pressed = false
                }

            }
        }?: run {
            if (fireMode.get() == "Legit") mc.gameSettings.keyBindUseItem.pressed = false
            if (autoSneakMode.get() == "Packet"){
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SNEAKING,0))
            }else{
                mc.gameSettings.keyBindSneak.pressed = false
            }
            ticks = 0
            tick = 0
        }
        if (!autoSneakOnlyAwp.get() || awp()) {
            if (autoSneak.get() && autoSneakTriggerMode.get() == "Always") {
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
    }

    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = if(playerThroughWallPredict.get()) {
            Vec3(
                player.posX + (player.posX - player.prevPosX) * playerThroughWallPredictSize.get(),
                player.posY + player.eyeHeight,
                player.posZ + (player.posZ - player.prevPosZ) * playerThroughWallPredictSize.get()
            )
        } else {
            Vec3(
                player.posX,
                player.posY + player.eyeHeight,
                player.posZ
            )
        }
        val targetVec = if(targetThroughWallPredict.get()) {
            Vec3(
                target.posX + (target.posX - target.prevPosX) * targetThroughWallPredictSize.get(),
                (target.posY + target.eyeHeight * eyeHeight.get()) + target.posY - target.prevPosY,
                target.posZ + (target.posZ - target.prevPosZ) * targetThroughWallPredictSize.get()
            )
        }else{
            Vec3(
                target.posX,
                (target.posY + target.eyeHeight * eyeHeight.get()) + target.posY - target.prevPosY,
                target.posZ
            )
        }
        val result = world.rayTraceBlocks(playerVec, targetVec, false, true, false)
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
