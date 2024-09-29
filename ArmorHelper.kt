package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C0EPacketClickWindow
import java.awt.Robot
import java.awt.event.InputEvent

@ModuleInfo(name = "ArmorHelper", category = ModuleCategory.COMBAT)
class ArmorHelper : Module() {
    private val Mode = ListValue("Mode", arrayOf("C0E","Robot"),"C0E")
    private val Tick = IntegerValue("Tick",1,1,100)
    private val RobotX = IntegerValue("RobotX",500,-2000,2000)
    private val RobotY = IntegerValue("RobotY",100,-800,800)
    private val RobotDelay = IntegerValue("RobotDelay",50,0,100)
    val robot = Robot()
    val helmetSlotIndex = 5
    var tick = 0

    override fun onDisable() {
        tick = 0
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (tick < Tick.get()){
            tick ++
            if (Mode.get() == "Robot" && mc.currentScreen != null) {
                robot.mouseMove(RobotX.get(), RobotY.get())
            }
        }else{
            if (Mode.get() == "C0E"){
            mc.netHandler.addToSendQueue(
                C0EPacketClickWindow(
                mc.thePlayer.openContainer.windowId,
                helmetSlotIndex,
                0,
                0,
                mc.thePlayer.inventory.getStackInSlot(helmetSlotIndex),
                mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)
            )
            )
            tick = 0
        }else if (mc.currentScreen != null){
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                Thread.sleep(RobotDelay.get().toLong())
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                tick = 0
        }
        }
    }override val tag: String
        get() = Mode.get()
}