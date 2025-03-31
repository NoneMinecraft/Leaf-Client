package net.nonemc.leaf.features.module.modules.rage.rage.control

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.rage.RageBot
import net.nonemc.leaf.features.module.modules.rage.RageBot.rageBotTargetPlayer
import net.nonemc.leaf.utils.particles.Vec3

fun idle(): Boolean {
    return rageBotTargetPlayer == null || !Leaf.moduleManager[RageBot::class.java]!!.state
}

fun targetVec(): Vec3? {
    return rageBotTargetPlayer?.let { Vec3(it.posX, it.posX, it.posX) }
}