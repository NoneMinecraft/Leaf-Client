package net.nonemc.leaf.features.special

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.utils.MinecraftInstance

object AntiForge : MinecraftInstance(), Listenable {
    var enabled = true
    var blockFML = true
    var blockProxyPacket = true
    var blockPayloadPackets = true

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (enabled && !mc.isIntegratedServerRunning) {
            try {
                if (blockProxyPacket && packet.javaClass.name == "net.minecraftforge.fml.common.network.internal.FMLProxyPacket") {
                    event.cancelEvent()
                }

                if (blockPayloadPackets && packet is C17PacketCustomPayload) {
                    if (!packet.channelName.startsWith("MC|")) {
                        event.cancelEvent()
                    } else if (packet.channelName.equals("MC|Brand", true)) {
                        packet.data = PacketBuffer(Unpooled.buffer()).writeString("vanilla")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun handleEvents() = true
}