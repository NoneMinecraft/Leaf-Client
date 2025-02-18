package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.Chat
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat

@ModuleInfo(name = "ChatBot", category = ModuleCategory.Rage)
object ChatBot : Module() {
    private val victory = TextValue("Victory","胜利: ")
    private val teamCT = TextValue("TeamCT","警察")
    private val teamT = TextValue("TeamT","罪犯")
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val playerArmor = mc.thePlayer?.inventory?.armorInventory?.get(3) ?:return
        val myItemArmor = playerArmor.item as? ItemArmor
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
                if (myItemArmor != null) {
                    if (message.contains(victory.get() + teamCT.get()) &&
                        (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON || myItemArmor.getColor(playerArmor) == 0x0000FF)) {
                        Chat("@Leaf Client Won the game!")
                    }
                }
            if (myItemArmor != null) {
                if (message.contains(victory.get()+teamT.get())&&
                    (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN || myItemArmor.getColor(playerArmor) == 0xFF0000)) {
                    Chat("@Leaf Client Won the game!")
                }
            }
        }
    }
}