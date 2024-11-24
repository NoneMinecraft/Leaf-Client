package net.ccbluex.liquidbounce.features.module.modules.rage.rage.actions

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

fun fire(){
    if (mc.thePlayer != null) mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(MinecraftInstance.mc.thePlayer.heldItem))
}