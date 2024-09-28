package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.MainLib.Jump
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion
import java.lang.Math.random

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {
    private val mode = ListValue("Mode", arrayOf("Cancel","Jump","IntaveJump","IntaveTimer","IntaveReduce","OldIntaveTimer",
        "Custom","CustomTimer","Intave","JumpReduce","Intave2","Mid","SlowAir",
        "Intave4","Intave3","Polar","TickReduce","Intave5"), "Cancel")
    private val customX = FloatValue("CustomX",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customY = FloatValue("CustomY",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customZ = FloatValue("CustomZ",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customMaxHurtTime = IntegerValue("CustomMaxHurtTime",10,0,10).displayable{mode.get() == "Custom"}
    private val customMinHurtTime = IntegerValue("CustomMinHurtTime",0,0,10).displayable{mode.get() == "Custom"}
    private val customTimerLow = FloatValue("CustomTimer-Low",0.7F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerMax = FloatValue("CustomTimer-Max",1.2F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerLowTick = IntegerValue("CustomTimer-LowTick",5,0,10).displayable{mode.get() == "CustomTimer"}
    private val customTimerC03 = BoolValue("CustomTimer-C03",true).displayable{mode.get() == "CustomTimer"}
    private val reverse = BoolValue("Reveres", false).displayable{mode.get() == "Intave"}
    private val yreuce = FloatValue("ReduceY", 0.05f, 0f, 0.5f).displayable{mode.get() == "Intave"}
    private val jumpValue = BoolValue("Jump", false).displayable{mode.get() == "Intave4"}
    private val xzOnHit = FloatValue("XZ-on-hit", 0.6f, 0.0f, 1.0f).displayable{mode.get() == "Intave4"}
    private val xzOnSprintHit = FloatValue("XZ-on-sprint-hit", 0.6f, 0.0f, 1.0f).displayable{mode.get() == "Intave4"}
    private val reduceUnnecessarySlowdown = BoolValue("Reduce-Unnecessary-Slowdown", false).displayable{mode.get() == "Intave4"}
    private val chance = FloatValue("Chance", 100.0f, 0.0f, 100.0f).displayable{mode.get() == "Intave4"}
    private val jumpInInv = BoolValue("Jump-In-Inv", false).displayable{mode.get() == "Intave4"}
    private val jumpChance = FloatValue("Jump-Chance", 80.0f, 0.0f, 100.0f).displayable{mode.get() == "Intave4"}
    private val notWhileSpeed = BoolValue("Not-While-Speed", false).displayable{mode.get() == "Intave4"}
    private val notWhileJumpBoost = BoolValue("Not-While-JumpBoost", false).displayable{mode.get() == "Intave4"}
    private val debug = BoolValue("Debug", false)
    val cancelHorizontal = BoolValue("CancelHorizontalVelocity", true)
    val cancelVertical = BoolValue("CancelVerticalVelocity", true)
    var jump = false
    private var jumped = 0
    var jumped2 = false
    private var reduced = false
    @EventTarget
    fun onPostVelocity(event: TickEvent) {
        when (mode.get()) {
            "Intave4" -> {
        if (noAction()) return

        if (jumpValue.get()) {
            if (random() > jumpChance.get() / 100) return

            val player = mc.thePlayer
            if (player != null && player.onGround && (jumpInInv.get() || mc.currentScreen == null)) {
                player.jump()
            }
        }
        reduced = false
    }
        }
    }


    @EventTarget
     fun onAttack(event: AttackEvent) {
        when (mode.get()) {
            "Intave5" ->{
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in 1..6 &&mc.thePlayer.isSwingInProgress && mc.thePlayer.isSprinting) {
                        mc.thePlayer.motionZ *= 0.676
                        mc.thePlayer.motionX *= 0.676
                        if(debug.get()){
                            ChatPrint("0.676")
                        }
                    }
                }
            }
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
            "Intave3" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime == 10 && mc.thePlayer.onGround){
                        Jump(true)
                    }else{
                        Jump(false)
                    }
                    if (mc.thePlayer.hurtTime in 3..6 && mc.thePlayer.isSwingInProgress && mc.thePlayer.onGround){
                        if (mc.thePlayer.isSprinting){
                            val targetPlayer = mc.theWorld.playerEntities
                                .filterIsInstance<EntityPlayer>()
                                .filter { it != mc.thePlayer && EntityUtils.isSelected(it, true) }
                                .filter { it.getDistanceToEntityBox(mc.thePlayer) < 2 }
                                .firstOrNull {true}
                            targetPlayer?.let {
                                    mc.thePlayer.motionX *= mc.thePlayer.motionX * 0.999
                                    mc.thePlayer.motionZ *= mc.thePlayer.motionZ * 0.999
                                if (debug.get()) {
                                    ChatPrint("hurttime:" + mc.thePlayer.hurtTime + " motionX:" + mc.thePlayer.motionX * 0.999 + " motionZ:" + mc.thePlayer.motionZ * 0.999)
                                }
                            }
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
            "Intave4" -> {
                val player = mc.thePlayer ?: return
                val target = event.targetEntity

                if (target is EntityLivingBase && player.hurtTime > 0) {
                    if (noAction()) return
                    if (random() > chance.get() / 100) return
                    if (reduceUnnecessarySlowdown.get() && reduced) return

                    if (player.isSprinting) {
                        player.motionX *= xzOnSprintHit.get()
                        player.motionZ *= xzOnSprintHit.get()
                    } else {
                        player.motionX *= xzOnHit.get()
                        player.motionZ *= xzOnHit.get()
                    }
                    reduced = true
                    if (debug.get()) {
                        ChatPrint("Reduced: ${player.motionX}, ${player.motionZ}")
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
                if (mc.objectMouseOver == null) {
                    return
                }
                if (mc.thePlayer.hurtTime <= 6 && mc.thePlayer.isSwingInProgress && mc.thePlayer.hurtTime > 0) {
                    if (reverse.get() && !mc.thePlayer.onGround) {
                        mc.thePlayer.motionX =
                            -kotlin.math.sin(java.lang.Math.toRadians(mc.thePlayer.rotationYaw.toDouble())) * 0.019999999552965164
                        mc.thePlayer.motionZ =
                            kotlin.math.cos(java.lang.Math.toRadians(mc.thePlayer.rotationYaw.toDouble())) * 0.019999999552965164
                    }
                    val player = mc.thePlayer
                    player.motionY *= 1.0 - yreuce.get()
                }
            }
            "Intave2" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in 3..6 && mc.gameSettings.keyBindForward.pressed &&mc.thePlayer.isSwingInProgress && mc.thePlayer.isSprinting) {
                        mc.thePlayer.motionZ *= 0.676
                        mc.thePlayer.motionX *= 0.676
                        if(debug.get()){
                            ChatPrint("0.676")
                        }
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
                        if (!cancelVertical.get()) mc.thePlayer.motionY = packet.getMotionY().toDouble() / 8000.0
                        if (!cancelHorizontal.get()) {
                            mc.thePlayer.motionX = packet.getMotionX().toDouble() / 8000.0
                            mc.thePlayer.motionZ = packet.getMotionZ().toDouble() / 8000.0
                        }
                    }
            }
            "CustomTimer" -> {
                if (
                    customTimerC03.get() &&
                    event.packet is C03PacketPlayer && !(
                            event.packet is C03PacketPlayer.C04PacketPlayerPosition
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
                "TickReduce" -> {
                    when (mc.thePlayer.hurtTime){
                        10 -> {mc.thePlayer.motionX *= 0.99
                            mc.thePlayer.motionZ *= 0.99}
                        9 -> {mc.thePlayer.motionX *= 0.88
                            mc.thePlayer.motionZ *= 0.88}
                        8 -> {mc.thePlayer.motionX *= 0.77
                            mc.thePlayer.motionZ *= 0.77}
                        7 -> {mc.thePlayer.motionX *= 0.66
                            mc.thePlayer.motionZ *= 0.66}
                        6 -> {mc.thePlayer.motionX *= 0.55
                            mc.thePlayer.motionZ *= 0.55}
                        5-> {mc.thePlayer.motionX *= 0.44
                            mc.thePlayer.motionZ *= 0.44}
                        4 -> {mc.thePlayer.motionX *= 0.33
                            mc.thePlayer.motionZ *= 0.33}
                        3 -> {mc.thePlayer.motionX *= 0.22
                            mc.thePlayer.motionZ *= 0.22}
                        2 -> {mc.thePlayer.motionX *= 0.11
                            mc.thePlayer.motionZ *= 0.11}
                        1 -> {mc.thePlayer.motionX *= 0.0
                            mc.thePlayer.motionZ *= 0.0}
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
                "OldIntaveTimer" -> {
                    if (mc.thePlayer.hurtTime > 5){
                        mc.timer.timerSpeed = 0.779F
                    }else if (!mc.thePlayer.onGround){
                        mc.timer.timerSpeed = 1.536F
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
    private fun noAction(): Boolean {
        val player = mc.thePlayer ?: return true
        return player.activePotionEffects.any {
            (notWhileSpeed.get() && it.potionID == Potion.moveSpeed.id)
                    || (notWhileJumpBoost.get() && it.potionID == Potion.jump.id)
        }
    }
    override fun onDisable() {
        jump = false
        jumped = 0
        jumped2 = false
    }
}