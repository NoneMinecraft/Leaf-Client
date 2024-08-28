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
    private val Mode = ListValue("Mode", arrayOf("C04", "Motion","LegitJump","FastPos","SetPos","FastMotion","Packet","Diff"), "C04")
    private val MotionValue = FloatValue("MotionValue",0.1F,0.01F,0.42F)
    private val hurttime = IntegerValue("HurtTime",7,1,10)
    private var LastRegen = 0
    private var reverse = false
    var tick = 0
    override fun onDisable() {
        tick = 0
        reverse = false
        LastRegen = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {


        val player = mc.thePlayer
        val hungerLevel = player.foodStats.foodLevel
        val saturationLevel = player.foodStats.saturationLevel

        val currentHealth = player.health
        val maxHealth = player.maxHealth
        val ticks = updateRegenTimer(hungerLevel, saturationLevel, currentHealth, maxHealth)
        if (ticks >= 79 && mc.thePlayer.foodStats.foodLevel > 18){
            ChatPrint("玩家回血")
        }

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
            "Diff" -> {
                val health = target.health
                val hurtTime = target.hurtTime

                val healthDiff = onceFunc(intArrayOf(0, 20), intArrayOf(8, 2))
                val finalDiff = (healthDiff[0] * health + healthDiff[1]).toInt()
                if (mc.thePlayer.onGround) {
                    if (inRange(hurtTime, 0, finalDiff) && !reverse) {
                        setPos(0.0, 0.234, 0.0)
                        reverse = true
                    }
                    if ((hurtTime == 0 || inRange(hurtTime, finalDiff + 1, 10)) && reverse) {
                        setPos(0.0, 0.234, 0.0)
                        reverse = false
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

    fun updateRegenTimer(hunger: Int, saturation: Float, currentHealth: Float, maxHealth: Float): Int {
        if (currentHealth < maxHealth) {
            if (hunger >= 18 && saturation > 0) {
                LastRegen++
                if (LastRegen >= 80) {
                    regenerateHealth()
                    LastRegen = 0
                }
            } else if (hunger >= 20) {
                LastRegen++
                if (LastRegen >= 80) {
                    regenerateHealth()
                    LastRegen = 0
                }
            } else {
                LastRegen++
            }
        } else {
            LastRegen = 0
        }
        return LastRegen
    }
    private fun regenerateHealth() {
        println("Player's health is regenerated.")
    }
private fun onceFunc(arr1: IntArray, arr2: IntArray): IntArray {
    return intArrayOf(arr1[0] * arr2[0], arr1[1] * arr2[1])
}
    private fun inRange(value: Int, min: Int, max: Int): Boolean {
        return value in min..max
    }
    private fun setPos(x: Double, y: Double, z: Double) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z)
    }
    private fun setGround(state: Boolean) {
        mc.thePlayer.onGround = state
    }
}
