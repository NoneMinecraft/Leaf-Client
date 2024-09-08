package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MoveEvent
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
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3

@ModuleInfo(name = "HackAura", category = ModuleCategory.COMBAT)
class HackAura : Module() {
    val player = mc.thePlayer

    private  val Range = FloatValue("Range", 6f, 1f, 8f)
    private val attackMode = ListValue("AttackMode", arrayOf("C02","KeyBind"),"C02")
    private  val autoBlockMode = ListValue("AutoBlockMode", arrayOf("KeyBind","Vanilla"),"KeyBind")
    private  val rotateValue = BoolValue("SilentRotate", true)
    private  val keepLength = IntegerValue("keepLength", 1, 1, 20)
    private  val Strafe = ListValue("Strafe", arrayOf("Strict","OFF"),"OFF")
    var isAim = false
    override fun onDisable() {
        isAim = false
        mc.gameSettings.keyBindUseItem.pressed = false
    }
    var rotation: Rotation? = null
    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        val player = mc.thePlayer ?: return

        val  targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != player && EntityUtils.isSelected(it, true) }
            .filter { it.getDistanceToEntityBox(player) <= Range.get() }
            .firstOrNull {true}
        targetPlayer?.let {
            val targetVec = Vec3(it.posX+(it.posX - it.prevPosX),
                (it.posY + it.eyeHeight*0.8)+it.posY-it.prevPosY,
                it.posZ+(it.posZ-it.prevPosZ))
            val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
            rotation = getRotationTo(playerVec, targetVec)

            if (rotateValue.get()) {
                RotationUtils.setTargetRotation(rotation, keepLength.get())
            }else{
                mc.thePlayer.rotationYaw = rotation!!.yaw
                mc.thePlayer.rotationPitch = rotation!!.pitch
            }
            if (autoBlockMode.get() == "Vanilla"){
                mc.netHandler.addToSendQueue(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
            }else{
                mc.gameSettings.keyBindUseItem.pressed = true
            }
            if (attackMode.get() == "C02"){
                mc.netHandler.addToSendQueue(C02PacketUseEntity(targetPlayer, C02PacketUseEntity.Action.ATTACK))
            }else{
                mc.gameSettings.keyBindAttack.pressed=true
            }
        }
        if (targetPlayer == null){
            mc.gameSettings.keyBindUseItem.pressed = false
            isAim = false
        }else{
            isAim = true
        }
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
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (isAim && Strafe.get() == "Strict"){
    }
    }
}