package net.nonemc.leaf.features.module.modules.player

import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "FastUse", category = ModuleCategory.PLAYER)
class FastUse : Module() {
    private val modeValue =
        ListValue("Mode", arrayOf("NCP", "Instant", "CustomDelay", "DelayedInstant", "Intave"), "Intave")
    private val durationValue =
        IntegerValue("InstantDelay", 14, 0, 35).displayable { modeValue.equals("DelayedInstant") }
    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300).displayable { modeValue.equals("CustomDelay") }
    private val LowTimer = FloatValue("LowTimer", 0.3F, 0.01F, 10F).displayable { modeValue.equals("Intave") }
    private val MaxTimer = FloatValue("MaxTimer", 0.3F, 0.01F, 10F).displayable { modeValue.equals("Intave") }
    private val Ticks = FloatValue("Ticks", 1F, 1F, 20F).displayable { modeValue.equals("Intave") }
    private var iseating = 10
    var reset = false
    private val msTimer = MSTimer()
    private var usedTimer = false


    private fun send(int: Int) {
        repeat(int) {
            mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
        }
    }

    private fun send() {
        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get()) {
            "Intave" -> {
                if (mc.thePlayer.isEating && (mc.thePlayer.heldItem.item is ItemFood || mc.thePlayer.heldItem.item is ItemPotion)) {
                    reset = false
                    if (iseating >= 1) {
                        iseating--
                        mc.timer.timerSpeed = LowTimer.get()

                    } else {
                        iseating = 10
                        mc.timer.timerSpeed = MaxTimer.get()
                    }
                } else {
                    iseating = Ticks.get().toInt()
                    if (!reset) {
                        mc.timer.timerSpeed = 1F
                        reset = true
                    }
                }
            }
        }

        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }

        if (!mc.thePlayer.isUsingItem) {
            return
        }

        val usingItem = mc.thePlayer.itemInUse.item

        if (usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion) {
            when (modeValue.get().lowercase()) {
                "delayedinstant" -> if (mc.thePlayer.itemInUseDuration > durationValue.get()) {
                    send(36 - mc.thePlayer.itemInUseDuration)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "ncp" -> if (mc.thePlayer.itemInUseDuration > 14) {
                    send(20)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "instant" -> {
                    send(35)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "customdelay" -> {
                    if (!msTimer.hasTimePassed(delayValue.get().toLong())) {
                        return
                    }

                    send()
                    msTimer.reset()
                }
            }
        }
    }

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
            iseating = Ticks.get().toInt()
            mc.timer.timerSpeed = 1F
            reset = false

        }
    }

    override val tag: String
        get() = modeValue.get()
}
