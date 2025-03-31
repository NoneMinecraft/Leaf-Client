package net.nonemc.leaf.features.module.modules.movement.flys.other

import net.minecraft.block.BlockAir
import net.minecraft.util.AxisAlignedBB
import net.nonemc.leaf.event.BlockBBEvent
import net.nonemc.leaf.features.module.modules.movement.flys.FlyMode

class FakeGroundFly : FlyMode("FakeGround") {
    override fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockAir && event.y <= fly.launchY) {
            event.boundingBox = AxisAlignedBB.fromBounds(
                event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                event.x + 1.0, fly.launchY, event.z + 1.0
            )
        }
    }
}