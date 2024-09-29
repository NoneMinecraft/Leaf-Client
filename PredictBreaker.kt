package net.ccbluex.liquidbounce.features.module.modules.combat

import com.sun.jdi.BooleanValue
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils4.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.random.Random

@ModuleInfo(name = "PredictBreaker", category = ModuleCategory.COMBAT)
class PredictBreaker : Module() {
    private val predictsize2 = FloatValue("ThroughWallPredictSize", 10F, 0F, 30F)
    private val mode = ListValue("Mode", arrayOf("Disruptions","FakeLag","AntiCheat","TimerLag","Motion"),"Disruptions")
    private val timing = ListValue("Timing", arrayOf("Pre","Post","Always"),"Always")
    private val movementMode = ListValue("MovementMode", arrayOf("Cube","Line","RandomCube"),"Cube")
    private val lineMode = ListValue("LineMode", arrayOf("Random","OnlyX","OnlyZ"),"Random")
    private val cubeMotionSpeed = FloatValue("CubeMotionSpeed", 0.3f, -1f, 1f)
    private val maxMotionSpeed = FloatValue("MaxMotionSpeed", 0.3f, -1f, 1f)
    private val minMotionSpeed = FloatValue("MaxMotionSpeed", -0.3f, -1f, 1f)
    private val randomMax = IntegerValue("MaxRandom", 3, 0, 10)
    private val cubeTicks = IntegerValue("CubeTick", 3, 0, 10)
    private val ChangeTimer = FloatValue("ChangeTimer", 1.4f, 1f, 10f)
    private val LowTimer = FloatValue("LowTimer", 0.9f, 0.01f, 1.1f)
    private val LowTicks = FloatValue("LowTick", 10f, 1f, 20f)
    private val ChangeTicks = FloatValue("ChangeTick", 100f, 1f, 200f)
    private val threshold = FloatValue("threshold", 0.4f, 0.01f, 1f)
    private val OffGround = BoolValue("OffGround",false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 1000, 1, 500)
    private val inboundValue = BoolValue("Inbound", false)
    private var tickCounter = 0
    private var phase = 1
    private val pulseTimer = MSTimer()
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    var mt = 0
    var isreset = false
    var types = 0
    var cubeTick = 0

    private var disableLogger = false
    private val positions = LinkedList<DoubleArray>()
    var motionTick =0

    override fun onEnable() {
        when(mode.get()) {
            "FakeLag" -> {
                if (mc.thePlayer == null) return
                synchronized(positions) {
                    positions.add(
                        doubleArrayOf(
                            mc.thePlayer.posX,
                            mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                            mc.thePlayer.posZ
                        )
                    )
                    positions.add(
                        doubleArrayOf(
                            mc.thePlayer.posX,
                            mc.thePlayer.entityBoundingBox.minY,
                            mc.thePlayer.posZ
                        )
                    )
                }
                pulseTimer.reset()
            }
        }
    }
    override fun onDisable() {
        cubeTick = 0
        types = 0
        isreset = false
        mt = 0
        motionTick =0
        mc.timer.timerSpeed = 1F
        phase = 1
        tickCounter = 0
        when(mode.get()) {
            "FakeLag" -> {
                if (mc.thePlayer == null) return
                blink()
            }
        }
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (timing.get() == "Always") {
            when(mode.get()) {

                "FakeLag" -> {
                    val packet = event.packet
                    if (mc.thePlayer == null || disableLogger) return
                    if (packet is C03PacketPlayer) {
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
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (timing.get() == "Always"){
            when(mode.get()){
                "Motion" -> {
                    when(movementMode.get()){
                        "Line" -> {
                            when(lineMode.get()){
                                "OnlyX" -> {
                                    if (!isreset){
                                        motionTick = Random.nextInt(0, randomMax.get())
                                        isreset = true
                                    }
                                    if (motionTick > mt) {
                                        mt ++
                                        mc.thePlayer.motionX = Random.nextDouble(minMotionSpeed.get().toDouble(), maxMotionSpeed.get().toDouble())
                                    }else{
                                        mt = 0
                                        isreset = false
                                    }
                                }
                                "OnlyZ" -> {
                                    if (!isreset){
                                        motionTick = Random.nextInt(0, randomMax.get())
                                        isreset = true
                                    }
                                    if (motionTick > mt) {
                                        mt ++
                                        mc.thePlayer.motionZ = Random.nextDouble(minMotionSpeed.get().toDouble(), maxMotionSpeed.get().toDouble())
                                    }else{
                                        mt = 0
                                        isreset = false
                                    }
                                }
                                "Random" -> {
                                    when(Random.nextInt(0,2)){
                                        0 ->{
                                            if (!isreset){
                                            motionTick = Random.nextInt(0, randomMax.get())
                                                isreset = true
                                            }
                                            if (motionTick > mt) {
                                                mt ++
                                                mc.thePlayer.motionX =Random.nextDouble(minMotionSpeed.get().toDouble(), maxMotionSpeed.get().toDouble())
                                            }else{
                                                mt = 0
                                                isreset = false
                                            }
                                        }
                                        1 ->{
                                            if (!isreset){
                                                motionTick = Random.nextInt(0, randomMax.get())
                                                isreset = true
                                            }
                                            if (motionTick > mt) {
                                                mt ++
                                                mc.thePlayer.motionZ =Random.nextDouble(minMotionSpeed.get().toDouble(), maxMotionSpeed.get().toDouble())
                                            }else{
                                                mt = 0
                                                isreset = false
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        "Cube" -> {
                            when (types){
                                0->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionX = cubeMotionSpeed.get().toDouble()
                                        types++
                                        cubeTick= 0
                                    }
                                }
                                1->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionZ = cubeMotionSpeed.get().toDouble()
                                        types++
                                        cubeTick= 0
                                    }
                                }
                                2->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionX = -cubeMotionSpeed.get().toDouble()
                                        types++
                                        cubeTick= 0
                                    }
                                }
                                3->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionZ = -cubeMotionSpeed.get().toDouble()
                                        types = 0
                                        cubeTick= 0
                                    }
                                }
                            }
                        }
                        "RandomCube" -> {
                            when (Random.nextInt(0,5)){
                                0->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionX = cubeMotionSpeed.get().toDouble()
                                        cubeTick= 0
                                    }
                                }
                                1->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionZ = cubeMotionSpeed.get().toDouble()
                                        cubeTick= 0
                                    }
                                }
                                2->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionX = -cubeMotionSpeed.get().toDouble()
                                        cubeTick= 0
                                    }
                                }
                                3->{
                                    if (cubeTick < cubeTicks.get()){
                                        cubeTick++
                                    }else{
                                        mc.thePlayer.motionZ = -cubeMotionSpeed.get().toDouble()
                                        types = 0
                                        cubeTick= 0
                                    }
                                }
                            }
                        }
                    }
                }

                "TimerLag"->{
                    val target = mc.theWorld.playerEntities
                        .filterIsInstance<EntityPlayer>()
                        .filter { it != mc.thePlayer && EntityUtils.isSelected(it, true) }
                        .filter { (!canSeePlayer(mc.thePlayer, it)) }
                        .firstOrNull { true }
                    target?.let {
                            tickCounter++
                            when (phase) {
                                0 -> {
                                    mc.timer.timerSpeed = ChangeTimer.get()
                                    if (tickCounter >= ChangeTicks.get()) {
                                        tickCounter = 0
                                        phase = 1
                                    }
                                }

                                1 -> {
                                    mc.timer.timerSpeed = LowTimer.get()
                                    if (tickCounter >= LowTicks.get()) {
                                        tickCounter = 0
                                        phase = 0
                                    }
                                }
                        }
                    }
                   if (target == null){
                       mc.timer.timerSpeed = 1F
                   }
                }
                "FakeLag"->{
                    synchronized(positions) {
                        positions.add(
                            doubleArrayOf(
                                mc.thePlayer.posX,
                                mc.thePlayer.entityBoundingBox.minY,
                                mc.thePlayer.posZ
                            )
                        )
                    }
                    if (pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
                        blink()
                        pulseTimer.reset()
                    }
                }
                "Disruptions"->{
                    if (mc.thePlayer.motionX > threshold.get() || mc.thePlayer.motionZ > threshold.get() && (!OffGround.get() || !mc.thePlayer.onGround)){
                        mc.thePlayer.motionX *= 0
                        mc.thePlayer.motionY *= 0
                        mc.thePlayer.motionZ *= 0
                    }
                }
           
            }
        }
    }
    private fun canSeePlayer(player: EntityPlayer, target: EntityPlayer): Boolean {
        val world = mc.theWorld
        val playerVec = Vec3(player.posX, player.posY + player.eyeHeight, player.posZ)
        val targetVec = Vec3(target.posX+(target.posX - target.prevPosX) * predictsize2.get(),
            (target.posY + target.eyeHeight*0.8)+target.posY-target.prevPosY,
            target.posZ+(target.posZ-target.prevPosZ)*predictsize2.get())
        val result = world.rayTraceBlocks(playerVec, targetVec, false, true, false)
        return result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS
    }
     fun blink() {
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