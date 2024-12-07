package net.ccbluex.liquidbounce.features.module.modules.rage.rage.actions

import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

fun fire(){
    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
}