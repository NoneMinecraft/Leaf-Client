//All the code was written by N0ne.
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S12PacketEntityVelocity
import java.util.*

@ModuleInfo(name = "DelayPackets", category = ModuleCategory.COMBAT)
class DelayPackets : Module() {
    private val packetC02List = LinkedList<C02PacketUseEntity>()
    private val packetC03List = LinkedList<C03PacketPlayer>()
    private val packetC07List = LinkedList<C07PacketPlayerDigging>()
    private val packetC08List = LinkedList<C08PacketPlayerBlockPlacement>()
    private val packetC09List = LinkedList<C09PacketHeldItemChange>()
    private val packetC0BList = LinkedList<C0BPacketEntityAction>()
    private val packetS12List = LinkedList<S12PacketEntityVelocity>()
    private var isProcessingC02 = false
    private var startTimeC02 = 0L
    private var isProcessingC03 = false
    private var startTimeC03 = 0L
    private var isProcessingC07 = false
    private var startTimeC07 = 0L
    private var isProcessingC08 = false
    private var startTimeC08 = 0L
    private var isProcessingC09 = false
    private var startTimeC09 = 0L
    private var isProcessingC0B = false
    private var startTimeC0B = 0L
    private var isProcessingS12 = false
    private var startTimeS12 = 0L
    override fun onDisable() {
        isProcessingC02 = false
        startTimeC02 = 0L
        isProcessingC03 = false
        startTimeC03 = 0L
        isProcessingC07 = false
        startTimeC07 = 0L
        isProcessingC08 = false
        startTimeC08 = 0L
        isProcessingC09 = false
        startTimeC09 = 0L
        isProcessingC0B = false
        startTimeC0B = 0L
        isProcessingS12 = false
        startTimeS12 = 0L

        packetC02List.clear()
        packetC03List.clear()
        packetC07List.clear()
        packetC08List.clear()
        packetC09List.clear()
        packetC0BList.clear()
        packetS12List.clear()
    }

    private val delayTimeC02 = IntegerValue("C02 DelayTime", 300, 1, 1000)
    private val maxPacketsC02 = IntegerValue("C02 MaxPackets", 50, 1, 200)

    private val delayTimeC03 = IntegerValue("C03 DelayTime", 300, 1, 1000)
    private val maxPacketsC03 = IntegerValue("C03 MaxPackets", 50, 1, 200)

    private val delayTimeC07 = IntegerValue("C07 DelayTime", 300, 1, 1000)
    private val maxPacketsC07 = IntegerValue("C07 MaxPackets", 50, 1, 200)

    private val delayTimeC08 = IntegerValue("C08 DelayTime", 300, 1, 1000)
    private val maxPacketsC08 = IntegerValue("C08 MaxPackets", 50, 1, 200)

    private val delayTimeC09 = IntegerValue("C09 DelayTime", 300, 1, 1000)
    private val maxPacketsC09 = IntegerValue("C09 MaxPackets", 50, 1, 200)

    private val delayTimeC0B = IntegerValue("C0B DelayTime", 300, 1, 1000)
    private val maxPacketsC0B = IntegerValue("C0B MaxPackets", 50, 1, 200)

    private val delayTimeS12 = IntegerValue("S12 DelayTime", 300, 1, 1000)
    private val maxPacketsS12 = IntegerValue("S12 MaxPackets", 50, 1, 200)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        when (packet) {
            is C02PacketUseEntity -> {
                if (!isProcessingC02) {
                    startTimeC02 = System.currentTimeMillis()
                    isProcessingC02 = true
                }
                packetC02List.add(packet)
                event.cancelEvent()
            }
            is C03PacketPlayer -> {
                if (!isProcessingC03) {
                    startTimeC03 = System.currentTimeMillis()
                    isProcessingC03 = true
                }
                packetC03List.add(packet)
                event.cancelEvent()
            }
            is C07PacketPlayerDigging -> {
                if (!isProcessingC07) {
                    startTimeC07 = System.currentTimeMillis()
                    isProcessingC07 = true
                }
                packetC07List.add(packet)
                event.cancelEvent()
            }
            is C08PacketPlayerBlockPlacement -> {
                if (!isProcessingC08) {
                    startTimeC08 = System.currentTimeMillis()
                    isProcessingC08 = true
                }
                packetC08List.add(packet)
                event.cancelEvent()
            }
            is C09PacketHeldItemChange -> {

                if (!isProcessingC09) {
                    startTimeC09 = System.currentTimeMillis()
                    isProcessingC09 = true
                }
                packetC09List.add(packet)
                event.cancelEvent()
            }
            is C0BPacketEntityAction -> {

                if (!isProcessingC0B) {
                    startTimeC0B = System.currentTimeMillis()
                    isProcessingC0B = true
                }
                packetC0BList.add(packet)
                event.cancelEvent()
            }
            is S12PacketEntityVelocity -> {

                if (!isProcessingS12) {
                    startTimeS12 = System.currentTimeMillis()
                    isProcessingS12 = true
                }
                packetS12List.add(packet)
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onTick(event: UpdateEvent) {
        // C02 延迟包处理
        if (isProcessingC02) {
            val elapsedTime = System.currentTimeMillis() - startTimeC02
            if (elapsedTime >= delayTimeC02.get()) {
                if (packetC02List.isNotEmpty()) {
                    val packet = packetC02List.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                    isProcessingC02 = false
                }
            }
            if (packetC02List.size > maxPacketsC02.get()) {
                packetC02List.removeFirst()
            }

        }

        // C03 延迟包处理
        if (isProcessingC03) {
            val elapsedTime = System.currentTimeMillis() - startTimeC03
            if (elapsedTime >= delayTimeC03.get()) {
                if (packetC03List.isNotEmpty()) {
                    val packet = packetC03List.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                }
            }
            if (packetC03List.size > maxPacketsC03.get()) {
                packetC03List.removeFirst()
            }
        }

        // C07 延迟包处理
        if (isProcessingC07) {
            val elapsedTime = System.currentTimeMillis() - startTimeC07
            if (elapsedTime >= delayTimeC07.get()) {
                if (packetC07List.isNotEmpty()) {
                    val packet = packetC07List.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                    isProcessingC07 = false
                }
            }
            if (packetC07List.size > maxPacketsC07.get()) {
                packetC07List.removeFirst()
            }
        }

        // C08 延迟包处理
        if (isProcessingC08) {
            val elapsedTime = System.currentTimeMillis() - startTimeC08
            if (elapsedTime >= delayTimeC08.get()) {
                if (packetC08List.isNotEmpty()) {
                    val packet = packetC08List.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                    isProcessingC08 = false
                }
            }
            if (packetC08List.size > maxPacketsC08.get()) {
                packetC08List.removeFirst()
            }
        }

        // C09 延迟包处理
        if (isProcessingC09) {
            val elapsedTime = System.currentTimeMillis() - startTimeC09
            if (elapsedTime >= delayTimeC09.get()) {
                if (packetC09List.isNotEmpty()) {
                    val packet = packetC09List.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                    isProcessingC09 = false
                }
            }
            if (packetC09List.size > maxPacketsC09.get()) {
                packetC09List.removeFirst()
            }
        }
        // C0B 延迟包处理
        if (isProcessingC0B) {
            val elapsedTime = System.currentTimeMillis() - startTimeC0B
            if (elapsedTime >= delayTimeC0B.get()) {
                if (packetC0BList.isNotEmpty()) {
                    val packet = packetC0BList.removeFirst()
                    PacketUtils.sendPacketNoEvent(packet)
                    isProcessingC0B = false
                }
            }
            if (packetC0BList.size > maxPacketsC0B.get()) {
                packetC0BList.removeFirst()
            }
        }

        // S12 延迟包处理
        if (isProcessingS12) {
            val elapsedTime = System.currentTimeMillis() - startTimeS12
            if (elapsedTime >= delayTimeS12.get()) {
                if (packetS12List.isNotEmpty()) {
                    val packet = packetS12List.removeFirst()
                    PacketUtils.handlePacket(packet)
                    isProcessingS12 = false
                }
            }
            if (packetS12List.size > maxPacketsS12.get()) {
                packetS12List.removeFirst()
            }
        }
    }
}
