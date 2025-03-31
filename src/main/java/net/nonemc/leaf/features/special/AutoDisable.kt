package net.nonemc.leaf.features.special

import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.WorldEvent
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.EnumTriggerType
import net.nonemc.leaf.ui.client.hud.element.elements.Notification
import net.nonemc.leaf.ui.client.hud.element.elements.NotifyType

object AutoDisable : Listenable {
    private const val name = "AutoDisable"

    @EventTarget
    fun onWorld(event: WorldEvent) {
        Leaf.moduleManager.modules
            .filter { it.state && it.autoDisable == EnumAutoDisableType.RESPAWN && it.triggerType == EnumTriggerType.TOGGLE }
            .forEach { module ->
                module.state = false
                Leaf.hud.addNotification(
                    Notification(
                        this.name,
                        "Disabled ${module.name} due world Changed.",
                        NotifyType.WARNING,
                        2000
                    )
                )
            }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            Leaf.moduleManager.modules
                .filter { it.state && it.autoDisable == EnumAutoDisableType.FLAG && it.triggerType == EnumTriggerType.TOGGLE }
                .forEach { module ->
                    module.state = false
                    Leaf.hud.addNotification(
                        Notification(
                            this.name,
                            "Disabled ${module.name} due flags.",
                            NotifyType.WARNING,
                            2000
                        )
                    )
                }
        }
    }

    fun handleGameEnd() {
        Leaf.moduleManager.modules
            .filter { it.state && it.autoDisable == EnumAutoDisableType.GAME_END }
            .forEach { module ->
                module.state = false
                Leaf.hud.addNotification(
                    Notification(
                        this.name,
                        "Disabled ${module.name} due to game end.",
                        NotifyType.WARNING,
                        2000
                    )
                )
            }
    }

    override fun handleEvents() = true
}
