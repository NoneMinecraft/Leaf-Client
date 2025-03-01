package net.nonemc.leaf.features.module.modules.rage.rage.handle

import net.nonemc.leaf.utils.mc
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement

fun fire(){
    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
}