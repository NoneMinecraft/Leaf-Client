package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.network.play.client.*
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.PacketUtils
import net.nonemc.leaf.value.IntegerValue
import java.util.*

@ModuleInfo(name = "DelayPackets", category = ModuleCategory.COMBAT)
class DelayPackets : Module() {
    private val packetQueue = LinkedList<Any>()
    private var isProcessing = false
    private var startTime = 0L

    private val delayTime = IntegerValue("DelayTime", 300, 1, 1000)
    private val maxPackets = IntegerValue("MaxPackets", 50, 1, 200)

    override fun onDisable() {
        isProcessing = false
        startTime = 0L
        packetQueue.clear()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet
        when (packet) {
            is C00PacketKeepAlive -> packetQueue.add(packet)
            is C01PacketChatMessage -> packetQueue.add(packet)
            is C02PacketUseEntity -> packetQueue.add(packet)
            is C03PacketPlayer -> packetQueue.add(packet)
            is C07PacketPlayerDigging -> packetQueue.add(packet)
            is C08PacketPlayerBlockPlacement -> packetQueue.add(packet)
            is C09PacketHeldItemChange -> packetQueue.add(packet)
            is C10PacketCreativeInventoryAction -> packetQueue.add(packet)
            is C11PacketEnchantItem -> packetQueue.add(packet)
            is C12PacketUpdateSign -> packetQueue.add(packet)
            is C13PacketPlayerAbilities -> packetQueue.add(packet)
            is C0APacketAnimation -> packetQueue.add(packet)
            is C0BPacketEntityAction -> packetQueue.add(packet)
            is C0CPacketInput -> packetQueue.add(packet)
            is C0DPacketCloseWindow -> packetQueue.add(packet)
            is C0EPacketClickWindow -> packetQueue.add(packet)
            is C0FPacketConfirmTransaction -> packetQueue.add(packet)
        }
        event.cancelEvent()

        if (!isProcessing) {
            startTime = System.currentTimeMillis()
            isProcessing = true
        }
    }

    @EventTarget
    fun onTick(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        if (isProcessing) {
            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime >= delayTime.get()) {
                if (packetQueue.isNotEmpty()) {
                    val packet = packetQueue.removeFirst()
                    when (packet) {
                        is C00PacketKeepAlive -> PacketUtils.sendPacketNoEvent(packet)
                        is C01PacketChatMessage -> PacketUtils.sendPacketNoEvent(packet)
                        is C02PacketUseEntity -> PacketUtils.sendPacketNoEvent(packet)
                        is C03PacketPlayer -> PacketUtils.sendPacketNoEvent(packet)
                        is C07PacketPlayerDigging -> PacketUtils.sendPacketNoEvent(packet)
                        is C08PacketPlayerBlockPlacement -> PacketUtils.sendPacketNoEvent(packet)
                        is C09PacketHeldItemChange -> PacketUtils.sendPacketNoEvent(packet)
                        is C10PacketCreativeInventoryAction -> PacketUtils.sendPacketNoEvent(packet)
                        is C11PacketEnchantItem -> PacketUtils.sendPacketNoEvent(packet)
                        is C12PacketUpdateSign -> PacketUtils.sendPacketNoEvent(packet)
                        is C13PacketPlayerAbilities -> PacketUtils.sendPacketNoEvent(packet)
                        is C0APacketAnimation -> PacketUtils.sendPacketNoEvent(packet)
                        is C0BPacketEntityAction -> PacketUtils.sendPacketNoEvent(packet)
                        is C0CPacketInput -> PacketUtils.sendPacketNoEvent(packet)
                        is C0DPacketCloseWindow -> PacketUtils.sendPacketNoEvent(packet)
                        is C0EPacketClickWindow -> PacketUtils.sendPacketNoEvent(packet)
                        is C0FPacketConfirmTransaction -> PacketUtils.sendPacketNoEvent(packet)
                    }
                }
                isProcessing = false
            }

            if (packetQueue.size > maxPackets.get()) {
                packetQueue.removeFirst()
            }
        }
    }
}
