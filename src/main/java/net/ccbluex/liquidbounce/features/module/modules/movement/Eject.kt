package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "Eject", category = ModuleCategory.MOVEMENT)
class Eject : Module() {
    private val die = TextValue("Die","Die")
    private var reborn = TextValue("Reborn","Reborn")
    private val motionX = FloatValue("motionX",1F,0F,10F)
    private val motionY = FloatValue("motionY",1F,0F,10F)
    private val motionZ = FloatValue("motionZ",0F,0F,10F)
    private val velX = FloatValue("velX",1F,0F,10F)
    private val velY = FloatValue("velY",1F,0F,10F)
    private val velZ = FloatValue("velZ",0F,0F,10F)
    private val timer = FloatValue("Timer",0.3F,0F,1F)
    private val tick = FloatValue("Tick",1F,0F,10F)
    private val motionTick = FloatValue("MotionTick",1F,0F,10F)
    private val velTick = FloatValue("VelocityTick",1F,0F,10F)
    private val c03 = BoolValue("NoC03",false)
    private val c0f = BoolValue("NoC0F",false)
    var isS45 = false
    var isReborn = false
    var ticks = 0
    var vt = 0
    var mt = 0
    override fun onDisable() {
        vt = 0
        mt = 0
        isS45 = false
        isReborn = false
        ticks = 0
        mc.timer.timerSpeed = 1F
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (isS45) {
            mc.timer.timerSpeed = timer.get()
            if (vt < velTick.get()) {
                vt++

            } else {
                vt = 0
                val velocityPacket = S12PacketEntityVelocity(
                    mc.thePlayer.entityId,
                    velX.get().toDouble(),
                    velY.get().toDouble(),
                    velZ.get().toDouble()
                )
                mc.netHandler.addToSendQueue(velocityPacket)
            }
            if (mt < motionTick.get()) {
                mt++

            } else {
                mt = 0
                mc.thePlayer.motionX = motionX.get().toDouble()
                mc.thePlayer.motionY = motionY.get().toDouble()
                mc.thePlayer.motionZ = motionZ.get().toDouble()
            }
        }
        if (isReborn){
            if (ticks < tick.get()){
                ticks++
            }else{
                isS45 = false
                isReborn = false
                ticks = 0
                mc.timer.timerSpeed = 1F
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && c03.get()){
            event.cancelEvent()
        }
        if (event.packet is C0FPacketConfirmTransaction && c0f.get()){
            event.cancelEvent()
        }
        if (event.packet is S45PacketTitle){
            val titlePacket = event.packet
            val message = titlePacket.message.unformattedText
            if (message.contains(die.get(), ignoreCase = true)) {
                isS45 = true
            }
            if (message.contains(reborn.get(), ignoreCase = true) && isS45) {
                isReborn = true
            }
        }
    }
}