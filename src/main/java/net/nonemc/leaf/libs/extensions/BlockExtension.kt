package net.nonemc.leaf.libs.extensions

import net.minecraft.block.Block
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.nonemc.leaf.libs.block.BlockLib

fun BlockPos.getBlock() = BlockLib.getBlock(this)

fun BlockPos.getVec() = Vec3(x + 0.5, y + 0.5, z + 0.5)
fun AxisAlignedBB(x: Int, y: Int, z: Int): AxisAlignedBB {
    return AxisAlignedBB(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
}

fun AxisAlignedBB.down(height: Double): AxisAlignedBB {
    return AxisAlignedBB(minX, minY, minZ, maxX, maxY - height, maxZ)
}

fun getBlockName(id: Int): String = Block.getBlockById(id).localizedName


