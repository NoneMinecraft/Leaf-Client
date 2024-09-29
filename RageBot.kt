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
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3

@ModuleInfo(name = "RageBot", category = ModuleCategory.COMBAT)
class RageBot : Module() {
    private val range = FloatValue("Range", 100F, 50F, 200F)
    private val PitchOffset = FloatValue("PitchOffset", 1.5F, -5F, 5F)
    private val predictsize = FloatValue("PredictSize", 4.3F, 0F, 10F)
    private val predictsize2 = FloatValue("ThroughWallPredictSize", 5F, 0F, 20F)
    private val eyeHeight = FloatValue("EyeHeight", 0.8F, -1F, 1F)
    private val FireMode = ListValue("FireMode", arrayOf("Legit","Packet"),"Packet")
    private val noSpreadValue = BoolValue("NoSpread", true)
    private val noSpreadMode = ListValue("NoSpreadMode", arrayOf("Switch","Spoof"),"Switch")
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
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        if (targetPlayer == null){
            mc.gameSettings.keyBindUseItem.pressed = false
            ticks = 0
            tick = 0
        }

        targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= range.get() }
            .firstOrNull { canSeePlayer(player, it) }
        targetPlayer?.let {
            val targetVec = Vec3(it.posX+(it.posX - it.prevPosX) * predictsize.get(),
                (it.posY + it.eyeHeight*eyeHeight.get())+it.posY-it.prevPosY,
                it.posZ+(it.posZ-it.prevPosZ)*predictsize.get())
            val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
            val rotation = getRotationTo(playerVec, targetVec)

            val yaw = rotation.yaw
            val pitch  = rotation.pitch + PitchOffset.get()

            if (noSpreadValue.get()){
                if (noSpreadMode.get() == "Switch") {
                    when (type) {
                        0 -> {
                            if (resettick < 2) {
                                resettick++
                                if (isWoodenPickaxe() && !isMP7() && !isM4ORAK()) mc.thePlayer.inventory.currentItem =
                                    1 else mc.thePlayer.inventory.currentItem = 0
                            } else {
                                resettick = 0
                                type = 1
                            }
                        }

                        1 -> {
                            mc.thePlayer.inventory.currentItem = 3
                            type = 0
                        }
                    }
                }else if (noSpreadMode.get() == "Spoof"){
                    when (type) {0 -> {
                            if (resettick < 2) {
                                resettick++
                                if (isWoodenPickaxe() && !isMP7() && !isM4ORAK()){ mc.netHandler.addToSendQueue(C09PacketHeldItemChange(0))
                                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem)) }else{ mc.netHandler.addToSendQueue(C09PacketHeldItemChange(1))
                                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem)) } } else {resettick = 0
                                type = 1 }}

                        1 -> { mc.netHandler.addToSendQueue(C09PacketHeldItemChange(3))
                            type = 0 }}
                }
            }
            if (rotateValue.get()) {
                RotationUtils.setTargetRotation(Rotation(yaw, pitch))
            } else {
                mc.thePlayer.rotationYaw = yaw
                mc.thePlayer.rotationPitch = pitch
            }


            if (FireMode.get() == "Packet"){
                mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }else{
                if (tick < 1) {
                    tick ++
                    mc.gameSettings.keyBindUseItem.pressed = true
                }else{
                    tick = 0
                    mc.gameSettings.keyBindUseItem.pressed = false
                }

            }
        }
    }


    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
        val targetVec = Vec3(target.posX+(target.posX - target.prevPosX) * predictsize2.get(),
            (target.posY + target.eyeHeight*eyeHeight.get())+target.posY-target.prevPosY,
            target.posZ+(target.posZ-target.prevPosZ)*predictsize2.get())
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
    private fun isWoodenPickaxe(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.wooden_pickaxe) {
                return true
            }
        }
        return false
    }
    private fun isM4ORAK(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && (stack.item == Items.iron_hoe || stack.item == Items.stone_hoe)) {
                return true
            }
        }
        return false
    }
    private fun isMP7(): Boolean {
        val playerInventory = mc.thePlayer.inventory
        for (i in 0 until playerInventory.sizeInventory) {
            val stack = playerInventory.getStackInSlot(i)
            if (stack != null && stack.item == Items.stone_shovel) {
                return true
            }
        }
        return false
    }
}
