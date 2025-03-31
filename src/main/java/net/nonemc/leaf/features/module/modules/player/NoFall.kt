/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.player

import net.minecraft.block.BlockLiquid
import net.minecraft.util.AxisAlignedBB
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.EnumAutoDisableType
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.features.module.modules.player.nofalls.NoFallMode
import net.nonemc.leaf.features.module.modules.render.FreeCam
import net.nonemc.leaf.utils.misc.ClassUtils
import net.nonemc.leaf.utils.block.BlockUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "NoFall", category = ModuleCategory.PLAYER, autoDisable = EnumAutoDisableType.FLAG)
class NoFall : Module() {
    private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.nofalls", NoFallMode::class.java)
        .map { it.newInstance() as NoFallMode }
        .sortedBy { it.modeName }

    val mode: NoFallMode
        get() = modes.find { modeValue.equals(it.modeName) } ?: throw NullPointerException()

    private val modeValue: ListValue = object : ListValue("Mode", modes.map { it.modeName }.toTypedArray(), "Vanilla") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    private val noVoid = BoolValue("NoVoid", false)

    var launchX = 0.0
    var launchY = 0.0
    var launchZ = 0.0
    var launchYaw = 0f
    var launchPitch = 0f

    var antiDesync = false

    var needReset = true

    var wasTimer = false

    override fun onEnable() {
        needReset = true
        launchX = mc.thePlayer.posX
        launchY = mc.thePlayer.posY
        launchZ = mc.thePlayer.posZ
        launchYaw = mc.thePlayer.rotationYaw
        launchPitch = mc.thePlayer.rotationPitch
        wasTimer = false

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
        if (wasTimer) {
            mc.timer.timerSpeed = 1.0f
            wasTimer = false
        }
        mode.onUpdate(event)
        if (!state || Leaf.moduleManager[FreeCam::class.java]!!.state) {
            return
        }

        if (mc.thePlayer.isSpectator || mc.thePlayer.capabilities.allowFlying || mc.thePlayer.capabilities.disableDamage) {
            return
        }

        if (BlockUtils.collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } || BlockUtils.collideBlock(
                AxisAlignedBB(
                    mc.thePlayer.entityBoundingBox.maxX,
                    mc.thePlayer.entityBoundingBox.maxY,
                    mc.thePlayer.entityBoundingBox.maxZ,
                    mc.thePlayer.entityBoundingBox.minX,
                    mc.thePlayer.entityBoundingBox.minY - 0.01,
                    mc.thePlayer.entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }) {
            return
        }

        if (checkVoid() && noVoid.get()) return

        mode.onNoFall(event)
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

    override val tag: String
        get() = modeValue.get()

    private fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld.getCollisionBoxes(
                mc.thePlayer.entityBoundingBox.offset(
                    mc.thePlayer.motionX * 0.5,
                    i.toDouble(),
                    mc.thePlayer.motionZ * 0.5
                )
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }


    /**
     * 读取mode中的value并和本体中的value合并
     * 所有的value必须在这个之前初始化
     */
    override val values = super.values.toMutableList()
        .also { modes.map { mode -> mode.values.forEach { value -> it.add(value.displayable { modeValue.equals(mode.modeName) }) } } }
}
