package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "AirStuck", category = ModuleCategory.COMBAT)
class AirStuck : Module() {
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    var isStuck = false
    override fun onDisable() {
        isStuck = false
        mc.thePlayer.motionX = motionX
        mc.thePlayer.motionY = motionY
        mc.thePlayer.motionZ = motionZ
        mc.thePlayer.setPositionAndRotation(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (checkVoid()){
            if (!isStuck){
                if (mc.thePlayer == null) {
                    return
                }

                x = mc.thePlayer.posX
                y = mc.thePlayer.posY
                z = mc.thePlayer.posZ
                motionX = mc.thePlayer.motionX
                motionY = mc.thePlayer.motionY
                motionZ = mc.thePlayer.motionZ
                isStuck = true
            }
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.setPositionAndRotation(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        }else{
            isStuck = false
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (checkVoid()){
        if (event.packet is C03PacketPlayer) {
            event.cancelEvent()
        }
        if (event.packet is S08PacketPlayerPosLook) {
            x = event.packet.x
            y = event.packet.y
            z = event.packet.z
            motionX = 0.0
            motionY = 0.0
            motionZ = 0.0
        }
            }
    }

    private fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer.posY-1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(mc.thePlayer.motionX * 0.5, i.toDouble(), mc.thePlayer.motionZ * 0.5)).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }
}