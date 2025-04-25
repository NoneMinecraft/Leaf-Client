package net.nonemc.leaf.features.module.modules.player.phases.vanilla

import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.block.BlockLib

class FastFallPhase : PhaseMode("FastFall") {
    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.noClip = true
        mc.thePlayer.motionY -= 10.0
        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)
        mc.thePlayer.onGround =
            BlockLib.collideBlockIntersects(mc.thePlayer.entityBoundingBox) { block: Block? -> block !is BlockAir }
    }
}