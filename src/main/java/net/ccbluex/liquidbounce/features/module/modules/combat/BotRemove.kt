package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@ModuleInfo(name = "BotRemove", category = ModuleCategory.COMBAT)
class BotRemove : Module() {

    private val mc: Minecraft = Minecraft.getMinecraft()
    private var playerList: List<EntityPlayer> = emptyList()
    private var index: Int = 0
    private var next: Boolean = false
    private var oldPosX: Double = 0.0
    private var oldPosZ: Double = 0.0
    private lateinit var currentEntity: EntityPlayer

    private val notAlwaysInRadius: MutableMap<EntityPlayer, Boolean> = mutableMapOf()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        if (!next) {
            playerList = mc.theWorld.loadedEntityList.filterIsInstance<EntityPlayer>()
            if (index >= playerList.size) {
                index = 0
                return
            }
            currentEntity = playerList[index]
            oldPosX = currentEntity.posX
            oldPosZ = currentEntity.posZ
            next = true
            return
        }

        val xDiff = oldPosX - currentEntity.posX
        val zDiff = oldPosZ - currentEntity.posZ
        val speed = Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 10

        if (isBot(currentEntity, speed)) {
            mc.theWorld.removeEntity(currentEntity)
            mc.thePlayer.addChatMessage(
                net.minecraft.util.ChatComponentText(
                    "§8[§9§lMatrixBotRemover§8] §3Remove §a${currentEntity.name}§3."
                )
            )
        }

        index++
        next = false
    }

    private fun isBot(entity: EntityPlayer, speed: Double): Boolean {
        return entity != mc.thePlayer &&
                !notAlwaysInRadius.containsKey(entity) &&
                speed > 8.0 &&
                mc.thePlayer.getDistanceToEntity(entity) <= 5.0 &&
                within(entity.posY, mc.thePlayer.posY - 1.5, mc.thePlayer.posY + 1.5)
    }

    private fun within(n: Double, mi: Double, ma: Double): Boolean {
        return n <= ma && n >= mi
    }

    override fun onEnable() {
        next = false
        index = 0
        oldPosX = 0.0
        oldPosZ = 0.0
    }
}
