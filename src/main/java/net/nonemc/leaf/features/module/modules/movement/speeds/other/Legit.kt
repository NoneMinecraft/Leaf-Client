package net.nonemc.leaf.features.module.modules.movement.speeds.other

import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class Legit : SpeedMode("Legit") {
    override fun onPreMotion() {
        if (mc.thePlayer.isInWater) return
        if (EntityMoveLib.isMoving()) {
            if (mc.thePlayer.onGround) mc.thePlayer.jump()
        }
    }
}
