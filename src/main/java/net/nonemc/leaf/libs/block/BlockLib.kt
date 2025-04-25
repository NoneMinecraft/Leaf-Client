package net.nonemc.leaf.libs.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.nonemc.leaf.libs.base.MinecraftInstance

object BlockLib : MinecraftInstance() {
    @JvmStatic
    fun getBlock(vec3: Vec3?): Block? = getBlock(BlockPos(vec3))
    @JvmStatic
    fun getBlock(blockPos: BlockPos?): Block? = mc.theWorld?.getBlockState(blockPos)?.block
    @JvmStatic
    fun getMaterial(blockPos: BlockPos?): Material? = getBlock(blockPos)?.material
    @JvmStatic
    fun isReplaceable(blockPos: BlockPos?) = getMaterial(blockPos)?.isReplaceable ?: false
    @JvmStatic
    fun getState(blockPos: BlockPos?): IBlockState = mc.theWorld.getBlockState(blockPos)
    @JvmStatic
    fun canBeClicked(blockPos: BlockPos?) = getBlock(blockPos)?.canCollideCheck(getState(blockPos), false) ?: false && mc.theWorld.worldBorder.contains(blockPos)
    @JvmStatic
    fun getBlockName(id: Int): String = Block.getBlockById(id).localizedName
    @JvmStatic
    fun collideBlock(axisAlignedBB: AxisAlignedBB, collide: (Block?) -> Boolean): Boolean {
        for (x in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minX) until
                MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxX) + 1) {
            for (z in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minZ) until
                    MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxZ) + 1) {
                val block = getBlock(BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble()))

                if (!collide(block)) {
                    return false
                }
            }
        }
        return true
    }
    @JvmStatic
    fun collideBlockIntersects(axisAlignedBB: AxisAlignedBB, collide: (Block?) -> Boolean): Boolean {
        for (x in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minX) until
                MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxX) + 1) {
            for (z in MathHelper.floor_double(mc.thePlayer.entityBoundingBox.minZ) until
                    MathHelper.floor_double(mc.thePlayer.entityBoundingBox.maxZ) + 1) {
                val blockPos = BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble())
                val block = getBlock(blockPos)

                if (collide(block)) {
                    val boundingBox = block?.getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos))
                        ?: continue

                    if (mc.thePlayer.entityBoundingBox.intersectsWith(boundingBox)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}