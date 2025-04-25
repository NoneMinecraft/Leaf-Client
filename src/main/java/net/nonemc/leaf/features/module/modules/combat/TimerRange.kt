package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.client.settings.GameSettings
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.combat.aura.invoke.isMove
import net.nonemc.leaf.libs.entity.EntityTypeLib
import net.nonemc.leaf.libs.extensions.getDistanceToEntityBox
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue

@ModuleInfo(name = "TimerRange", category = ModuleCategory.COMBAT)
class TimerRange : Module() {
    private val maxRange = FloatValue("MaxRange",6f,0f,10f)
    private val minRange = FloatValue("MinRange",0f,0f,10f)

    private val targetMaxHurtTime = IntegerValue("TargetMaxHurtTime",10,0,10)
    private val targetMinHurtTime = IntegerValue("TargetMinHurtTime",0,0,10)

    private val maxLowRange = FloatValue("MaxLowRange",3.5f,0f,10f)
    private val minLowRange = FloatValue("MinLowRange",3f,0f,10f)
    private val maxLowDelay = IntegerValue("MaxLowDelay",5,0,20)
    private val lowTimerValue = FloatValue("LowTimerSpeed",0.2f,0.01f,1f)

    private val maxHighRange = FloatValue("MaxHighRange",3f,0f,10f)
    private val minHighRange = FloatValue("MinHighRange",2.5f,0f,10f)
    private val maxHighDelay = IntegerValue("MaxHighDelay",5,0,20)
    private val highTimerValue = FloatValue("HighTimerSpeed",0.2f,1f,10f)

    private val onMove = BoolValue("OnMove",true)
    private val onGround = BoolValue("OnGround",false)
    private val forward = BoolValue("Forward",true)
    private val swing = BoolValue("Swing",false)
    private val noLava = BoolValue("NoLava",true)
    private val noWater = BoolValue("NoWater",true)
    private val noWwb = BoolValue("NoWwb",true)

    private var allowTimer = true
    private val lowTime = MSTimer()
    private val highTime = MSTimer()
    override fun onDisable() {
        allowTimer = true
        highTime.reset()
        lowTime.reset()
        mc.timer.timerSpeed = 1.0f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer?:return
        for (entity in mc.theWorld.playerEntities) {
            if (EntityTypeLib.isSelected(entity, true) && entity != null && entity.hurtTime in targetMinHurtTime.get()..targetMaxHurtTime.get()){
                val distance = entity.getDistanceToEntityBox(player)
                if (distance in minRange.get()..maxRange.get()) {
                    if (allowTimer) {
                        if ((!onMove.get() || isMove(mc.thePlayer))&&
                            (!forward.get() || GameSettings.isKeyDown(mc.gameSettings.keyBindForward))&&
                            (!swing.get() || mc.thePlayer.isSwingInProgress)&&
                            (!noLava.get() || !mc.thePlayer.isInLava)&&
                            (!noWater.get() || !mc.thePlayer.isInWater)&&
                            (!noWwb.get() || !mc.thePlayer.isInWeb)&&
                            (!onGround.get() || mc.thePlayer.onGround)) {
                            when (distance) {
                                in minLowRange.get()..maxLowRange.get() -> {
                                    if (lowTime.hasTimePassed(maxLowDelay.get().toLong())) {
                                        mc.timer.timerSpeed = highTimerValue.get()
                                        lowTime.reset()
                                    } else {
                                        mc.timer.timerSpeed = lowTimerValue.get()
                                    }
                                }

                                in minHighRange.get()..maxHighRange.get() -> {
                                    if (!highTime.hasTimePassed(maxHighDelay.get().toLong())) {
                                        mc.timer.timerSpeed = highTimerValue.get()
                                        highTime.reset()
                                    }
                                }

                                in minRange.get()..minHighRange.get() -> {
                                    mc.timer.timerSpeed = 1.0f
                                    allowTimer = false
                                }
                            }
                        }
                    }
                } else {
                    highTime.reset()
                    lowTime.reset()
                    mc.timer.timerSpeed = 1.0f
                    allowTimer = true
                }
            } else {
                highTime.reset()
                lowTime.reset()
            }
        }
    }
}