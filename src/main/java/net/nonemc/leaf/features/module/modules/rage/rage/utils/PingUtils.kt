package net.nonemc.leaf.features.module.modules.rage.rage.utils

import net.nonemc.leaf.utils.mc

fun ping():Int{
   return mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime
}