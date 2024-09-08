/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import akka.util.Switch
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.TimerUtil
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import scala.annotation.switch
import javax.annotation.meta.When
import kotlin.math.atan2

@ModuleInfo(name = "MCGOBot", category = ModuleCategory.COMBAT)
class MCGOBot : Module() {

    private val PitchOffset = FloatValue("PitchOffset", 1.5F, -5F, 5F)
    private val predictsize = FloatValue("PredictSize", 4.3F, 0F, 10F)
    private val predictsize2 = FloatValue("ThroughWallPredictSize", 10F, 0F, 30F)
    private val FireMode = ListValue("FireMode", arrayOf("Use","C08"),"Use")
    private val AntiAim = BoolValue("AntiAim", false)
    private val EStop = BoolValue("EStopHelper", true)
    private val Reset = BoolValue("Reset", true)
    private val ResetMode = ListValue("ResetMode", arrayOf("Switch","Spoof"),"Switch")
    private val RestTick = IntegerValue("RestTick",1,0,5)
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



        if (isWoodenPickaxe() && !isMP7() && !isM4ORAK()) {autoSwitchTick = 3}else{autoSwitchTick=0}

        val player = mc.thePlayer ?: return

        target = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= 100 }
            .firstOrNull { true }
        target?.let {
            if (!canSeePlayer(player, it)){

            }
        }


        if (targetPlayer == null){
            mc.gameSettings.keyBindUseItem.pressed = false
            ticks = 0
            tick = 0
        }

        if (AntiAim.get()&&targetPlayer == null){
            RotationUtils.setTargetRotation(Rotation(mc.thePlayer.rotationYaw, 85.55556F))
        }

        targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= 100 }
            .firstOrNull { canSeePlayer(player, it) }
        val speed = targetPlayer?.let {
            val motionX = it.motionX
            val motionY = it.motionY
            val motionZ = it.motionZ
            Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ)
        } ?: 0.0
        targetPlayer?.let {
            val targetVec = Vec3(it.posX+(it.posX - it.prevPosX) * predictsize.get(),
                (it.posY + it.eyeHeight*0.8)+it.posY-it.prevPosY,
                it.posZ+(it.posZ-it.prevPosZ)*predictsize.get())
            val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
            val rotation = getRotationTo(playerVec, targetVec)

            val Yaw = rotation.yaw
            val Pitch  = rotation.pitch + PitchOffset.get()


            if (rotateValue.get()) {
                RotationUtils.setTargetRotation(Rotation(Yaw, Pitch))
            } else {
                mc.thePlayer.rotationYaw = Yaw
                mc.thePlayer.rotationPitch = Pitch
            }

            if (EStop.get() && mc.gameSettings.keyBindLeft.pressed && mc.gameSettings.keyBindRight.pressed) {
                mc.gameSettings.keyBindLeft.pressed = false
                mc.gameSettings.keyBindRight.pressed = false
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
            }

            if (FireMode.get() == "C08"){
                mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement())
            }else{
                if (tick < 1) {
                    tick ++
                    mc.gameSettings.keyBindUseItem.pressed = true
                }else{
                    tick = 0
                    mc.gameSettings.keyBindUseItem.pressed = false
                }
                if (Reset.get()){
                    when (type){
                        0 ->{
                            if (resettick <RestTick.get()+autoSwitchTick){
                                resettick ++
                                if (ResetMode.get() == "Switch"){
                                    if (isWoodenPickaxe() && !isMP7() && !isM4ORAK()) {
                                        mc.thePlayer.inventory.currentItem = 1
                                    }else{
                                        mc.thePlayer.inventory.currentItem = 0
                                    }
                                }else{
                                    if (isWoodenPickaxe() && !isMP7() && !isM4ORAK()) {
                                        mc.thePlayer.inventory.currentItem = 1
                                    }else{
                                        mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(0))
                                    }}
                            }else{
                                resettick = 0
                                type = 1
                            }
                        }
                        1 ->{
                            if (ResetMode.get() == "Switch"){
                                mc.thePlayer.inventory.currentItem = 3
                            }else{
                                mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(3))
                            }
                            type = 0
                        }
                    }
                }
            }
        }
    }


    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
        val targetVec = Vec3(target.posX+(target.posX - target.prevPosX) * predictsize2.get(),
            (target.posY + target.eyeHeight*0.8)+target.posY-target.prevPosY,
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
