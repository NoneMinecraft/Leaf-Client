﻿/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.player

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S09PacketHeldItemChange
import net.minecraft.potion.Potion
import net.minecraft.util.MathHelper
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.event.WorldEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import java.util.*

@ModuleInfo(name = "Gapple", category = ModuleCategory.PLAYER)
class Gapple : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Auto", "LegitAuto", "Head"), "Auto")
    private val percent = FloatValue("HealthPercent", 75.0f, 1.0f, 100.0f)
    private val min = IntegerValue("MinDelay", 75, 1, 5000)
    private val max = IntegerValue("MaxDelay", 125, 1, 5000)
    private val regenSec = FloatValue("MinRegenSec", 4.6f, 0.0f, 10.0f)
    private val groundCheck = BoolValue("OnlyOnGround", false)
    private val waitRegen = BoolValue("WaitRegen", true)
    private val invCheck = BoolValue("InvCheck", false)
    private val absorpCheck = BoolValue("NoAbsorption", true)
    val timer = MSTimer()
    private var eating = -1
    var delay = 0
    var isDisable = false
    var tryHeal = false
    override fun onEnable() {
        eating = -1
        timer.reset()
        isDisable = false
        tryHeal = false
        delay = MathHelper.getRandomIntegerInRange(Random(), min.get(), max.get())
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        isDisable = true
        tryHeal = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (eating != -1 && packet is C03PacketPlayer) {
            eating++
        } else if (packet is S09PacketHeldItemChange || packet is C09PacketHeldItemChange) {
            eating = -1
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (tryHeal) {
            when (modeValue.get().lowercase()) {
                "auto" -> {
                    val gappleInHotbar = findItem(36, 45, Items.golden_apple)
                    if (gappleInHotbar != -1) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(gappleInHotbar - 36))
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                        repeat(35) {
                            mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                        }
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        alert("Gapple eaten")
                        tryHeal = false
                        timer.reset()
                        delay = MathHelper.getRandomIntegerInRange(Random(), min.get(), max.get())
                    } else {
                        tryHeal = false
                    }
                }

                "legitauto" -> {
                    if (eating == -1) {
                        val gappleInHotbar = findItem(36, 45, Items.golden_apple)
                        if (gappleInHotbar == -1) {
                            tryHeal = false
                            return
                        }
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(gappleInHotbar - 36))
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                        eating = 0
                    } else if (eating > 35) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        timer.reset()
                        tryHeal = false
                        delay = MathHelper.getRandomIntegerInRange(Random(), min.get(), max.get())
                    }
                }

                "head" -> {
                    val headInHotbar = findItem(36, 45, Items.skull)
                    if (headInHotbar != -1) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(headInHotbar - 36))
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        timer.reset()
                        tryHeal = false
                        delay = MathHelper.getRandomIntegerInRange(Random(), min.get(), max.get())
                    } else {
                        tryHeal = false
                    }
                }
            }
        }
        if (mc.thePlayer.ticksExisted <= 10 && isDisable) {
            isDisable = false
        }
        val absorp = MathHelper.ceiling_double_int(mc.thePlayer.absorptionAmount.toDouble())
        if ((groundCheck.get() && !mc.thePlayer.onGround) || (invCheck.get() && mc.currentScreen is GuiContainer) || (absorp > 0 && absorpCheck.get()))
            return
        if (waitRegen.get() && mc.thePlayer.isPotionActive(Potion.regeneration) && mc.thePlayer.getActivePotionEffect(
                Potion.regeneration
            ).duration > regenSec.get() * 20.0f
        )
            return
        if (!isDisable && (mc.thePlayer.health <= (percent.get() / 100.0f) * mc.thePlayer.maxHealth) && timer.hasTimePassed(
                delay.toLong()
            )
        ) {
            if (tryHeal)
                return
            tryHeal = true
        }
    }

    fun findItem(startSlot: Int, endSlot: Int, item: Item): Int {
        for (i in startSlot until endSlot) {
            val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
            if (stack != null && stack.item === item) {
                return i
            }
        }
        return -1
    }

    override val tag: String
        get() = modeValue.get()
} 
