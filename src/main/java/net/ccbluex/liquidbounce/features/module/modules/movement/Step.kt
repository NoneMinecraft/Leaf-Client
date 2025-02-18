
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.exploit.Phase
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.stats.StatList

@ModuleInfo(name = "Step", category = ModuleCategory.MOVEMENT)
class Step : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Jump", "NCP"), "Vanilla")
    private val heightValue = FloatValue("Height", 1F, 0.6F, 10F)
    private val jumpHeightValue = FloatValue("JumpMotion", 0.42F, 0.37F, 0.42F).displayable { modeValue.equals("Jump") }
    private val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val timerValue = FloatValue("Timer", 1F, 0.05F, 1F)
    private val timerDynValue = BoolValue("UseDynamicTimer", false)
    private var isStep = false
    private var stepX = 0.0
    private var stepY = 0.0
    private var stepZ = 0.0
    var wasTimer = false
    var lastOnGround = false
    var canStep = false

    private val timer = MSTimer()

    override fun onDisable() {
        mc.thePlayer ?: return
        mc.thePlayer.stepHeight = 0.6F
        if (wasTimer) mc.timer.timerSpeed = 1.0F
        wasTimer = false
        lastOnGround = mc.thePlayer.onGround
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround && lastOnGround) {
            canStep = true
        }else {
            canStep = false
            mc.thePlayer.stepHeight = 0.6F
        }
        lastOnGround = mc.thePlayer.onGround
        if (wasTimer) {
            wasTimer = false
            mc.timer.timerSpeed = 1.0F
        }
        val mode = modeValue.get()
        when {
            mode.equals("jump", true) && mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround
                    && !mc.gameSettings.keyBindJump.isKeyDown -> {
                fakeJump()
                mc.thePlayer.motionY = jumpHeightValue.get().toDouble()
            }
        }
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        mc.thePlayer ?: return
        val mode = modeValue.get()

        if (event.eventState == EventState.PRE) {
            if (LiquidBounce.moduleManager[Phase::class.java]!!.state) {
                event.stepHeight = 0F
                return
            }
            if (!mc.thePlayer.onGround || !timer.hasTimePassed(delayValue.get().toLong()) ||
                mode.equals("Jump", ignoreCase = true)) {
                mc.thePlayer.stepHeight = 0.6F
                event.stepHeight = 0.6F
                return
            }
            val height = heightValue.get()
            mc.thePlayer.stepHeight = height
            event.stepHeight = height
            
            if (event.stepHeight > 0.6F) {
                isStep = true
                stepX = mc.thePlayer.posX
                stepY = mc.thePlayer.posY
                stepZ = mc.thePlayer.posZ
            }
            
        } else {
            if (!isStep) {
                return
            }
            if (mc.thePlayer.entityBoundingBox.minY - stepY > 0.6) {
                if (timerValue.get()<1.0) {
                    wasTimer = true
                    mc.timer.timerSpeed = timerValue.get()
                    if (timerDynValue.get()) {
                        mc.timer.timerSpeed = (mc.timer.timerSpeed / Math.sqrt(mc.thePlayer.entityBoundingBox.minY - stepY)).toFloat()
                    }
                }
                when {
                    mode.equals("NCP", ignoreCase = true) -> {
                        fakeJump()
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 0.41999998688698, stepZ, false))
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(stepX,
                            stepY + 0.7531999805212, stepZ, false))
                        timer.reset()
                    }
                }
            }

            isStep = false
            stepX = 0.0
            stepY = 0.0
            stepZ = 0.0
        }
    }
    private fun fakeJump() {
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
    }
}
