package net.nonemc.leaf.features.module.modules.rage.rage.handle

import net.nonemc.leaf.features.module.modules.rage.RageBot
import net.nonemc.leaf.features.module.modules.rage.rage.control.fired

 fun handleSpreadAction(action: () -> Unit, reset: () -> Unit) {
    if (RageBot.noSpreadTriggerMode.get() == "Fired") {
        if (fired()) action() else reset()
    } else {
        if (RageBot.noSpreadTicks <= RageBot.noSpreadTick.get()) {
            action()
            RageBot.noSpreadTicks++
        } else {
            reset()
            RageBot.noSpreadTicks = 0
        }
    }
}

 fun handleSwitchAction() {
    handleSpreadAction({
        switch()
    }, {
        back(3)
    })
}

 fun handlePacketAction() {
    handleSpreadAction({
        packetSwitch()
    }, {
        backPacket(3)
    })
}

 fun handleSwitchOffsetsAction() {
    handleSpreadAction({
        switch()
        RageBot.offsetPitch = RageBot.noSpreadSwitchOffsetsPitchTick1.get()
        RageBot.offsetYaw = RageBot.noSpreadSwitchOffsetsYawTick1.get()
    }, {
        back(3)
        RageBot.offsetPitch = RageBot.noSpreadSwitchOffsetsPitchTick2.get()
        RageBot.offsetYaw = RageBot.noSpreadSwitchOffsetsYawTick2.get()
    })
}

 fun handlePacketOffsetsAction() {
    handleSpreadAction({
        packetSwitch()
        RageBot.offsetPitch = RageBot.noSpreadSwitchOffsetsPitchTick1.get()
        RageBot.offsetYaw = RageBot.noSpreadSwitchOffsetsYawTick1.get()
    }, {
        backPacket(3)
        RageBot.offsetPitch = RageBot.noSpreadSwitchOffsetsPitchTick2.get()
        RageBot.offsetYaw = RageBot.noSpreadSwitchOffsetsYawTick2.get()
    })
}