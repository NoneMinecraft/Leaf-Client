/*
Leaf Hack Client
Code By None
* */
package net.ccbluex.liquidbounce.features

import net.ccbluex.liquidbounce.features.module.modules.client.Rotations
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.mc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.MathHelper
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt
var Pitch = 0.0F

object MainLib {
    fun FindItems(items: Item): Int {
        for (i in 1 until mc.thePlayer.inventory.mainInventory.size) {
            val stack = mc.thePlayer.inventory.getStackInSlot(i)
            if (stack != null && stack.item == items) {
                return i

            }
        }
        return -1
    }




fun block () {
        val playerPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
        val radius = 1 // 半径
        val nonAirBlocks: MutableList<BlockPos> = ArrayList()
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val blockPos = playerPos.add(x, y, z)
                    if (mc.theWorld.getBlockState(blockPos).block !== Blocks.air) {
                        nonAirBlocks.add(blockPos)
                    }
                }
            }
        }
        var closestDistance = Double.MAX_VALUE
        var closestBlock: BlockPos? = null
        for (blockPos in nonAirBlocks) {
            val distanceSq = mc.thePlayer.getDistanceSq(
                blockPos.x.toDouble(),
                blockPos.y.toDouble(),
                blockPos.z.toDouble()
            )
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq
                closestBlock = blockPos
            }

            if (closestBlock != null) {
             mc.thePlayer.rotationYaw= Math.toDegrees(
                    atan2(
                        closestBlock.z - mc.thePlayer.posZ,
                        closestBlock.x - mc.thePlayer.posX

                    )
                ).toFloat() - 90


                   mc.thePlayer.rotationPitch = Math.toDegrees(

                    -atan(
                        (closestBlock.y - mc.thePlayer.posY) / sqrt(
                            (closestBlock.x - mc.thePlayer.posX).pow(2.0) + (closestBlock.z - mc.thePlayer.posZ).pow(
                                2.0

                            )
                        )
                    )

                ).toFloat()
            }
        }}


    fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld.getCollisionBoxes(
                mc.thePlayer.entityBoundingBox.offset(
                    mc.thePlayer.motionX * 1.4,
                    i.toDouble(),
                    mc.thePlayer.motionZ * 1.4
                )
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }

    fun RightClick(pressed:Boolean){
    mc.gameSettings.keyBindUseItem.pressed = pressed
    }
    fun LeftClick(pressed:Boolean){
        mc.gameSettings.keyBindUseItem.pressed = pressed
    }
    fun Jump(pressed:Boolean){
        mc.gameSettings.keyBindJump.pressed = pressed
    }
    fun Sneak(pressed:Boolean){
        mc.gameSettings.keyBindSneak.pressed = pressed
    }
    fun Chat(message:String){
        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage())
    }
    fun ChatPrint(message:String){
    mc.ingameGUI.chatGUI.printChatMessage(ChatComponentText(message))
}

}
