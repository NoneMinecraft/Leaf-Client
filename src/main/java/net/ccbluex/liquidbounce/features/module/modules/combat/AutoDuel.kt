package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Text
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S02PacketChat
import kotlin.random.Random

@ModuleInfo(name = "AutoDuel", category = ModuleCategory.COMBAT)
object AutoDuel : Module() {
    private val Tick = IntegerValue("Tick",100,0,2000)
    private val Tick2 = IntegerValue("Tick2",1,0,2000)
    private val target = TextValue("target","targetName")
    private val acceptTarget = TextValue("AcceptTarget","AcceptTarget")
    private val leaveTarget = TextValue("leaveTarget","leaveTarget")
    var tick = 0
    var tick2 =0
    override fun onDisable() {
        tick = 0
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            if (message.contains("in 5")&&mc.thePlayer.name == leaveTarget.get()) {
                mc.theWorld.sendQuittingDisconnectingPacket()
            }
        }
    }
    @EventTarget
    fun UpdateEvent(event: UpdateEvent) {
        val screen = mc.currentScreen

        if (tick2 < Tick2.get()){
            tick2++
        }else{
            mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage("/duel ${target.get()}"))
            mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage("/duel accept ${acceptTarget.get()}"))
            tick2 = 0
        }
        if (screen !is GuiChest) {
            return
        }
        val items = mutableListOf<Slot>()
        for (slotIndex in 0 until screen.inventoryRows * 9) {
            val slot = screen.inventorySlots.inventorySlots[slotIndex]
            if (slot.stack != null &&(slot.stack.item == Items.iron_sword || (slot.stack.item == Item.getItemFromBlock(Blocks.stained_glass_pane) && slot.stack.metadata == 5))) {
                items.add(slot) } }
        val randomSlot = Random.nextInt(items.size)
        val slot = items[randomSlot]
        if (tick < 1){
            tick++
            move(screen, slot)
            MainLib.ChatPrint("n")
        }else if (tick < Tick.get()){
            tick ++
        }else{
            tick=0
        }
    }
    private fun move(screen: GuiChest, slot: Slot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1)
    }
}