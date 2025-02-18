package net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils

import net.ccbluex.liquidbounce.utils.mc

fun ping():Int{
   return mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime
}