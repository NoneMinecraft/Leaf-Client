package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.MainLib.Jump
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import java.lang.Math.random

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {
    private val mode = ListValue("Mode", arrayOf("Cancel","Jump","IntaveJump","IntaveTimer","IntaveReduce",
        "Custom","CustomTimer","Intave","JumpReduce","Intave2","Mid","SlowAir", "Polar","LegitJump","AAC5"), "Cancel")
    private val customX = FloatValue("CustomX",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customY = FloatValue("CustomY",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customZ = FloatValue("CustomZ",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customMaxHurtTime = IntegerValue("CustomMaxHurtTime",10,0,10).displayable{mode.get() == "Custom"}
    private val customMinHurtTime = IntegerValue("CustomMinHurtTime",0,0,10).displayable{mode.get() == "Custom"}
    private val customTimerLow = FloatValue("CustomTimer-Low",0.7F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerMax = FloatValue("CustomTimer-Max",1.2F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerLowTick = IntegerValue("CustomTimer-LowTick",5,0,10).displayable{mode.get() == "CustomTimer"}
    private val customTimerC03 = BoolValue("CustomTimer-C03",true).displayable{mode.get() == "CustomTimer"}

    private val velocity = FloatValue("Velocity",0.66F,0F,1F).displayable{mode.get() == "Intave"}
    private val maxHurtTime = IntegerValue("MaxHurtTime",6,0,10).displayable{mode.get() == "Intave"}
    private val minHurtTime = IntegerValue("MinHurtTime",3,0,10).displayable{mode.get() == "Intave"}
    private val objectMouseOver = BoolValue("ObjectMouseOver", false).displayable{mode.get() == "Intave"}
    private val forward = BoolValue("Forward",true).displayable{mode.get() == "Intave"}
    private val swingInProgress = BoolValue("SwingInProgress",true).displayable{mode.get() == "Intave"}
    private val sprint = BoolValue("Sprint",true).displayable{mode.get() == "Intave"}
    private val noGUI = BoolValue("NoGUI",true).displayable{mode.get() == "Intave"}
    private val jump2 = BoolValue("Jump",true).displayable{mode.get() == "Intave"}
    private val reduceYValue = BoolValue("ReduceY",false).displayable{mode.get() == "Intave"}
    private val reduceY = FloatValue("ReduceVelocity", 0.99f, 0f, 1f).displayable{mode.get() == "Intave"}
    private val onHurt = BoolValue("OnHurt",true).displayable{mode.get() == "Intave"}
    private val debug = BoolValue("Debug", false)
    var jump = false
    private var jumped = 0
    var jumped2 = false


    @EventTarget
     fun onAttack(event: AttackEvent) {
        when (mode.get()) {
            "Polar" ->{
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime <6 && mc.thePlayer.isSwingInProgress){
                        mc.thePlayer.motionX *= 0.45
                        mc.thePlayer.motionZ *= 0.45
                        mc.thePlayer.isSprinting = false
                        if(debug.get()){
                            ChatPrint("0.45")
                        }
                    }
                }
            }

            "SlowAir" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in 7..10 && mc.thePlayer.motionX < 0.15 && mc.thePlayer.motionZ < 0.15) {
                                 mc.thePlayer.motionX *= 0.888F
                                 mc.thePlayer.motionX *= 0.888F
                    }
                }
            }
            "Mid" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime == 5 && mc.thePlayer.isSwingInProgress && mc.thePlayer.motionY >= 0.001) {
                        mc.thePlayer.motionX *= mc.thePlayer.motionX - mc.thePlayer.motionY / 0.8989999999999999
                        mc.thePlayer.motionZ *= mc.thePlayer.motionZ - mc.thePlayer.motionY / 0.8989999999999999
                       if (mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42
                    }
                }
            }
            "Intave" -> {
                if (objectMouseOver.get() && mc.objectMouseOver == mc.thePlayer) {return}
                if (!onHurt.get() || mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.onGround && jump2.get()) mc.gameSettings.keyBindJump.pressed = true else mc.gameSettings.keyBindJump.pressed = false
                    if (mc.thePlayer.hurtTime in minHurtTime.get()..maxHurtTime.get() &&
                        (!forward.get() || mc.gameSettings.keyBindForward.pressed) &&
                        (!swingInProgress.get()||mc.thePlayer.isSwingInProgress) && (!sprint.get() || mc.thePlayer.isSprinting) &&
                        (!noGUI.get() || mc.currentScreen == null)) {
                        if (reduceYValue.get()) mc.thePlayer.motionY *= reduceY.get()
                        mc.thePlayer.motionZ *= velocity.get()
                        mc.thePlayer.motionX *= velocity.get()
                        if(debug.get()) ChatPrint("[Velocity] ${velocity.get()}")
                    }
                }
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        when (mode.get()) {
            "Cancel" -> {
                    val packet = event.packet
                    if (packet is S12PacketEntityVelocity) {
                        event.cancelEvent()
                    }
            }
            "CustomTimer" -> {
                if (
                    customTimerC03.get() &&
                    event.packet is C03PacketPlayer && !(
                            event.packet is C04PacketPlayerPosition
                                    || event.packet is C03PacketPlayer.C05PacketPlayerLook
                                    || event.packet is C03PacketPlayer.C06PacketPlayerPosLook)
                ) {
                    event.cancelEvent()
                }
            }

            "IntaveTimer" -> {
                if (
                    mc.thePlayer.hurtTime != 0 &&
                    event.packet is C03PacketPlayer && !(
                            event.packet is C03PacketPlayer.C04PacketPlayerPosition
                                    || event.packet is C03PacketPlayer.C05PacketPlayerLook
                                    || event.packet is C03PacketPlayer.C06PacketPlayerPosLook)
                ) {
                    event.cancelEvent()
                }
            }
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime != 0) {
            when (mode.get()) {
                "AAC5"->{
                    if (mc.thePlayer.hurtTime> 1) {
                        mc.thePlayer.motionX *= 0.81
                        mc.thePlayer.motionZ *= 0.81
                    }
                }
                "JumpReduce" -> {
                    if (mc.thePlayer.onGround && mc.gameSettings.keyBindJump.pressed){
                            mc.thePlayer.motionX *= 0.09
                            mc.thePlayer.motionX *= 0.09
                    }
                }
                "Custom" -> {
                    if (mc.thePlayer.hurtTime in customMinHurtTime.get()..customMaxHurtTime.get()){
                        mc.thePlayer.motionX = customX.get().toDouble()
                        mc.thePlayer.motionY = customY.get().toDouble()
                        mc.thePlayer.motionZ = customZ.get().toDouble()
                    }
                }
                "IntaveJump" -> {
                    if (mc.thePlayer.hurtTime == 9) {
                        if (++jumped % 2 == 0 && mc.thePlayer.onGround && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                            mc.gameSettings.keyBindJump.pressed = true
                            jumped = 0
                        }
                    } else {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                    }
                }
                "CustomTimer" -> {
                    if (mc.thePlayer.hurtTime > customTimerLowTick.get()){
                        mc.timer.timerSpeed = customTimerLow.get()
                    }else if (!mc.thePlayer.onGround){
                        mc.timer.timerSpeed = customTimerMax.get()
                    }
                }
                "IntaveTimer" -> {
                    if (mc.thePlayer.hurtTime > 5){
                        mc.timer.timerSpeed = 0.789F
                    }else if (!mc.thePlayer.onGround){
                        mc.timer.timerSpeed = 1.321F
                    }
                }
                "IntaveReduce" -> {
                    if (mc.thePlayer.hurtTime in 9..10 && mc.thePlayer.isSwingInProgress && mc.gameSettings.keyBindForward.pressed) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionX *= 0.898
                            mc.thePlayer.motionZ *= 0.898
                        }
                    }
                }
            }
        }
    }  override val tag: String
        get() = mode.get()

    override fun onDisable() {
        jump = false
        jumped = 0
        jumped2 = false
    }
}