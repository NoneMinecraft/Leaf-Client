package net.nonemc.leaf.features.module.modules.rage.rage.entity

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3

class AxisAlignedBB {
    companion object {
        fun serverBoundingBox(entity: EntityPlayer): AxisAlignedBB {
            val width = entity.width / 2.0
            val height = entity.height
            val serverX = entity.serverPosX.toDouble() / 32.0
            val serverY = entity.serverPosY.toDouble() / 32.0
            val serverZ = entity.serverPosZ.toDouble() / 32.0
            return AxisAlignedBB(
                serverX - width, serverY,
                serverZ - width,
                serverX + width,
                serverY + height,
                serverZ + width
            )
        }
    }
}

fun AxisAlignedBB.setMaxY(newMaxY: Double): AxisAlignedBB {
    return AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, newMaxY, this.maxZ)
}

fun AxisAlignedBB.getCenter(): Vec3 {
    return Vec3(
        (minX + maxX) / 2,
        (minY + maxY) / 2,
        (minZ + maxZ) / 2
    )
}