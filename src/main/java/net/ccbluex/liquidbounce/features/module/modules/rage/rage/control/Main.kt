package net.ccbluex.liquidbounce.features.module.modules.rage.rage.control

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.rageBotTargetPlayer
import net.ccbluex.liquidbounce.utils.particles.Vec3

fun idle(): Boolean {
    return rageBotTargetPlayer == null || !LiquidBounce.moduleManager[RageBot::class.java]!!.state
}
fun targetVec(): Vec3? {
    return rageBotTargetPlayer?.let { Vec3( it.posX , it.posX ,it.posX ) }
}