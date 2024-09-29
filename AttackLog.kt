package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.minecraft.entity.player.EntityPlayer

@ModuleInfo(name = "AttackLog", category = ModuleCategory.COMBAT)
class AttackLog : Module() {
    var win = 0
    var lose = 0
    override fun onDisable() {
        lose = 0
        win = 0
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
        val targetPlayer = mc.theWorld.playerEntities
            .filterIsInstance<EntityPlayer>()
            .filter { it != mc.thePlayer && it.name == event.targetEntity.name}
            .firstOrNull{true}
        targetPlayer.let {
            ChatPrint("击中目标:"+event.targetEntity.name+" 对方血量:"+targetPlayer!!.health + " 玩家血量:"+ mc.thePlayer.health)

                if (mc.thePlayer.health  > targetPlayer.health && (mc.thePlayer.health  - targetPlayer.health) >= 0.789) {
                    ChatPrint("你可能胜利")
                    win ++
            }else{
                    ChatPrint("你可能死亡")
                    lose ++
            }
            if (mc.thePlayer.health < 10){
            if (mc.thePlayer.health  > targetPlayer.health && (mc.thePlayer.health  - targetPlayer.health) >= 0.789){
                ChatPrint("决策点:你可能胜利")
            }else{
                ChatPrint("决策点:你可能死亡")
            }
            }
        }


}
    }
