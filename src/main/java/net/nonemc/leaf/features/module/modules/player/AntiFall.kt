package net.nonemc.leaf.features.module.modules.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue

@ModuleInfo(name = "AntiFall", category = ModuleCategory.PLAYER)
object AntiFall : Module() {
    private val maxDetectionHigh = IntegerValue("MaxDetectionHigh",10,1,50)
    private val minHigh = IntegerValue("MinHigh",10,1,50)
    private val maxFallHigh = FloatValue("MaxFallHigh",2f,0f,10f)

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val player = mc.thePlayer ?: return
        val distance = getDistanceToGround(maxDetectionHigh.get(),player)
        if (distance >= minHigh.get() && mc.thePlayer.fallDistance >= maxFallHigh.get()){
            if (event.packet is C03PacketPlayer) event.packet.onGround = true
            mc.timer.timerSpeed = 8f
        }else{
            mc.timer.timerSpeed = 1f
        }
    }

    fun getDistanceToGround(max:Int,player:EntityPlayer): Double {
        val pos = Vec3(player.posX, player.posY, mc.thePlayer.posZ)
        for (i in 0 until max) {
            val checkPos = BlockPos(MathHelper.floor_double(pos.xCoord), MathHelper.floor_double(pos.yCoord) - i, MathHelper.floor_double(pos.zCoord))
            if (!mc.theWorld.isAirBlock(checkPos)) {
                val blockY = checkPos.y + 1.0
                return pos.yCoord - blockY
            }
        }
        return 0.0
    }
}