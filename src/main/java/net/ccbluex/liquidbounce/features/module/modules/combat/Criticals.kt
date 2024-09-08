package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.EnumAutoDisableType
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S0BPacketAnimation

@ModuleInfo(name = "Criticals", category = ModuleCategory.COMBAT, autoDisable = EnumAutoDisableType.FLAG)
class Criticals : Module() {
    private val Mode = ListValue("Mode", arrayOf("C04", "Motion","LegitJump","FastPos","Diff","SetPos","FastMotion","Packet","More"), "C04")
    private val MotionValue = FloatValue("MotionValue",0.1F,0.01F,0.42F)
    private val hurttime = IntegerValue("HurtTime",7,1,10)
    private var LastRegen = 0
    private val delayValue = IntegerValue("Delay", 0, 0, 1000)
    private var reverse = false
    var tick = 0
    private val timer = MSTimer()
    override fun onDisable() {
        tick = 0
        reverse = false
        LastRegen = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer
        when(Mode.get()){
            "FastMotion" ->{
                if (mc.thePlayer.hurtTime!=0){
                    if (mc.thePlayer.hurtTime.toDouble() in hurttime.get().toDouble()..10.0){
                        if (mc.thePlayer.onGround) {
                           mc.thePlayer.motionY = 0.1423
                        }
                    }else if (mc.thePlayer.hurtTime.toDouble() in 0.0..6.999){
                        if (mc.thePlayer.foodStats.foodLevel < 15){
                            mc.thePlayer.motionY = -0.123
                        }else{
                            mc.thePlayer.motionY = -0.234

                        }
                    }
                }
            }
            "FastPos" ->{
                if (mc.thePlayer.hurtTime!=0){
                if (mc.thePlayer.hurtTime.toDouble() in 7.0..10.0){
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX + 0.0561,mc.thePlayer.posY+0.514,mc.thePlayer.posZ+0.0374)
                    }
                }else if (mc.thePlayer.hurtTime.toDouble() in 0.0..6.999){
                    if (mc.thePlayer.foodStats.foodLevel < 15){
                        mc.thePlayer.motionY = -0.783
                }else{
                        mc.thePlayer.motionY = -0.424
                }
            }
            }
            }
        }
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
       val target = event.targetEntity as? EntityLivingBase ?: return

        when(Mode.get()){

            "More" ->{
                sendCriticalPacket(yOffset = 0.00000000001, ground = false)
                sendCriticalPacket(ground = false)
            }
            "NCP"->{
                sendCriticalPacket(yOffset = 0.11, ground = false)
                sendCriticalPacket(yOffset = 0.1100013579, ground = false)
                sendCriticalPacket(yOffset = 0.0000013579, ground = false)
            }

            "Diff" -> {
                if (criticalCondition() && (delayValue.get() <= 0 || (delayValue.get() > 0 && timer.hasTimePassed(delayValue.get().toLong())))) {
                        val target = event.targetEntity as? EntityLivingBase ?: return
                        val health = target.health
                        val hurtTime = target.hurtTime
                        val healthDiff = onceFunc(arrayOf(0.0, 20.0), arrayOf(8.0, 2.0))
                        val finalDiff = (healthDiff[0] * health + healthDiff[1]).toInt()

                        if (target.onGround) {
                            if (hurtTime in 0..finalDiff && !reverse) {
                                sendPositionPacket(0.0, 0.234, 0.0)
                                reverse = true
                            }

                            if ((hurtTime == 0 || hurtTime in (finalDiff + 1)..10) && reverse) {
                                sendPositionPacket(0.0, 0.234, 0.0)
                                reverse = false
                            }
                        }
                    if (delayValue.get() > 0) {
                        timer.reset()
                    }
                }
            }

            "Packet" ->{
                mc.thePlayer.sendQueue.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 0.0625,
                        mc.thePlayer.posZ,
                        true
                    )
                )
               mc.thePlayer.onGround = false

                mc.thePlayer.sendQueue.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 1.1E-5,
                        mc.thePlayer.posZ,
                        false
                    )
                )
                mc.thePlayer.onGround = false
            }
            "C04" -> {

                mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY+2.43192168e-14, mc.thePlayer.posZ, true))
                mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY-1.265e-256, mc.thePlayer.posZ, false))
            }
            "SetPos"->{
                if (mc.thePlayer.onGround)tick = 0
                if (tick<1){
                    tick ++
                    mc.thePlayer.setPosition(mc.thePlayer.posX ,mc.thePlayer.posY+0.234,mc.thePlayer.posZ)
                }else{
                    mc.thePlayer.setPosition(mc.thePlayer.posX ,mc.thePlayer.posY-0.234,mc.thePlayer.posZ)
                    tick = 0
                }
            }
            "Motion" -> {
                if(mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = MotionValue.get().toDouble()
                }
            }
            "LegitJump" ->{
                mc.gameSettings.keyBindJump.pressed = true
            }
        }
    }override val tag: String
        get() = Mode.get()

private fun onceFunc(arr1: IntArray, arr2: IntArray): IntArray {
    return intArrayOf(arr1[0] * arr2[0], arr1[1] * arr2[1])
}
    private fun inRange(value: Int, min: Int, max: Int): Boolean {
        return value in min..max
    }
    private fun setPos(x: Double, y: Double, z: Double) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z)
    }
    private fun criticalCondition(): Boolean {
        return true
    }

    private fun onceFunc(range1: Array<Double>, range2: Array<Double>): Array<Double> {
        return arrayOf(0.5, 3.0)
    }

    private fun sendPositionPacket(x: Double, y: Double, z: Double) {
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, mc.thePlayer.onGround))
    }

    companion object {
        var reverse = false
    }
    fun sendCriticalPacket(
        xOffset: Double = 0.0,
        yOffset: Double = 0.0,
        zOffset: Double = 0.0,
        ground: Boolean
    ) {
        val x = mc.thePlayer.posX + xOffset
        val y = mc.thePlayer.posY + yOffset
        val z = mc.thePlayer.posZ + zOffset
            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground))
    }
}
