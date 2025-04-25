package net.nonemc.leaf.features.module.modules.rage.rage.control

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.modules.rage.RageBot
import net.nonemc.leaf.features.module.modules.rage.RageBot.rageBotTargetPlayer

fun idle(): Boolean {
    return rageBotTargetPlayer == null || !Leaf.moduleManager[RageBot::class.java]!!.state
}