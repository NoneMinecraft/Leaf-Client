﻿package net.nonemc.leaf.features.module.modules.player

import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.player.phases.PhaseMode
import net.nonemc.leaf.libs.clazz.ClassReflect
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "Phase", category = ModuleCategory.PLAYER)
object Phase : Module() {

    private val modes = ClassReflect.resolvePackage("${this.javaClass.`package`.name}.phases", PhaseMode::class.java)
        .map { it.newInstance() as PhaseMode }
        .sortedBy { it.modeName }

    private val mode: PhaseMode
        get() = modes.find { modeValue.equals(it.modeName) } ?: throw NullPointerException() // this should not happen

    private val modeValue: ListValue = object : ListValue("Mode", modes.map { it.modeName }.toTypedArray(), "Vanilla") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    private val debugValue = BoolValue("Debug", false)

    override fun onEnable() {
        mode.onEnable()
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
        mode.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mode.onUpdate(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        mode.onMotion(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mode.onPacket(event)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        mode.onMove(event)
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mode.onBlockBB(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        mode.onJump(event)
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        mode.onStep(event)
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        mode.onWorld(event)
    }

    @EventTarget
    fun onPushOut(event: PushOutEvent) {
        event.cancelEvent()
    }

    override val tag: String
        get() = modeValue.get()

    override val values = super.values.toMutableList().also {
        modes.map { mode ->
            mode.values.forEach { value ->
                it.add(value.displayable { modeValue.equals(mode.modeName) })
            }
        }
    }
}