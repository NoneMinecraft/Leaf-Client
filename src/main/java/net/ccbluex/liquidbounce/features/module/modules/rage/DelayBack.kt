package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

@ModuleInfo(name = "DelayBack", category = ModuleCategory.Rage)
class DelayBack : Module() {
    private val maxDelay = FloatValue("MaxDelay",1000F,1F,8000F)
    private val backTick = IntegerValue("BackTick",1,0,30)
    private val c08 = BoolValue("C08", true)
    private val heldItem = BoolValue("HeldItem", true)
    private val dbX = FloatValue("DBX", 0f, -200f, 200f)
    private val dbZ = FloatValue("DBZ", 0f, -200f, 200f)
    private var playerX = 0.0
    private var playerY = 0.0
    private var playerZ = 0.0
    private var back = false
    private val delay = MSTimer()
    private var backDelay = 0
    private var fakePlayer: EntityOtherPlayerMP? = null
    override fun onEnable() {
        backDelay = 0
        delay.reset()
        back = false
        playerX = mc.thePlayer.posX
        playerY = mc.thePlayer.posY
        playerZ = mc.thePlayer.posZ
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.setPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        mc.theWorld.addEntityToWorld(-1337, fakePlayer)
    }
    override fun onDisable() {
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if ((!c08.get() || packet is C08PacketPlayerBlockPlacement) && (!heldItem.get() || mc.thePlayer.heldItem != null) && !back) {
            if (backTick.get() > backDelay) backDelay++ else {
                mc.thePlayer.setPosition(playerX, playerY, playerZ)
                back = true
                backDelay = 0
                if (fakePlayer != null) {
                    mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
                    fakePlayer = null
                }
            }
        }else if (delay.hasTimePassed(maxDelay.get().toLong())) {
            backDelay = 0
                playerX = mc.thePlayer.posX
                playerY = mc.thePlayer.posY
                playerZ = mc.thePlayer.posZ
                back = false
                delay.reset()
            fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
            fakePlayer!!.clonePlayer(mc.thePlayer, true)
            fakePlayer!!.setPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ)
            fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
            mc.theWorld.addEntityToWorld(-1337, fakePlayer)
            }

    }
    @EventTarget
    fun onRender(event: Render2DEvent) {
        MainLib.drawText("DB:$backDelay",dbX.get().toInt(),dbZ.get().toInt(),80,80,255, Fonts.SFUI35)
    }
}