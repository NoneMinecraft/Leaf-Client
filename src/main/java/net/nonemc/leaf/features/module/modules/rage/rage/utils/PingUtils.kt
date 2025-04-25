package net.nonemc.leaf.features.module.modules.rage.rage.utils

import net.nonemc.leaf.libs.base.mc

fun ping(): Int {
    return net.nonemc.leaf.libs.base.mc.netHandler.getPlayerInfo(net.nonemc.leaf.libs.base.mc.thePlayer.uniqueID).responseTime
}