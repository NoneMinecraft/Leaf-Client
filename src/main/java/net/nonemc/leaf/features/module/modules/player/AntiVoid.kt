package net.nonemc.leaf.features.module.modules.player

import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.BlockPos
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.event.WorldEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.block.BlockLib
import net.nonemc.leaf.libs.entity.EntityFallLib
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "AntiVoid", category = ModuleCategory.PLAYER)
class AntiVoid : Module() {
    private val modeValue = ListValue(
        "Mode",
        arrayOf(
            "Blink",
            "TPBack",
            "MotionFlag",
            "PacketFlag",
            "GroundSpoof",
        ),
        "Blink"
    )
    private val maxFallDistValue = FloatValue("MaxFallDistance", 10F, 5F, 20F)
    private val resetMotionValue = BoolValue("ResetMotion", false).displayable { modeValue.equals("Blink") }
    private val startFallDistValue =
        FloatValue("BlinkStartFallDistance", 2F, 0F, 5F).displayable { modeValue.equals("Blink") }
    private val autoScaffoldValue = BoolValue("BlinkAutoScaffold", true).displayable { modeValue.equals("Blink") }
    private val motionflagValue =
        FloatValue("MotionFlag-MotionY", 1.0F, 0.0F, 5.0F).displayable { modeValue.equals("MotionFlag") }
    private val voidOnlyValue = BoolValue("OnlyVoid", true)

    private val packetCache = ArrayList<C03PacketPlayer>()
    private var blink = false
    private var canBlink = false
    private var canCancel = false
    private var canSpoof = false
    private var tried = false
    private var flagged = false

    private var posX = 0.0
    private var posY = 0.0
    private var posZ = 0.0
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var lastRecY = 0.0

    override fun onEnable() {
        canCancel = false
        blink = false
        canBlink = false
        canSpoof = false
        if (mc.thePlayer != null) {
            lastRecY = mc.thePlayer.posY
        } else {
            lastRecY = 0.0
        }
        tried = false
        flagged = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (lastRecY == 0.0) {
            lastRecY = mc.thePlayer.posY
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.onGround) {
            tried = false
            flagged = false
        }

        when (modeValue.get().lowercase()) {
            "groundspoof" -> {
                if (!voidOnlyValue.get() || inVoid()) {
                    canSpoof = mc.thePlayer.fallDistance > maxFallDistValue.get()
                }
            }

            "motionflag" -> {
                if (!voidOnlyValue.get() || inVoid()) {
                    if (mc.thePlayer.fallDistance > maxFallDistValue.get() && !tried) {
                        mc.thePlayer.motionY += motionflagValue.get()
                        mc.thePlayer.fallDistance = 0.0F
                        tried = true
                    }
                }
            }

            "packetflag" -> {
                if (!voidOnlyValue.get() || inVoid()) {
                    if (mc.thePlayer.fallDistance > maxFallDistValue.get() && !tried) {
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX + 1,
                                mc.thePlayer.posY + 1,
                                mc.thePlayer.posZ + 1,
                                false
                            )
                        )
                        tried = true
                    }
                }
            }

            "tpback" -> {
                if (mc.thePlayer.onGround && BlockLib.getBlock(
                        BlockPos(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 1.0,
                            mc.thePlayer.posZ
                        )
                    ) !is BlockAir
                ) {
                    posX = mc.thePlayer.prevPosX
                    posY = mc.thePlayer.prevPosY
                    posZ = mc.thePlayer.prevPosZ
                }
                if (!voidOnlyValue.get() || inVoid()) {
                    if (mc.thePlayer.fallDistance > maxFallDistValue.get() && !tried) {
                        mc.thePlayer.setPositionAndUpdate(posX, posY, posZ)
                        mc.thePlayer.fallDistance = 0F
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionY = 0.0
                        mc.thePlayer.motionZ = 0.0
                        tried = true
                    }
                }
            }


            "blink" -> {
                if (!blink) {
                    val collide = EntityFallLib(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0,
                        0F,
                        0F,
                        0F,
                        0F
                    ).findCollision(60)
                    if (canBlink && (collide == null || (mc.thePlayer.posY - collide.y) > startFallDistValue.get())) {
                        posX = mc.thePlayer.posX
                        posY = mc.thePlayer.posY
                        posZ = mc.thePlayer.posZ
                        motionX = mc.thePlayer.motionX
                        motionY = mc.thePlayer.motionY
                        motionZ = mc.thePlayer.motionZ

                        packetCache.clear()
                        blink = true
                    }

                    if (mc.thePlayer.onGround) {
                        canBlink = true
                    }
                } else {
                    if (mc.thePlayer.fallDistance > maxFallDistValue.get()) {
                        mc.thePlayer.setPositionAndUpdate(posX, posY, posZ)
                        if (resetMotionValue.get()) {
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionY = 0.0
                            mc.thePlayer.motionZ = 0.0
                            mc.thePlayer.jumpMovementFactor = 0.00f
                        } else {
                            mc.thePlayer.motionX = motionX
                            mc.thePlayer.motionY = motionY
                            mc.thePlayer.motionZ = motionZ
                            mc.thePlayer.jumpMovementFactor = 0.00f
                        }

                        if (autoScaffoldValue.get()) {

                        }

                        packetCache.clear()
                        blink = false
                        canBlink = false
                    } else if (mc.thePlayer.onGround) {
                        blink = false

                        for (packet in packetCache) {
                            mc.netHandler.addToSendQueue(packet)
                        }
                    }
                }
            }
        }
    }
    private fun inVoid(): Boolean {
        var i = (-(net.nonemc.leaf.libs.base.mc.thePlayer.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = net.nonemc.leaf.libs.base.mc.theWorld.getCollisionBoxes(
                net.nonemc.leaf.libs.base.mc.thePlayer.entityBoundingBox.offset(net.nonemc.leaf.libs.base.mc.thePlayer.motionX * 0.5, i.toDouble(), net.nonemc.leaf.libs.base.mc.thePlayer.motionZ * 0.5)
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        when (modeValue.get().lowercase()) {
            "blink" -> {
                if (blink && (packet is C03PacketPlayer)) {
                    packetCache.add(packet)
                    event.cancelEvent()
                }
            }

            "groundspoof" -> {
                if (canSpoof && (packet is C03PacketPlayer)) {
                    packet.onGround = true
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}
