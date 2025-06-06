﻿/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.movement.longjumps.LongJumpMode
import net.nonemc.leaf.libs.clazz.ClassReflect
import net.nonemc.leaf.libs.entity.EntityMoveLib
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "LongJump", category = ModuleCategory.MOVEMENT, autoDisable = EnumAutoDisableType.FLAG)
class LongJump : Module() {
    private val modes =
        ClassReflect.resolvePackage("${this.javaClass.`package`.name}.longjumps", LongJumpMode::class.java)
            .map { it.newInstance() as LongJumpMode }
            .sortedBy { it.modeName }

    private val mode: LongJumpMode
        get() = modes.find { modeValue.equals(it.modeName) } ?: throw NullPointerException()

    val modeValue: ListValue = object : ListValue("Mode", modes.map { it.modeName }.toTypedArray(), "NCP") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    val autoJumpValue = BoolValue("AutoJump", true)
    val autoDisableValue = BoolValue("AutoDisable", true)
    var jumped = false
    var hasJumped = false
    var no = false

    override fun onEnable() {
        jumped = false
        hasJumped = false
        no = false
        mode.onEnable()
    }

    override fun onDisable() {
        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.capabilities.flySpeed = 0.05f
        mc.thePlayer.noClip = false
        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = 0.02F
        mode.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!state) return
        mode.onUpdate(event)
        if (!no && autoJumpValue.get() && mc.thePlayer.onGround && EntityMoveLib.isMoving()) {
            jumped = true
            if (hasJumped && autoDisableValue.get()) {
                state = false
                return
            }
            mc.thePlayer.jump()
            hasJumped = true
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!state) return
        mode.onMotion(event)
        if (event.eventState != EventState.PRE) return
        mode.onPreMotion(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!state) return
        mode.onPacket(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!state) return
        mode.onMove(event)
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (!state) return
        mode.onBlockBB(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (!state) return
        mode.onJump(event)
        jumped = true
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        if (!state) return
        mode.onStep(event)
    }

    override val tag: String
        get() = modeValue.get()
    override val values = super.values.toMutableList()
        .also { modes.map { mode -> mode.values.forEach { value -> it.add(value.displayable { modeValue.equals(mode.modeName) }) } } }
}