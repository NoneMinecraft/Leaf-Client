package net.nonemc.leaf.features.module.modules.player.nofalls

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.modules.player.NoFall
import net.nonemc.leaf.utils.misc.ClassUtils
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.value.Value

abstract class NoFallMode(val modeName: String) : MinecraftInstance() {
    protected val valuePrefix = "$modeName-"

    protected val nofall: NoFall
        get() = Leaf.moduleManager[NoFall::class.java]!!

    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass, this)

    open fun onEnable() {}
    open fun onDisable() {}

    open fun onUpdate(event: UpdateEvent) {}
    open fun onNoFall(event: UpdateEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onMove(event: MoveEvent) {}
    open fun onBlockBB(event: BlockBBEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onStep(event: StepEvent) {}
}