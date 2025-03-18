package net.nonemc.leaf.features.module.modules.rage.rage.special

import net.nonemc.leaf.utils.mc

fun spreadOffset(tick : Int): Double {
    return if (!mc.thePlayer.isSprinting) tick / 12.0 else if (tick <= 1) 0.0 else tick / 12.0
}