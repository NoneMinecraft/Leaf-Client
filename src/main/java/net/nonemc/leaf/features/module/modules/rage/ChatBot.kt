package net.nonemc.leaf.features.module.modules.rage

import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.S02PacketChat
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.Util.Chat
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.TextValue

@ModuleInfo(name = "ChatBot", category = ModuleCategory.Rage)
class ChatBot : Module() {
    private val victory = TextValue("Victory", "胜利: ")
    private val teamCT = TextValue("TeamCT", "警察")
    private val teamT = TextValue("TeamT", "罪犯")

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val playerArmor = mc.thePlayer?.inventory?.armorInventory?.get(3) ?: return
        val myItemArmor = playerArmor.item as? ItemArmor
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText
            if (myItemArmor != null) {
                if (message.contains(victory.get() + teamCT.get()) &&
                    (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.IRON || myItemArmor.getColor(playerArmor) == 0x0000FF)
                ) {
                    Chat("@Leaf Client Won the game!")
                }
            }
            if (myItemArmor != null) {
                if (message.contains(victory.get() + teamT.get()) &&
                    (myItemArmor.armorMaterial == ItemArmor.ArmorMaterial.CHAIN || myItemArmor.getColor(playerArmor) == 0xFF0000)
                ) {
                    Chat("@Leaf Client Won the game!")
                }
            }
        }
    }
}