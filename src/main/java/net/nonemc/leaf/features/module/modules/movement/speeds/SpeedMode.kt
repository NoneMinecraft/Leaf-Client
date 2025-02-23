/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.nonemc.leaf.features.module.modules.movement.speeds

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.MotionEvent
import net.nonemc.leaf.event.MoveEvent
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.modules.movement.Speed
import net.nonemc.leaf.utils.ClassUtils
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.value.Value

abstract class SpeedMode(val modeName: String) : MinecraftInstance() {
    protected val valuePrefix = "$modeName-"

    protected val speed: Speed
        get() = Leaf.moduleManager[Speed::class.java]!!

    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass, this)

    open fun onEnable() {}
    open fun onDisable() {}

    open fun onPreMotion() {}
    open fun onMotion(event: MotionEvent) {}
    open fun onUpdate() {}
    open fun onMove(event: MoveEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onTick() {}

    open val noJump = false
}
