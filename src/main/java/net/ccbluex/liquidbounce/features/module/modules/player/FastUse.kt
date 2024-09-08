
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "FastUse", category = ModuleCategory.PLAYER)
class FastUse : Module() {

    private val modeValue = ListValue("Mode", arrayOf("NCP","Instant", "Timer", "CustomDelay", "DelayedInstant", "MinemoraTest", "AAC", "NewAAC","Medusa","Matrix","Fast","Intave"), "DelayedInstant")
    private val timerValue = FloatValue("Timer", 1.22F, 0.1F, 2.0F).displayable { modeValue.equals("Timer") }
    private val durationValue = IntegerValue("InstantDelay", 14, 0, 35).displayable { modeValue.equals("DelayedInstant") }
    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300).displayable { modeValue.equals("CustomDelay") }
    private val LowTimer = FloatValue("LowTimer", 0.3F, 0.01F, 10F)
    private val MaxTimer = FloatValue("MaxTimer", 0.3F, 0.01F, 10F)
    private val Ticks = FloatValue("Ticks", 1F, 1F, 20F)
    var iseating = 10
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
            "Intave"->{
                if(mc.thePlayer.isEating && (mc.thePlayer.heldItem.item is ItemFood || mc.thePlayer.heldItem.item is ItemPotion)){
                    reset = false
                    if (iseating>=1){
                        iseating--
                        mc.timer.timerSpeed = LowTimer.get()

                    }else{
                        iseating=10
                        mc.timer.timerSpeed = MaxTimer.get()
                    }
                }else{
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

                "matrix" -> {
                    mc.timer.timerSpeed = 0.5f
                    usedTimer = true
                    send()
                }
                "fast" -> {
                    if (mc.thePlayer.itemInUseDuration < 25) {
                        mc.timer.timerSpeed = 0.3f
                        usedTimer = true
                        send(5)
                    }
                }
                
                "medusa" -> {
                    if (mc.thePlayer.itemInUseDuration > 5 || !msTimer.hasTimePassed(360L))
                        return

                    send(20)

                    msTimer.reset()
                }
                "delayedinstant" -> if (mc.thePlayer.itemInUseDuration > durationValue.get()) {
                    send(36 - mc.thePlayer.itemInUseDuration)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }

                "ncp" -> if (mc.thePlayer.itemInUseDuration > 14) {
                    send(20)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)}

                "instant" -> {
                    send(35)

                    mc.playerController.onStoppedUsingItem(mc.thePlayer)
                }
                "aac" -> {
                    mc.timer.timerSpeed = 0.49F
                    usedTimer = true
                    if (mc.thePlayer.itemInUseDuration > 14) {
                        send(23)
                    }
                }
                "newaac" -> {
                    mc.timer.timerSpeed = 0.49F
                    usedTimer = true
                    send(2)
                }
                "timer" -> {
                    mc.timer.timerSpeed = timerValue.get()
                    usedTimer = true
                }

                "minemoratest" -> {
                    mc.timer.timerSpeed = 0.5F
                    usedTimer = true
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        send(2)
                    }
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

    // @EventTarget
    // fun onMove(event: MoveEvent?) {
    //     if (event == null) return

    //     if (!mc.thePlayer.isUsingItem || !modeValue.get().lowercase()=="aac") return
    //     val usingItem1 = mc.thePlayer.itemInUse.item
    //     if ((usingItem1 is ItemFood || usingItem1 is ItemBucketMilk || usingItem1 is ItemPotion))
    //         event.zero()
    // }

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
