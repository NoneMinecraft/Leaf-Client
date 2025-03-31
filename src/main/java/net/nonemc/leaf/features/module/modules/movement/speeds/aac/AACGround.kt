package net.nonemc.leaf.features.module.modules.movement.speeds.aac

import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.value.FloatValue

class AACGround : SpeedMode("AACGround") {
    private val timerValue = FloatValue("${valuePrefix}Timer", 3f, 1.1f, 10f)

    override fun onUpdate() {
        if (!MovementUtils.isMoving()) return

        mc.timer.timerSpeed = timerValue.get()

        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
}