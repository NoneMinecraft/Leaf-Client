package net.ccbluex.liquidbounce.features.module.modules.rage.rage.utils

import net.minecraft.entity.player.EntityPlayer
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.data.Pos

fun getPos(entity: EntityPlayer): Pos {
    return Pos(entity.posX, entity.posY, entity.posZ)
}