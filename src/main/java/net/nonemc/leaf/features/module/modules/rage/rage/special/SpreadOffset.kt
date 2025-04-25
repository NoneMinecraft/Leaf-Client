package net.nonemc.leaf.features.module.modules.rage.rage.special

import net.nonemc.leaf.libs.base.mc

fun spreadOffset(tick: Int): Double {
    return if (!net.nonemc.leaf.libs.base.mc.thePlayer.isSprinting) tick / 12.0 else if (tick <= 1) 0.0 else tick / 12.0
}