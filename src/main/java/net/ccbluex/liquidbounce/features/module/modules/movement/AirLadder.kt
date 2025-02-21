
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockLadder
import net.minecraft.block.BlockVine
import net.minecraft.util.BlockPos

@ModuleInfo(name = "AirLadder", category = ModuleCategory.MOVEMENT)
class AirLadder : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val block = BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ))
        if ((block is BlockLadder && mc.thePlayer.isCollidedHorizontally) || (block is BlockVine || BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) is BlockVine)) {
            mc.thePlayer.motionY = 0.15
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}
