/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "TimerRange", category = ModuleCategory.COMBAT)
object TimerRange : Module() {
    private val Range = IntegerValue("Range", 3, 1, 10)
    private val Low = FloatValue("LowTimer", 0.1F, 0.05F, 1F)
    private val LowTimerTicks = IntegerValue("LowTimerTicks", 1, 1, 20)
    private val Max = FloatValue("MaxTimer", 100F, 1F, 1000F)
    private val MaxTimerTicks = IntegerValue("MaxTimerTicks", 1, 1, 20)
    private val C03 = BoolValue("C03", true)
    private val C0F = BoolValue("C0F", true)
    var tag1 = false
    var cancel = false
    var n = 0
    var n2 = 0
    override fun onDisable() {
        tag1 = false
        n=0
        cancel = false
        mc.timer.timerSpeed = 1.0f
        n2 = 0
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
            val packet = event.packet
            if (packet is C0FPacketConfirmTransaction&&cancel&&C0F.get()) {
                event.cancelEvent()

            }
            if (cancel&&C03.get()&&packet is C03PacketPlayer && !(packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C05PacketPlayerLook || packet is C03PacketPlayer.C06PacketPlayerPosLook)) {
                event.cancelEvent()
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return
        val world = minecraft.theWorld ?: return

        // Define the bounding box (3x3x3 centered around the player)
        val minX = player.posX - Range.get()
        val minY = player.posY - Range.get()
        val minZ = player.posZ - Range.get()
        val maxX = player.posX + Range.get()
        val maxY = player.posY + Range.get()
        val maxZ = player.posZ + Range.get()
        val boundingBox = AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)

        // Get all entities within the bounding box
        val entities = world.getEntitiesWithinAABB(EntityPlayer::class.java, boundingBox)

        // Check each entity
        for (entity in entities) {
            if (entity != player) {
                cancel = true
                if (!tag1){
                    if (n<= LowTimerTicks.get()){
                        n++
                        mc.timer.timerSpeed = Low.get()
                    }else if (n2<= MaxTimerTicks.get()){
                        n2++
                        mc.timer.timerSpeed = Max.get()
                    }else
                    {
                        tag1 = true
                        mc.timer.timerSpeed = 1F
                    }
                }
            }
        }
        tag1 = false
    }

}
