package net.nonemc.leaf.features.module.modules.rage

import net.minecraft.client.settings.GameSettings
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.MainLib.ChatPrint
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import org.lwjgl.input.Keyboard
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "MovementEditor", category = ModuleCategory.COMBAT)
class MovementEditor : Module() {
    private val baseController = ListValue("BaseController", arrayOf("Linear-OpenLoop"),"Linear-OpenLoop")
    private val enableQuickAdjustMode = BoolValue("EnableQuickAdjustMode", true)
    private val quickAdjustModeStep = IntegerValue("QuickAdjustModeStep", 10, 1, 500)
    private val stepValue = IntegerValue("StepTimerValue", 10, 1, 500)
    private val tYaw = FloatValue("Yaw", 10f, -180f, 180f)
    private val keyUP = Keyboard.KEY_UP
    private val keyDOWN = Keyboard.KEY_DOWN
    private val keyW = Keyboard.KEY_U //W
    private val keyS = Keyboard.KEY_J //S
    private val keyA = Keyboard.KEY_H //A
    private val keyD = Keyboard.KEY_K //D
    private val WTimer = MSTimer()
    private val STimer = MSTimer()
    private val ATimer = MSTimer()
    private val DTimer = MSTimer()
    private var baseTimer = 0
    private var run = false
    private var wmove = false
    private var smove = false
    private var amove = false
    private var dmove = false
    private var wBackMove = false
    private var sBackMove = false
    private var aBackMove = false
    private var dBackMove = false
    override fun onDisable() {
        run = false
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        moveAtYaw(tYaw.get())
        if (enableQuickAdjustMode.get()) {
            if (mc.currentScreen == null) {
                if (Keyboard.isKeyDown(keyUP)){
                    baseTimer += quickAdjustModeStep.get()
                    ChatPrint("[+]移动时间已被设为:$baseTimer")
                }
                if (Keyboard.isKeyDown(keyDOWN)){
                    baseTimer -= quickAdjustModeStep.get()
                    ChatPrint("[-]移动时间已被设为:$baseTimer")
                }
            }
        }else{
            baseTimer = stepValue.get()
        }
        when (baseController.get()){
            "Linear-OpenLoop" -> {
                // W方向处理
                if (Keyboard.isKeyDown(keyW)) {
                    wmove = true
                    WTimer.reset()
                }

                // S方向处理
                if (Keyboard.isKeyDown(keyS)) {
                    smove = true
                    STimer.reset()
                }

                // A方向处理
                if (Keyboard.isKeyDown(keyA)) {
                    amove = true
                    ATimer.reset()
                }

                // D方向处理
                if (Keyboard.isKeyDown(keyD)) {
                    dmove = true
                    DTimer.reset()
                }

                // 处理W移动
                if (wmove) {
                    if (WTimer.hasTimePassed(baseTimer.toLong())) {
                        if (!wBackMove) {
                            wBackMove = true
                            WTimer.reset()
                        } else {
                            wmove = false
                            wBackMove = false
                        }
                    } else {
                        if (wBackMove) {
                            mc.gameSettings.keyBindBack.pressed = true
                            mc.gameSettings.keyBindForward.pressed = false
                        } else {
                            mc.gameSettings.keyBindForward.pressed = true
                            mc.gameSettings.keyBindBack.pressed = false
                        }
                    }
                }else if (!smove && !amove && !dmove){
                    mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
                }

                // 处理S移动（反向逻辑）
                if (smove) {
                    if (STimer.hasTimePassed(baseTimer.toLong())) {
                        if (!sBackMove) {
                            sBackMove = true
                            STimer.reset()
                        } else {
                            smove = false
                            sBackMove = false
                        }
                    } else {
                        if (sBackMove) {
                            mc.gameSettings.keyBindForward.pressed = true // S的反向是向前
                            mc.gameSettings.keyBindBack.pressed = false
                        } else {
                            mc.gameSettings.keyBindBack.pressed = true // S的正向是向后
                            mc.gameSettings.keyBindForward.pressed = false
                        }
                    }
                }else if (!wmove && !amove && !dmove){
                    mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                }

                // 处理A移动（横向移动）
                if (amove) {
                    if (ATimer.hasTimePassed(baseTimer.toLong())) {
                        if (!aBackMove) {
                            aBackMove = true
                            ATimer.reset()
                        } else {
                            amove = false
                            aBackMove = false
                        }
                    } else {
                        if (aBackMove) {
                            mc.gameSettings.keyBindRight.pressed = true // A的反向是向右
                            mc.gameSettings.keyBindLeft.pressed = false
                        } else {
                            mc.gameSettings.keyBindLeft.pressed = true // A的正向是向左
                            mc.gameSettings.keyBindRight.pressed = false
                        }
                    }
                }else if (!smove && !wmove && !dmove){
                    mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
                }

                // 处理D移动（横向移动）
                if (dmove) {
                    if (DTimer.hasTimePassed(baseTimer.toLong())) {
                        if (!dBackMove) {
                            dBackMove = true
                            DTimer.reset()
                        } else {
                            dmove = false
                            dBackMove = false
                        }
                    } else {
                        if (dBackMove) {
                            mc.gameSettings.keyBindLeft.pressed = true // D的反向是向左
                            mc.gameSettings.keyBindRight.pressed = false
                        } else {
                            mc.gameSettings.keyBindRight.pressed = true // D的正向是向右
                            mc.gameSettings.keyBindLeft.pressed = false
                        }
                    }
                }else if (!smove && !amove && !wmove){
                    mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
                }
            }
        }
    }
    fun moveAtYaw(targetYaw: Float) {
        // 角度规范化处理
        val normalizedYaw = (targetYaw % 360).let {
            if (it < 0) it + 360 else it
        }

        // 计算方向分量
        val yawRad = Math.toRadians(normalizedYaw.toDouble())
        val forward = -sin(yawRad).toFloat() // Z轴方向
        val strafe = cos(yawRad).toFloat()    // X轴方向

        // 设置按键状态
        with(mc.gameSettings) {
            // 前后方向
            keyBindForward.pressed = forward > 0.001
            keyBindBack.pressed = forward < -0.001

            // 左右方向
            keyBindRight.pressed = strafe > 0.001
            keyBindLeft.pressed = strafe < -0.001
        }

        // 当分量接近零时自动停止
        if (abs(forward) < 0.001 && abs(strafe) < 0.001) {
            resetMovementKeys()
        }
    }

    private fun resetMovementKeys() {
        with(mc.gameSettings) {
            keyBindForward.pressed = false
            keyBindBack.pressed = false
            keyBindLeft.pressed = false
            keyBindRight.pressed = false
        }
    }
}