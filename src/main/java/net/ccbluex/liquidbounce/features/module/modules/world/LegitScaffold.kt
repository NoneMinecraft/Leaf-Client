package net.ccbluex.liquidbounce.features.module.modules.world


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event4.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.render.SilentRotations
import net.ccbluex.liquidbounce.utils4.InventoryUtils
import net.ccbluex.liquidbounce.utils4.MinecraftInstance.mc
import net.ccbluex.liquidbounce.utils4.Rotation
import net.ccbluex.liquidbounce.utils4.RotationUtils
import net.ccbluex.liquidbounce.utils4.misc.RandomUtils
import net.ccbluex.liquidbounce.utils4.render.RenderUtils
import net.ccbluex.liquidbounce.utils4.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard
import java.awt.Color

@ModuleInfo(name = "LegitScaffoldB", category = ModuleCategory.WORLD)
object LegitScaffold : Module() {

    private val sneakValue = BoolValue("AutoSneak", true)
    private val autoSwitchValue = BoolValue("AutoSwitch", true)
    private val safeWalkValue = BoolValue("SafeWalk", true)
    private val stopSprintValue = BoolValue("StopSprint", true)
    private val delayValue = IntegerValue("PlaceDelay", 0, 0, 30)
    private val maxTurnSpeed: FloatValue =
        object : FloatValue("MaxTurnSpeed", 80f, 0f, 180f) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = minTurnSpeed.get()
                if (i > newValue) set(i)
            }
        }
    private val minTurnSpeed: FloatValue =
        object : FloatValue("MinTurnSpeed", 40f, 0f, 180f) {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = maxTurnSpeed.get()
                if (i < newValue) set(i)
            }
        }

    private val tickTimer = TickTimer()
    var lastSlot = 0

    override fun onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (autoSwitchValue.get()) {
            mc.thePlayer.inventory.currentItem = lastSlot
            mc.playerController.updateController()
        }

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            tickTimer.reset()
            mc.gameSettings.keyBindSneak.pressed = true
            return
        }

        if (event.eventState == EventState.POST) {
            try {
                if (autoSwitchValue.get() && mc.thePlayer.inventory.currentItem != mc.thePlayer.inventoryContainer.getSlot(
                        InventoryUtils.findAutoBlockBlock()
                    ).slotIndex
                ) {
                    mc.thePlayer.inventory.currentItem = InventoryUtils.findAutoBlockBlock() - 36
                    mc.playerController.updateController()
                }
            } catch (ignored: Exception) {
            }
        }

        tickTimer.update()

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if (shouldEagle && (tickTimer.hasTimePassed(delayValue.get()) || !mc.thePlayer.onGround)) {
            if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemBlock)
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            tickTimer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            tickTimer.reset()
            mc.gameSettings.keyBindSneak.pressed = true
            return
        }

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if (sneakValue.get() && shouldEagle || GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = true
        else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false

        if (stopSprintValue.get())
            mc.thePlayer.isSprinting = false

        RotationUtils.setTargetRotation(
            RotationUtils.limitAngleChange(
                RotationUtils.serverRotation!!,
                Rotation(
                    RotationUtils.cameraYaw - 180f,
                    (if (!mc.thePlayer.isSneaking) 80.4f else 80f) + if (mc.thePlayer.isSprinting) 0.2f else 0.0f
                ),
                RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
            )
        )
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ),
            Color(255, 255, 255, 40),
            false
        )
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!LiquidBounce.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!)
            event.yaw = RotationUtils.serverRotation?.yaw!! - 180f
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (!LiquidBounce.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!)
            event.yaw = RotationUtils.serverRotation?.yaw!! - 180f
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.onGround && safeWalkValue.get())
            event.isSafeWalk = true
    }
}