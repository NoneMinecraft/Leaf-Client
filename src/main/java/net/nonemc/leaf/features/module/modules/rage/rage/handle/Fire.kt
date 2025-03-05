package net.nonemc.leaf.features.module.modules.rage.rage.handle

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.nonemc.leaf.utils.mc

fun fire() {
    mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
}