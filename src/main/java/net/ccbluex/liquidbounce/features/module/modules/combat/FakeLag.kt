/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import com.sun.jdi.DoubleValue
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.render.Breadcrumbs
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "FakeLag", category = ModuleCategory.COMBAT)
class FakeLag : Module() {
    private val Range = IntegerValue("Range", 3, 1, 10)
    private val inboundValue = BoolValue("Inbound", false)
    private val onattack = BoolValue("OnAttack", true)
    private val glLineWidth = FloatValue("glLineWidth", 2F, 1F, 10F)
    private val glColor4dRed = FloatValue("glColor4d-Red", 1F, 0F, 1F)
    private val glColor4dGreen = FloatValue("glColor4d-Green", 1F, 0F, 1F)
    private val glColor4dBlue = FloatValue("glColor4d-Blue", 1F, 0F, 1F)
    private val pulseTimer = MSTimer()
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var disableLogger = false
    private val positions = LinkedList<DoubleArray>()
    private var tag1 = false
    private var packet = false
    override fun onDisable() {
        packet = false
        tag1 = false
        if (mc.thePlayer == null) return
        blink()
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
        packet = false
        tag1 = false
        if (mc.thePlayer == null) return
        blink()
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (packet){
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer) { // Cancel all movement stuff
            event.cancelEvent()
        }
        if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity
        ) {
            event.cancelEvent()
            packets.add(packet as Packet<INetHandlerPlayServer>)
        }
        if (packet is S08PacketPlayerPosLook && inboundValue.get()) event.cancelEvent()
    }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (packet){
        val breadcrumbs = LiquidBounce.moduleManager[Breadcrumbs::class.java]!!
        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glLineWidth(glLineWidth.get())
            GL11.glBegin(GL11.GL_LINE_STRIP)
            RenderUtils.glColor(breadcrumbs.color)
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ
            for (pos in positions) GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)
            GL11.glColor4d(glColor4dRed.get().toDouble(), glColor4dGreen.get().toDouble(), glColor4dBlue.get().toDouble(), 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        val player = mc.thePlayer ?: return
        val world = mc.theWorld ?: return

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
                if (entity!=null){
                packet = true
                if (!tag1){
                if (mc.thePlayer == null) return
                    fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
                    fakePlayer!!.clonePlayer(mc.thePlayer, true)
                    fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
                    fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
                    mc.theWorld.addEntityToWorld(-1337, fakePlayer)
                synchronized(positions) {
                    positions.add(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2, mc.thePlayer.posZ))
                    positions.add(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
                }
                pulseTimer.reset()
                tag1 = true
            }
            }else{
                    tag1 = false
                    packet = false
                    if (mc.thePlayer == null) return
                    blink()
                    if (fakePlayer != null) {
                        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
                        fakePlayer = null
                    }
                }}
        }

    if (packet){
        synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY,
                    mc.thePlayer.posZ
                )
            )
        }
    }
    }
    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.addToSendQueue(packets.take())
            }
            disableLogger = false
        } finally {
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }
}