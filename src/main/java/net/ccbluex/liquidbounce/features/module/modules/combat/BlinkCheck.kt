package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.MainLib.ChatPrint
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.math.Vec4
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos

@ModuleInfo(name = "BlinkCheck", category = ModuleCategory.COMBAT)
class BlinkCheck : Module() {
    private var vlTick = 0
    private var baseTick = 0

    private var lastPositions = mutableListOf<BlockPos>()


    private val playerPositions = mutableMapOf<String, MutableList<BlockPos>>()
    private val playerPositions2 = mutableMapOf<String, MutableList<BlockPos>>()


    override fun onDisable() {
        vlTick = 0
        baseTick = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val world = mc.theWorld
        playerPositions.keys.toList().forEach { playerName ->
            val player = world.getPlayerEntityByName(playerName) ?: return@forEach
            val currentPos = BlockPos(player.posX, player.posY, player.posZ)
            val positions = playerPositions[playerName]!!

            positions.add(currentPos)

            if (positions.size > 3) {
                val oldPos = positions.removeAt(0)
                val distance = currentPos.distanceSq(oldPos.x.toDouble(), oldPos.y.toDouble(), oldPos.z.toDouble())

                if (distance > 9.0) {
                    vlTick++
                    ChatPrint("§bVAC §f${player.name} §ffailed §bBlink[movement] §f(vl:$vlTick)")
                }
            }}
        playerPositions2.keys.toList().forEach { playerName ->
            val player = world.getPlayerEntityByName(playerName) ?: return@forEach
            val currentPos = BlockPos(player.posX, player.posY, player.posZ)
            val positions = playerPositions[playerName]!!

            positions.add(currentPos)

            if (positions.size > 3) {
                val oldPos = positions.removeAt(0)
                val distance = currentPos.distanceSq(oldPos.x.toDouble(), oldPos.y.toDouble(), oldPos.z.toDouble())

                if (distance == 0.0 && player.name != mc.thePlayer.name &&!player.onGround) {
                    vlTick++
                    ChatPrint("§bVAC §f${player.name} §ffailed §bBlink §f(vl:$vlTick)")
                }
            }}

        val allPlayers = world.playerEntities
        allPlayers.forEach { player ->
            val playerName = player.name
            val currentPos = BlockPos(player.posX, player.posY, player.posZ)

            // 初始化或更新玩家的位置记录
            if (playerPositions[playerName] == null) {
                playerPositions[playerName] = mutableListOf(currentPos)
            } else {
                val positions = playerPositions[playerName]!!
                positions.add(currentPos)

                if (positions.size > 3) {
                    val oldPos = positions.removeAt(0)
                    val distance = currentPos.distanceSq(oldPos.x.toDouble(), oldPos.y.toDouble(), oldPos.z.toDouble())

                    if (distance > 9.0) {
                        vlTick++
                        ChatPrint("§bVAC §f${player.name} §ffailed §bBlink[movement] §f(vl:$vlTick)")
                    }
                }
            }
        }
        val airPlayers = world.playerEntities
        airPlayers.forEach { player ->
            val playerName = player.name
            val currentPos = BlockPos(player.posX, player.posY, player.posZ)

            // 初始化或更新玩家的位置记录
            if (playerPositions[playerName] == null) {
                playerPositions[playerName] = mutableListOf(currentPos)
            } else {
                val positions = playerPositions[playerName]!!
                positions.add(currentPos)

                if (positions.size > 3) {
                    val oldPos = positions.removeAt(0)
                    val distance = currentPos.distanceSq(oldPos.x.toDouble(), oldPos.y.toDouble(), oldPos.z.toDouble())

                    if (distance == 0.0 && player.name != mc.thePlayer.name &&!player.onGround) {
                        vlTick++
                        ChatPrint("§bVAC §f${player.name} §ffailed §bBlink §f(vl:$vlTick)")
                    }
                }
            }
        }
    }

}
