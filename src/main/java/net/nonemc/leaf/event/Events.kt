package net.nonemc.leaf.event

import net.minecraft.block.Block
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class AttackEvent(val targetEntity: Entity) : CancellableEvent()

class EntityKilledEvent(val targetEntity: EntityLivingBase) : Event()

class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
    val x = blockPos.x
    val y = blockPos.y
    val z = blockPos.z
}

class ClickBlockEvent(val clickedBlock: BlockPos?, val enumFacing: EnumFacing?) : Event()

class ClientShutdownEvent : Event()

class JumpEvent(var motion: Float) : CancellableEvent()

class KeyEvent(val key: Int) : Event()

class MotionEvent(val eventState: EventState) : Event() {
    fun isPre() : Boolean {
    return eventState == EventState.PRE
    }
}
class GameLoopEvent : Event()
data class EntityMovementEvent(val movedEntity: Entity) : Event()

class UpdateModelEvent(val player: EntityPlayer, val model: ModelPlayer) : Event()

class EntityDamageEvent(val damagedEntity: Entity): Event()

class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

class StrafeEvent(val strafe: Float, val forward: Float, val friction: Float) : CancellableEvent()

class MoveEvent(var x: Double, var y: Double, var z: Double) : CancellableEvent() {
    var isSafeWalk = false

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

class PacketEvent(val packet: Packet<*>, val type: Type) : CancellableEvent() {
    enum class Type {
        RECEIVE,
        SEND
    }

    fun isServerSide() = type == Type.RECEIVE
}

class PushOutEvent : CancellableEvent()

class Render2DEvent(val partialTicks: Float, val scaledResolution: ScaledResolution) : Event()

class Render3DEvent(val partialTicks: Float) : Event()

class ScreenEvent(val guiScreen: GuiScreen?) : Event()

class SessionEvent : Event()

class StepEvent(var stepHeight: Float, val eventState: EventState) : Event()

class TextEvent(var text: String?) : Event()

class TickEvent : Event()

class UpdateEvent : Event()

class WorldEvent(val worldClient: WorldClient?) : Event()

class ClickWindowEvent(val windowId: Int, val slotId: Int, val mouseButtonClicked: Int, val mode: Int) : CancellableEvent()
