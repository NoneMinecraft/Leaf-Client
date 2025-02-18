package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.MainLib.Jump
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.GameSettings
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.potion.Potion
import java.lang.Math.random
import kotlin.math.abs
import kotlin.math.atan2

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {
    private val mode = ListValue("Mode", arrayOf("Cancel","Jump","IntaveJump","IntaveDouble","IntaveTimer","IntaveReduce",
        "Custom","CustomTimer","Intave","JumpReduce","Intave2","Mid","SlowAir", "OldPolar","PolarFlag","LegitJump","AAC5"), "Cancel")
    private val customX = FloatValue("CustomX",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customY = FloatValue("CustomY",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customZ = FloatValue("CustomZ",0F,0F,1F).displayable{mode.get() == "Custom"}
    private val customMaxHurtTime = IntegerValue("CustomMaxHurtTime",10,0,10).displayable{mode.get() == "Custom"}
    private val customMinHurtTime = IntegerValue("CustomMinHurtTime",0,0,10).displayable{mode.get() == "Custom"}
    private val customTimerLow = FloatValue("CustomTimer-Low",0.7F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerMax = FloatValue("CustomTimer-Max",1.2F,0F,1F).displayable{mode.get() == "CustomTimer"}
    private val customTimerLowTick = IntegerValue("CustomTimer-LowTick",5,0,10).displayable{mode.get() == "CustomTimer"}
    private val customTimerC03 = BoolValue("CustomTimer-C03",true).displayable{mode.get() == "CustomTimer"}
    private val intaveDoubleVelocity = FloatValue("IntaveDouble-Velocity",0.66F,0F,1F).displayable{mode.get() == "IntaveDouble"}
    private val intaveDoubleMaxHurtTime = IntegerValue("IntaveDouble-MaxHurtTime",6,0,10).displayable{mode.get() == "IntaveDouble"}
    private val intaveDoubleMinHurtTime = IntegerValue("IntaveDouble-MinHurtTime",3,0,10).displayable{mode.get() == "IntaveDouble"}
    private val intaveVelocity = FloatValue("Intave-Velocity",0.66F,0F,1F).displayable{mode.get() == "Intave"}
    private val intaveMaxHurtTime = IntegerValue("Intave-MaxHurtTime",6,0,10).displayable{mode.get() == "Intave"}
    private val intaveMinHurtTime = IntegerValue("Intave-MinHurtTime",3,0,10).displayable{mode.get() == "Intave"}
    private val polarFlagChangeTimer = FloatValue("PolarFlag-ChangeTimer",1.06F,0F,2F).displayable{mode.get() == "Intave"}
    private val polarFlagLowTimer = FloatValue("PolarFlag-PolarFlagLowTimer",0.94F,0F,1F).displayable{mode.get() == "Intave"}
    private val polarFlagMaxHurtTime = IntegerValue("PolarFlag-MaxHurtTime",6,0,10).displayable{mode.get() == "Intave"}
    private val polarFlagMinHurtTime = IntegerValue("PolarFlag-MinHurtTime",3,0,10).displayable{mode.get() == "Intave"}
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
    private var hasReceivedVelocity = false
    private var limitUntilJump = 0
    private var attack = false

    @EventTarget
     fun onAttack(event: AttackEvent) {
        attack = true
        when (mode.get()) {
            "OldPolar" ->{
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
                if (objectMouseOver.get() && mc.objectMouseOver == mc.thePlayer) return
                if (!onHurt.get() || mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.onGround && jump2.get()) mc.gameSettings.keyBindJump.pressed = true else mc.gameSettings.keyBindJump.pressed = false
                    if (mc.thePlayer.hurtTime in intaveMinHurtTime.get()..intaveMaxHurtTime.get() &&
                        (!forward.get() || mc.gameSettings.keyBindForward.pressed) &&
                        (!swingInProgress.get()||mc.thePlayer.isSwingInProgress) && (!sprint.get() || mc.thePlayer.isSprinting) &&
                        (!noGUI.get() || mc.currentScreen == null)) {
                        if (reduceYValue.get()) mc.thePlayer.motionY *= reduceY.get()
                        mc.thePlayer.motionZ *= intaveVelocity.get()
                        mc.thePlayer.motionX *= intaveVelocity.get()
                        if(debug.get()) ChatPrint("[Velocity] ${intaveVelocity.get()}")
                    }
                }
            }
            "IntaveDouble" -> {
                if (mc.thePlayer.hurtTime != 0) {
                    if (mc.thePlayer.hurtTime in intaveDoubleMinHurtTime.get()..intaveDoubleMaxHurtTime.get() &&
                       mc.gameSettings.keyBindForward.pressed && mc.thePlayer.hurtTime != 9 && mc.thePlayer.isSwingInProgress
                        && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                        mc.thePlayer.motionZ *= intaveDoubleVelocity.get()
                        mc.thePlayer.motionX *= intaveDoubleVelocity.get()
                        if(debug.get()) ChatPrint("[Velocity] ${intaveDoubleVelocity.get()}")
                    }
                }
            }
        }
    }
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val player = mc.thePlayer ?: return

        if (mode.get() == "Jump" && hasReceivedVelocity) {
            if (player.isSprinting && player.onGround && player.hurtTime == 9) {
                player.tryJump()
                limitUntilJump = 0
                if(debug.get()) ChatPrint("Jump")
            }
            hasReceivedVelocity = false
            return
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val player = mc.thePlayer ?: return
        when (mode.get()) {
            "Jump" -> {
                var packetDirection = 0.0
                when (packet) {
                    is S12PacketEntityVelocity -> {
                        val motionX = packet.motionX.toDouble()
                        val motionZ = packet.motionZ.toDouble()

                        packetDirection = atan2(motionX, motionZ)
                    }

                    is S27PacketExplosion -> {
                        val motionX = player.motionX + packet.x
                        val motionZ = player.motionZ + packet.y

                        packetDirection = atan2(motionX, motionZ)
                    }
                }
                val degreePlayer = getDirection()
                val degreePacket = Math.floorMod(packetDirection.toDegrees().toInt(), 360).toDouble()
                var angle = abs(degreePacket + degreePlayer)
                val threshold = 120.0
                angle = Math.floorMod(angle.toInt(), 360).toDouble()
                val inRange = angle in 180 - threshold / 2..180 + threshold / 2
                if (inRange)
                    hasReceivedVelocity = true
            }

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
                "PolarFlag"->{
                    if (mc.thePlayer.hurtTime in polarFlagMinHurtTime.get()..polarFlagMaxHurtTime.get()){
                        mc.timer.timerSpeed = polarFlagChangeTimer.get()
                    }else{
                        mc.timer.timerSpeed = polarFlagLowTimer.get()
                    }
                }
                "IntaveDouble" -> {
                    if (mc.thePlayer.hurtTime == 9) {
                        if (++jumped % 2 == 0 && mc.thePlayer.onGround && mc.thePlayer.isSprinting && mc.currentScreen == null) {
                            mc.gameSettings.keyBindJump.pressed = true
                            mc.gameSettings.keyBindForward.pressed = true
                            jumped = 0
                        }
                    } else {
                        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
                        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                    }
                }
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
                    } else if (mc.thePlayer.hurtTime == 9){
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
    fun Float.toRadians() = this * 0.017453292f
    fun Float.toRadiansD() = toRadians().toDouble()
    fun Float.toDegrees() = this * 57.29578f
    fun Float.toDegreesD() = toDegrees().toDouble()

    fun Double.toRadians() = this * 0.017453292
    fun Double.toRadiansF() = toRadians().toFloat()
    fun Double.toDegrees() = this * 57.295779513
    fun Double.toDegreesF() = toDegrees().toFloat()

    private fun getDirection(): Double {
        var moveYaw = mc.thePlayer.rotationYaw
        if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing == 0f) {
            moveYaw += if (mc.thePlayer.moveForward > 0) 0 else 180
        } else if (mc.thePlayer.moveForward != 0f && mc.thePlayer.moveStrafing != 0f) {
            if (mc.thePlayer.moveForward > 0) moveYaw += if (mc.thePlayer.moveStrafing > 0) -45 else 45 else moveYaw -= if (mc.thePlayer.moveStrafing > 0) -45 else 45
            moveYaw += if (mc.thePlayer.moveForward > 0) 0 else 180
        } else if (mc.thePlayer.moveStrafing != 0f && mc.thePlayer.moveForward == 0f) {
            moveYaw += if (mc.thePlayer.moveStrafing > 0) -90 else 90
        }
        return Math.floorMod(moveYaw.toInt(), 360).toDouble()
    }

    private fun EntityPlayerSP.tryJump() {
        if (!mc.gameSettings.keyBindJump.isKeyDown) {
            this.jump()
        }
    }
    override fun onDisable() {
        attack = false
        jump = false
        jumped = 0
        jumped2 = false
    }
}