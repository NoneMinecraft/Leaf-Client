package net.ccbluex.liquidbounce.features.module.modules.rage.rage.actions

import net.ccbluex.liquidbounce.features.module.modules.rage.rage.WeaponType
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.search.hasWeapon
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.network.play.client.C09PacketHeldItemChange

fun switch(){
    mc.thePlayer.inventory.currentItem = (if (hasWeapon(WeaponType.RIFLE)) 0 else 1)
}
fun packetSwitch(){
    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(if (hasWeapon(WeaponType.RIFLE)) 0 else 1))
}
fun back(n:Int) {
    mc.thePlayer.inventory.currentItem = n
}
fun backPacket(n:Int) {
    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(n))
}
