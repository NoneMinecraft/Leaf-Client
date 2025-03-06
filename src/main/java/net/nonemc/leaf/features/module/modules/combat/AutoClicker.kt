﻿/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemSword
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.misc.RandomUtils
import net.nonemc.leaf.utils.timer.TimeUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import kotlin.random.Random

@ModuleInfo(name = "AutoClicker", category = ModuleCategory.COMBAT)
class AutoClicker : Module() {

    private val maxCPSValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 40) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()
            if (minCPS > newValue) {
                set(minCPS)
            }
        }
    }
    private val minCPSValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 40) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()
            if (maxCPS < newValue) {
                set(maxCPS)
            }
        }
    }
    private val legitJitter = BoolValue("LegitJitterCPS", false)

    private val rightValue = BoolValue("Right", true)
    private val rightBlockOnlyValue = BoolValue("RightBlockOnly", false)
    private val leftValue = BoolValue("Left", true)
    private val leftSwordOnlyValue = BoolValue("LeftSwordOnly", false)
    private val jitterValue = BoolValue("Jitter", false)

    private var rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var rightLastSwing = 0L
    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L


    @EventTarget
    fun onRender(event: Render3DEvent) {
        // Left click
        if (mc.gameSettings.keyBindAttack.isKeyDown && leftValue.get() &&
            System.currentTimeMillis() - leftLastSwing >= leftDelay && (!leftSwordOnlyValue.get() || mc.thePlayer.heldItem?.item is ItemSword) && mc.playerController.curBlockDamageMP == 0F
        ) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis()
            if (legitJitter.get()) {
                if (Random.nextInt(1, 14) <= 3) {
                    if (Random.nextInt(1, 3) == 2) {
                        leftDelay = (Random.nextInt(98, 102)).toLong()
                    } else {
                        leftDelay = (Random.nextInt(114, 117)).toLong()
                    }
                } else {
                    if (Random.nextInt(1, 4) == 1) {
                        leftDelay = (Random.nextInt(64, 68)).toLong()
                    } else {
                        leftDelay = (Random.nextInt(84, 85)).toLong()
                    }
                }
            } else {
                leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
            }
        }

        // Right click
        if (mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem && rightValue.get() &&
            System.currentTimeMillis() - rightLastSwing >= rightDelay &&
            (!rightBlockOnlyValue.get() || mc.thePlayer.heldItem?.item is ItemBlock) && rightValue.get()
        ) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode) // Minecraft Click Handling

            rightLastSwing = System.currentTimeMillis()
            if (legitJitter.get()) {
                if (Random.nextInt(1, 14) <= 3) {
                    if (Random.nextInt(1, 3) == 2) {
                        rightDelay = (Random.nextInt(98, 102)).toLong()
                    } else {
                        rightDelay = (Random.nextInt(114, 117)).toLong()
                    }
                } else {
                    if (Random.nextInt(1, 4) == 1) {
                        rightDelay = (Random.nextInt(64, 68)).toLong()
                    } else {
                        rightDelay = (Random.nextInt(84, 85)).toLong()
                    }
                }
            } else {
                rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (jitterValue.get() && (leftValue.get() && mc.gameSettings.keyBindAttack.isKeyDown || rightValue.get() && mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem)) {
            if (Random.nextBoolean()) mc.thePlayer.rotationYaw += if (Random.nextBoolean()) -RandomUtils.nextFloat(
                0F,
                1F
            ) else RandomUtils.nextFloat(0F, 1F)

            if (Random.nextBoolean()) {
                mc.thePlayer.rotationPitch += if (Random.nextBoolean()) -RandomUtils.nextFloat(
                    0F,
                    1F
                ) else RandomUtils.nextFloat(0F, 1F)

                // Make sure pitch does not go in to blatent values
                if (mc.thePlayer.rotationPitch > 90)
                    mc.thePlayer.rotationPitch = 90F
                else if (mc.thePlayer.rotationPitch < -90)
                    mc.thePlayer.rotationPitch = -90F
            }
        }
    }
}
