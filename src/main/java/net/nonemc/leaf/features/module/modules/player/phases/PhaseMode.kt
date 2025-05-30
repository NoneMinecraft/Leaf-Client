﻿package net.nonemc.leaf.features.module.modules.player.phases


import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.modules.player.Phase
import net.nonemc.leaf.libs.clazz.ClassReflect
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.value.Value

abstract class PhaseMode(val modeName: String) : MinecraftInstance() {
    protected val valuePrefix = "$modeName-"

    protected val phase: Phase
        get() = Leaf.moduleManager[Phase::class.java]!!

    open val values: List<Value<*>>
        get() = ClassReflect.getValues(this.javaClass, this)

    open fun onEnable() {}
    open fun onDisable() {}

    open fun onUpdate(event: UpdateEvent) {}
    open fun onWorld(event: WorldEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMove(event: MoveEvent) {}
    open fun onBlockBB(event: BlockBBEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onStep(event: StepEvent) {}
}