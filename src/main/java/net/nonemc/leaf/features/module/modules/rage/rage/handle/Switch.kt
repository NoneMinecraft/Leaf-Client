package net.nonemc.leaf.features.module.modules.rage.rage.handle

import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.nonemc.leaf.features.module.modules.rage.rage.WeaponType
import net.nonemc.leaf.features.module.modules.rage.rage.search.hasWeapon
import net.nonemc.leaf.libs.base.mc

fun switch() {
    net.nonemc.leaf.libs.base.mc.thePlayer.inventory.currentItem = (if (hasWeapon(WeaponType.RIFLE)) 0 else 1)
}

fun packetSwitch() {
    net.nonemc.leaf.libs.base.mc.netHandler.addToSendQueue(C09PacketHeldItemChange(if (hasWeapon(WeaponType.RIFLE)) 0 else 1))
}

fun back(n: Int) {
    net.nonemc.leaf.libs.base.mc.thePlayer.inventory.currentItem = n
}

fun backPacket(n: Int) {
    net.nonemc.leaf.libs.base.mc.netHandler.addToSendQueue(C09PacketHeldItemChange(n))
}
