/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.movement.speeds.SpeedMode
import net.nonemc.leaf.utils.misc.ClassUtils
import net.nonemc.leaf.utils.entity.MovementUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.ListValue
import org.lwjgl.input.Keyboard

@ModuleInfo(
    name = "Speed",
    category = ModuleCategory.MOVEMENT,
    autoDisable = EnumAutoDisableType.FLAG,
    keyBind = Keyboard.KEY_V
)
class Speed : Module() {
    private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.speeds", SpeedMode::class.java)
        .map { it.newInstance() as SpeedMode }
        .sortedBy { it.modeName }

    private val mode: SpeedMode
        get() = modes.find { modeValue.equals(it.modeName) } ?: throw NullPointerException() // this should not happen

    private val modeValue: ListValue = object : ListValue("Mode", modes.map { it.modeName }.toTypedArray(), "NCPBhop") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    private val noWater = BoolValue("NoWater", true)
    private val debug = BoolValue("Debug", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.isSneaking || (mc.thePlayer.isInWater && noWater.get())) return
        if (MovementUtils.isMoving()) mc.thePlayer.isSprinting = true
        mode.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (debug.get()) {
            alert("Speed: " + MovementUtils.getSpeed().toString())
            alert("YMotion: " + mc.thePlayer.motionY.toString())
        }

        if (MovementUtils.isMoving()) {
            mc.thePlayer.isSprinting = true
        }

        mode.onMotion(event)

        if (mc.thePlayer.isSneaking || event.eventState !== EventState.PRE || (mc.thePlayer.isInWater && noWater.get())) {
            return
        }

        mode.onPreMotion()
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isSneaking || (mc.thePlayer.isInWater && noWater.get())) {
            return
        }
        mode.onMove(event)
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (mc.thePlayer.isSneaking || (mc.thePlayer.isInWater && noWater.get())) {
            return
        }

        mode.onTick()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mode.onPacket(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mode.noJump) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return
        mc.timer.timerSpeed = 1f
        mode.onEnable()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        mc.timer.timerSpeed = 1f
        mode.onDisable()
    }

    override val tag: String
        get() = modeValue.get()


    override val values = super.values.toMutableList()
        .also { modes.map { mode -> mode.values.forEach { value -> it.add(value.displayable { modeValue.equals(mode.modeName) }) } } }
}
