package net.nonemc.leaf.features.module.modules.player.phases.matrix

import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.entity.EntityMoveLib

class OldMatrixPhase : PhaseMode("OldMatrix") {

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ)
        mc.gameSettings.keyBindForward.pressed = true
        EntityMoveLib.strafe(0.1f)
        mc.gameSettings.keyBindForward.pressed = false
    }
}