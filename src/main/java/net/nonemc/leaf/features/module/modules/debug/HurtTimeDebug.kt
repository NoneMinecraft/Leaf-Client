package net.nonemc.leaf.features.module.modules.debug

import net.minecraft.entity.player.EntityPlayer
import net.nonemc.leaf.event.AttackEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo

@ModuleInfo(name = "HurtTimeDebug", category = ModuleCategory.DEBUG)
class HurtTimeDebug : Module() {
    @EventTarget
    fun onAttack(event: AttackEvent) {
        val targetPlayer:EntityPlayer = event.targetEntity as EntityPlayer
        ChatPrint("[${targetPlayer.name}] HurtTime:${targetPlayer.hurtTime}")
    }
}