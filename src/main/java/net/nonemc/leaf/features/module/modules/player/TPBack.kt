package net.nonemc.leaf.features.module.modules.player

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.event.WorldEvent
import net.nonemc.leaf.features.Util.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import org.lwjgl.input.Keyboard
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "TPBack", category = ModuleCategory.PLAYER)
class TPBack : Module() {
    //Old KKCraft Intave Bypass
    private val delay = IntegerValue("Delay", 500, 0, 1000)
    private val badPacketValue = FloatValue("BadPacketHeight", 10F, 0F, 100F)
    private val shortcutKey = Keyboard.KEY_U
    private val shortcutKey2 = Keyboard.KEY_I
    private var tags = false
    private val timer = MSTimer()
    private val reduceTimer = MSTimer()
    private var flagsTime = 0
    private var stuck = false
    var flag = 0
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private var disableLogger = false
    private var isDelay = false
    private var act = false
    private var act2 = false
    private val time = MSTimer()
    private val sleepTime = MSTimer()
    override fun onDisable() {
        if (isDelay)
            act2 = false
        isDelay = false
        act = false
        time.reset()
        sleepTime.reset()
    }

    override fun onEnable() {
        time.reset()
        tags = false
        flag = 0
        reset()
        runLag()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (Keyboard.isKeyDown(shortcutKey2) && mc.currentScreen == null) {
            isDelay = true
        }
        if (Keyboard.isKeyDown(shortcutKey) && mc.currentScreen == null) {
            runLag()
        }
        if (stuck) {
            if (timer.hasTimePassed(1500)) {
                stuck = false
                flagsTime = 0
                timer.reset()
                reduceTimer.reset()
            }
        } else {
            if (flagsTime > 0) {
                flag++
                timer.reset()
                reduceTimer.reset()
                flagsTime = 0
                stuck = true
                tags = false
                if (sleepTime.hasTimePassed(100)) ChatPrint("§0[§8TP§0] §6发生一次意外标记:按下KEY_U重新标记,否则无法执行返回")
            }
            if (timer.hasTimePassed(1500) && reduceTimer.hasTimePassed(500) && flagsTime > 0) {
                flagsTime -= 1
                reduceTimer.reset()
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (isDelay) {
            if (!act) {
                blink()
                act = true
                mc.thePlayer!!.setPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - badPacketValue.get(),
                    mc.thePlayer.posZ
                )

                time.reset()
            } else {
                if (!act2) {
                    if (time.hasTimePassed(delay.get().toLong())) {
                        blink()
                        act2 = false
                        act = false
                        isDelay = false
                        time.reset()
                    }
                }
            }
            runDelayPacket(event)
            sleepTime.reset()
        }
        if (packet is S08PacketPlayerPosLook) {
            flagsTime++
            reduceTimer.reset()
            if (!stuck) {
                timer.reset()
            }
        }
    }

    private fun reset() {
        stuck = false
        flagsTime = 0
        timer.reset()
        reduceTimer.reset()
    }

    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.addToSendQueue(packets.take())
            }
            disableLogger = false
        } finally {
            disableLogger = false
        }
    }

    private fun runDelayPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer) { // Cancel all movement stuff
            event.cancelEvent()
        }
        if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity
        ) {
            event.cancelEvent()
            packets.add(packet as Packet<INetHandlerPlayServer>)
        }
    }

    private fun runLag() {
        mc?.thePlayer!!.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
        ChatPrint("§0[§8TP§0] §6标记了一处地点[§8${mc.thePlayer.posX}, ${mc.thePlayer.posY - 1},${mc.thePlayer.posZ}§6]\n§6按下KEY_U重新标记,按下KEY_I返回该位置")
        tags = true
    }
}
