package net.nonemc.leaf.features.module.modules.player

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "DelayBound", category = ModuleCategory.PLAYER)
class DelayBound : Module() {
    private val inboundValue = BoolValue("Inbound", false)
    private val pulseValue = BoolValue("Pulse", false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 1000, 500, 5000).displayable { pulseValue.get() }
    private val pulseTimer = MSTimer()
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private var disableLogger = false
    private val positions = LinkedList<DoubleArray>()
    override fun onEnable() {
        if (mc.thePlayer == null) return
        pulseTimer.reset()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        run()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer) event.cancelEvent()
        if (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C0EPacketClickWindow ||
            packet is C0FPacketConfirmTransaction || packet is C00PacketKeepAlive || packet is C0DPacketCloseWindow ||
            packet is C11PacketEnchantItem || packet is C10PacketCreativeInventoryAction || packet is C09PacketHeldItemChange || packet is C12PacketUpdateSign ||
            packet is C13PacketPlayerAbilities || packet is C14PacketTabComplete || packet is C15PacketClientSettings || packet is C16PacketClientStatus || packet is C17PacketCustomPayload
            || packet is C18PacketSpectate || packet is C19PacketResourcePackStatus || packet is C0CPacketInput
        ) {
            event.cancelEvent()
            packets.add(packet as Packet<INetHandlerPlayServer>)
        }
        if (packet is S08PacketPlayerPosLook && inboundValue.get()) event.cancelEvent()

    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            run()
            pulseTimer.reset()
        }
    }

    private fun run() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) mc.netHandler.addToSendQueue(packets.take())
            disableLogger = false
        } finally {
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }
}
