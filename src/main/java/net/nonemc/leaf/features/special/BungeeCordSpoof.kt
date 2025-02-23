

package net.nonemc.leaf.features.special

import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.utils.MinecraftInstance
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.handshake.client.C00Handshake
import java.util.*

class BungeeCordSpoof : MinecraftInstance(), Listenable {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C00Handshake && enabled && packet.requestedState == EnumConnectionState.LOGIN)
            packet.ip = "${packet.ip}\u0000${getRandomIpPart()}.${getRandomIpPart()}.${getRandomIpPart()}.${getRandomIpPart()}\u0000${UUIDSpoofer.getUUID()}"
    }

    private fun getRandomIpPart(): String = RANDOM.nextInt(256).toString()

    override fun handleEvents(): Boolean = true

    companion object {
        private val RANDOM = Random()

        @JvmField
        var enabled = false
    }
}