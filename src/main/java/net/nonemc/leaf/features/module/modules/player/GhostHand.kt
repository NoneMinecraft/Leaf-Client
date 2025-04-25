package net.nonemc.leaf.features.module.modules.player

import net.minecraft.block.Block
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.block.BlockLib.getBlock
import net.nonemc.leaf.libs.extensions.getVec
import net.nonemc.leaf.value.BlockValue
import net.nonemc.leaf.value.IntegerValue

@ModuleInfo(name = "GhostHand", category = ModuleCategory.PLAYER)
class GhostHand : Module() {
    private val blockValue = BlockValue("Block", 54)
    private val radiusValue = IntegerValue("Radius", 4, 2, 7)

    private var click = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!click && mc.gameSettings.keyBindUseItem.isKeyDown) {
            Thread({
                val radius = radiusValue.get()
                val selectedBlock = Block.getBlockById(blockValue.get())
                var diff = 114514F
                var targetBlock: BlockPos? = null

                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val xPos: Int = mc.thePlayer.posX.toInt() + x
                            val yPos: Int = mc.thePlayer.posY.toInt() + y
                            val zPos: Int = mc.thePlayer.posZ.toInt() + z
                            val blockPos = BlockPos(xPos, yPos, zPos)
                            val block = getBlock(blockPos)
                            if (block === selectedBlock) {
                                val dist = mc.thePlayer.getDistanceSqToCenter(blockPos).toFloat()
                                if (dist < diff) {
                                    diff = dist
                                    targetBlock = blockPos
                                }
                            }
                        }
                    }
                }

                if (targetBlock != null) {
                    if (mc.playerController.onPlayerRightClick(
                            mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem,
                            targetBlock, EnumFacing.DOWN, targetBlock.getVec()
                        )
                    ) {
                        mc.thePlayer.swingItem()
                    }
                }
            }, "GhostHand").start()
            click = true
        } else if (!mc.gameSettings.keyBindUseItem.isKeyDown) {
            click = false
        }
    }
}
