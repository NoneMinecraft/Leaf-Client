package net.nonemc.leaf.libs.packet

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.TickEvent
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.libs.timer.MSTimer

object PacketLib : MinecraftInstance() {
    private val packets = ArrayList<Packet<INetHandlerPlayServer>>()
    private var inBound = 0
    private var outBound = 0
    var avgInBound = 0
    var avgOutBound = 0
    private val packetTimer = MSTimer()
    fun handleSendPacket(packet: Packet<*>): Boolean {
        if (packets.contains(packet)) {
            packets.remove(packet)
            return true
        }
        return false
    }
    fun sendPacketNoEvent(packet: Packet<INetHandlerPlayServer>) {
        packets.add(packet)
        mc.netHandler.addToSendQueue(packet)
    }
    fun getPacketType(packet: Packet<*>): PacketType {
        val className = packet.javaClass.simpleName
        if (className.startsWith("C", ignoreCase = true)) {
            return PacketType.CLIENTSIDE
        } else if (className.startsWith("S", ignoreCase = true)) {
            return PacketType.SERVERSIDE
        }
        return PacketType.UNKNOWN
    }
    enum class PacketType {
        SERVERSIDE,
        CLIENTSIDE,
        UNKNOWN
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.isServerSide()) {
            inBound++
        } else {
            outBound++
        }
    }
    @EventTarget
    fun onTick(event: TickEvent) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound
            avgOutBound = outBound
            outBound = 0
            inBound = 0
            packetTimer.reset()
        }
    }
}