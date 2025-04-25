package net.nonemc.leaf.features.module.modules.rage.rage.handle

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.nonemc.leaf.libs.base.mc

fun fire() {
    net.nonemc.leaf.libs.base.mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(net.nonemc.leaf.libs.base.mc.thePlayer.heldItem))
}