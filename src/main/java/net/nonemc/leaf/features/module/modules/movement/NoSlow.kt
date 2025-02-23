
package net.nonemc.leaf.features.module.modules.movement

import net.nonemc.leaf.event.*
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.MovementUtils
import net.nonemc.leaf.utils.PacketUtils
import net.nonemc.leaf.utils.timer.MSTimer
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S09PacketHeldItemChange
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.network.play.server.S30PacketWindowItems
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*
import kotlin.math.sqrt

@ModuleInfo(name = "NoSlow", category = ModuleCategory.MOVEMENT)
object NoSlow : Module() {

    val mode = arrayOf(
        "Vanilla",
        "Custom",
        "Bug",
        "UNCP",
        "NCP",
        "AAC5",
        "SwitchItem",
    )
    private val modeValue = ListValue(
        "PacketMode",
        mode,
        "Vanilla"
    )
    private val antiSwitchItem = BoolValue("AntiSwitchItem", false)
    private val onlyGround = BoolValue("OnlyGround", false)
    private val onlyMove = BoolValue("OnlyMove", false)

    //Modify Slowdown / Packets
    private val blockModifyValue = BoolValue("Blocking", true)
    private val blockForwardMultiplier =
        FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F).displayable { blockModifyValue.get() }
    private val blockStrafeMultiplier =
        FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F).displayable { blockModifyValue.get() }
    private val consumeModifyValue = BoolValue("Consume", true)
    private val consumePacketValue = ListValue(
        "ConsumePacket",
        arrayOf("None", "AAC5","UNCP","Bug","Packet"),
        "None"
    ).displayable { consumeModifyValue.get() }
    private val conmode = ListValue("BugMode", arrayOf("C07","C16"),"C07").displayable { consumePacketValue.equals("Bug")}
    private val consumeTimingValue =
        ListValue("ConsumeTiming", arrayOf("Pre", "Post"), "Pre").displayable { consumeModifyValue.get() }
    private val consumeForwardMultiplier =
        FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F).displayable { consumeModifyValue.get() }
    private val consumeStrafeMultiplier =
        FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F).displayable { consumeModifyValue.get() }
    private val bowModifyValue = BoolValue("Bow", true)
    private val bowPacketValue = ListValue(
        "BowPacket",
        arrayOf("None", "AAC5", "SpamItemChange", "SpamPlace", "SpamEmptyPlace","UNCP", "Glitch", "Grim","InvalidC08", "Packet"),
        "None"
    ).displayable { bowModifyValue.get() }
    private val bowTimingValue =
        ListValue("BowTiming", arrayOf("Pre", "Post"), "Pre").displayable { bowModifyValue.get() }
    private val bowForwardMultiplier =
        FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F).displayable { bowModifyValue.get() }
    private val bowStrafeMultiplier =
        FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F).displayable { bowModifyValue.get() }
    private val customOnGround = BoolValue("CustomOnGround", false).displayable { modeValue.equals("Custom") }
    private val customDelayValue = IntegerValue("CustomDelay", 60, 10, 200).displayable { modeValue.equals("Custom") }
    val soulsandValue = BoolValue("SoulSand", true)

    //AACv4
    private val c07Value = BoolValue("AAC4-C07", true).displayable { modeValue.equals("AAC4") }
    private val c08Value = BoolValue("AAC4-C08", true).displayable { modeValue.equals("AAC4") }
    private val groundValue = BoolValue("AAC4-OnGround", true).displayable { modeValue.equals("AAC4") }

    // Slowdown on teleport
    private val teleportValue = BoolValue("Teleport", false)
    private val teleportModeValue = ListValue(
        "TeleportMode",
        arrayOf("Vanilla", "VanillaNoSetback", "Custom", "Decrease"),
        "Vanilla"
    ).displayable { teleportValue.get() }
    private val teleportNoApplyValue = BoolValue("TeleportNoApply", false).displayable { teleportValue.get() }
    private val teleportCustomSpeedValue = FloatValue("Teleport-CustomSpeed", 0.13f, 0f, 1f).displayable {
        teleportValue.get() && teleportModeValue.equals("Custom")
    }
    private val teleportCustomYValue =
        BoolValue("Teleport-CustomY", false).displayable { teleportValue.get() && teleportModeValue.equals("Custom") }
    private val teleportDecreasePercentValue = FloatValue(
        "Teleport-DecreasePercent",
        0.13f,
        0f,
        1f
    ).displayable { teleportValue.get() && teleportModeValue.equals("Decrease") }

    private var pendingFlagApplyPacket = false
    private var lastMotionX = 0.0
    private var lastMotionY = 0.0
    private var lastMotionZ = 0.0
    private val msTimer = MSTimer()
    private val matrixcheck = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var sendPacket = false
    private var lastBlockingStat = false
    private var eatslow = false
    // bug
    private var bugt = 0
    private var start = false
    private var stop = false
    private var mstimer2 = MSTimer()
    //hypixel
    private var postPlace = false
    //UNCP
    private var shouldSwap = false

    override fun onEnable() {
        start = false
        stop = false
        mstimer2.reset()
    }

    override fun onDisable() {
        shouldSwap = false
        bugt = 0
        matrixcheck.reset()
        msTimer.reset()
        eatslow = false
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun sendPacket(
        event: MotionEvent,
        sendC07: Boolean,
        sendC08: Boolean,
        delay: Boolean,
        delayValue: Long,
        onGround: Boolean,
        watchDog: Boolean = false
    ) {
        val digging = C07PacketPlayerDigging(
            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
            BlockPos(-1, -1, -1),
            EnumFacing.DOWN
        )
        val blockPlace = C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
        val blockMent = C08PacketPlayerBlockPlacement(
            BlockPos(-1, -1, -1),
            255,
            mc.thePlayer.inventory.getCurrentItem(),
            0f,
            0f,
            0f
        )
        if (onGround && !mc.thePlayer.onGround) {
            return
        }
        if (sendC07 && event.eventState == EventState.PRE) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && event.eventState == EventState.POST) {
            if (delay && msTimer.hasTimePassed(delayValue) && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
                msTimer.reset()
            } else if (!delay && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if (watchDog) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }

    private fun sendPacket2(packetType: String) {
        val isUsingItem = usingItemFunc()
        when (packetType.lowercase()) {
            "aac5" -> {
                mc.netHandler.addToSendQueue(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(-1, -1, -1),
                        255,
                        mc.thePlayer.inventory.getCurrentItem(),
                        0f,
                        0f,
                        0f
                    )
                )
            }
            "spamitemchange" -> {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }

            "spamplace" -> {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
            }

            "spamemptyplace" -> {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement())
            }

            "glitch" -> {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
            "Intave" -> {
                if (start) PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,BlockPos.ORIGIN,EnumFacing.UP))
            }
            "uncp" -> {
                if (start && (shouldSwap)) {
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                    PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
                    shouldSwap = false
                }
            }

            "grim" -> {
                val handle = mc.thePlayer.inventory.currentItem
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle % 8 + 1))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle % 7 + 2))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle))
            }

            "packet" -> {
                null
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return

        if ((!MovementUtils.isMoving() && onlyMove.get()) || (onlyGround.get() && !mc.thePlayer.onGround)) {
            return
        }
        val heldItem = mc.thePlayer.heldItem?.item
        if (event.eventState == EventState.POST && modeValue.equals("Hypixel") && postPlace) {
            if (mc.thePlayer.ticksExisted % 3 == 0) {
                mc.thePlayer.sendQueue.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }
            postPlace = false
        }
        start = event.eventState == EventState.PRE
        if (consumeModifyValue.get() && mc.thePlayer.isUsingItem && (heldItem is ItemFood || heldItem is ItemPotion || heldItem is ItemBucketMilk)) {
            if ((consumeTimingValue.equals("Pre") && event.eventState == EventState.PRE) || (consumeTimingValue.equals("Post") && event.eventState == EventState.POST)) {
                if (!consumePacketValue.equals("Bug")) {
                    sendPacket2(consumePacketValue.get())
                }
                if (consumePacketValue.equals("Bug")) {
                    if (conmode.equals("C07")) {
                        if (mc.thePlayer.heldItem.item is ItemPotion && mc.thePlayer.heldItem.item is ItemBucketMilk) {
                            return
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.DROP_ITEM,
                                BlockPos(0, 0, 0),
                                EnumFacing.DOWN
                            )
                        )
                    } else {
                        mc.netHandler.addToSendQueue(C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                    }
                    mc.gameSettings.keyBindUseItem.pressed = false
                    mc.thePlayer.stopUsingItem()
                    mstimer2.reset()
                }
            }
        }

        if (bowModifyValue.get() && mc.thePlayer.isUsingItem && heldItem is ItemBow) {
            if ((bowTimingValue.equals("Pre") && event.eventState == EventState.PRE) || (bowTimingValue.equals("Post") && event.eventState == EventState.POST)) {
                sendPacket2(bowPacketValue.get())
            }
        }

        if ((blockModifyValue.get() && (mc.thePlayer.isBlocking) && heldItem is ItemSword)
            || (bowModifyValue.get() && mc.thePlayer.isUsingItem && heldItem is ItemBow && bowPacketValue.equals("Packet"))
            || (consumeModifyValue.get() && mc.thePlayer.isUsingItem && (heldItem is ItemFood || heldItem is ItemPotion || heldItem is ItemBucketMilk) && consumePacketValue.equals(
                "Packet"
            ))
        ) {
            when (modeValue.get().lowercase()) {
                "leaf" -> {
                    sendPacket(event, sendC07 = true, sendC08 = true, delay = false, delayValue = 0, onGround = false)
                }

                "aac" -> {
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        sendPacket(
                            event,
                            sendC07 = true,
                            sendC08 = false,
                            delay = false,
                            delayValue = 0,
                            onGround = false
                        )
                    } else if (mc.thePlayer.ticksExisted % 3 == 1) {
                        sendPacket(
                            event,
                            sendC07 = false,
                            sendC08 = true,
                            delay = false,
                            delayValue = 0,
                            onGround = false
                        )
                    }
                }

                "aac4" -> {
                    sendPacket(event, c07Value.get(), c08Value.get(), true, 80, groundValue.get())
                }

                "aac5" -> {
                    if (event.eventState == EventState.POST) {
                        mc.netHandler.addToSendQueue(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1),
                                255,
                                mc.thePlayer.inventory.getCurrentItem(),
                                0f,
                                0f,
                                0f
                            )
                        )
                    }
                }
                "UNCP" -> {
                    if (event.eventState == EventState.POST && usingItemFunc()) {
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                        PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.heldItem, 0f, 0f, 0f))
                        PacketUtils.sendPacketNoEvent(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1), 255, mc.thePlayer.heldItem, 0f, 0f, 0f
                            )
                        )
                    }
                }

                "custom" -> {
                    sendPacket(
                        event,
                        sendC07 = true,
                        sendC08 = true,
                        delay = true,
                        delayValue = customDelayValue.get().toLong(),
                        onGround = customOnGround.get()
                    )
                }

                "ncp" -> {
                    sendPacket(event, sendC07 = true, sendC08 = true, delay = false, delayValue = 0, onGround = false)
                }

                "watchdog2" -> {
                    if (event.eventState == EventState.PRE) {
                        mc.netHandler.addToSendQueue(
                            C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                BlockPos.ORIGIN,
                                EnumFacing.DOWN
                            )
                        )
                    } else {
                        mc.netHandler.addToSendQueue(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1),
                                255,
                                null,
                                0.0f,
                                0.0f,
                                0.0f
                            )
                        )
                    }
                }
                "bug" -> {
                    mc.netHandler.addToSendQueue(C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                    mc.thePlayer.stopUsingItem()
                    mc.thePlayer.closeScreen()
                }
                "intavesword" -> {
                    if (start) PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                }
                "invalidc08" -> {
                    val heldItem = mc.thePlayer.heldItem
                    if (event.eventState == EventState.PRE) {
                        // Food Only
                        if (heldItem.item is ItemPotion || heldItem.item is ItemBucketMilk) {
                            return
                        }

                        if (getEmptySlot() != -1) {
                            if (mc.thePlayer.ticksExisted % 3 == 0)
                                PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 1, null, 0f, 0f, 0f))
                        }
                    }
                }

                "hypixel" -> {
                    postPlace = false
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        PacketUtils.sendPacketNoEvent(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1),
                                EnumFacing.UP.index,
                                null,
                                0.0f,
                                0.0f,
                                0.0f
                            )
                        )
                    }
                }

                "watchdog" -> {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        sendPacket(event, true, sendC08 = false, delay = true, delayValue = 50, onGround = true)
                    } else {
                        sendPacket(
                            event,
                            sendC07 = false,
                            sendC08 = true,
                            delay = false,
                            delayValue = 0,
                            onGround = true,
                            watchDog = true
                        )
                    }
                }

                "oldintave" -> {
                    if (mc.thePlayer.isUsingItem) {
                        if (event.eventState == EventState.PRE) {
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                        }
                        if (event.eventState == EventState.POST) {
                            mc.netHandler.addToSendQueue(
                                C08PacketPlayerBlockPlacement(
                                    mc.thePlayer.inventoryContainer.getSlot(
                                        mc.thePlayer.inventory.currentItem + 36
                                    ).stack
                                )
                            )
                        }
                    }
                }
                "grimc09" -> {
                    val handle = mc.thePlayer.inventory.currentItem
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle % 8 + 1))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle % 7 + 2))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(handle))
                }

                "switchitem" -> {
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                }

                "hypixelnew" -> {
                    if (getEmptySlot() != -1 && event.eventState == EventState.PRE && mc.thePlayer.ticksExisted % 3 != 0) {
                        mc.netHandler.addToSendQueue(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1),
                                255,
                                null,
                                0f,
                                0f,
                                0f
                            )
                        )
                    }
                }

                "spamitemchange" -> {
                    if (event.eventState == EventState.PRE)
                        mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                }

                "spamplace" -> {
                    if (event.eventState == EventState.PRE)
                        mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                }
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || (onlyGround.get() && !mc.thePlayer.onGround))
            return
        val heldItem = mc.thePlayer.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> {
            if (consumeModifyValue.get())
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get() else 0.2F
        }

        is ItemSword -> {
            if (blockModifyValue.get())
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get() else 0.2F
        }

        is ItemBow -> {
            if (bowModifyValue.get())
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get() else 0.2F
        }

        else -> 0.2F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        var count = 0
        var lastItem: ItemStack? = null
        val isBlocking =
            mc.thePlayer.isUsingItem && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword
        val player = mc.thePlayer ?: return
        val currentItem = player.currentEquippedItem
        if (mc.thePlayer == null || mc.theWorld == null || (onlyGround.get() && !mc.thePlayer.onGround))
            return

        if ((modeValue.equals("Matrix") || modeValue.equals("GrimAC")) && (lastBlockingStat || isBlocking)) {
            if (msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                if (modeValue.equals("GrimAC")) {
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                    PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                } else {
                    PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos(-1, -1, -1),
                            EnumFacing.DOWN
                        )
                    )
                }
                if (packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for (packet in packetBuf) {
                        if (packet is C03PacketPlayer) {
                            canAttack = true
                        }
                        if (!((packet is C02PacketUseEntity || packet is C0APacketAnimation) && !canAttack)) {
                            PacketUtils.sendPacketNoEvent(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if (!nextTemp) {
                lastBlockingStat = isBlocking
                if (!isBlocking) {
                    return
                }
                PacketUtils.sendPacketNoEvent(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(-1, -1, -1),
                        255,
                        mc.thePlayer.inventory.getCurrentItem(),
                        0f,
                        0f,
                        0f
                    )
                )
                nextTemp = true
                waitC03 = false
                msTimer.reset()
            }
        }
    }

    private val isBlocking: Boolean
        get() = (mc.thePlayer.isUsingItem && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword)

    private fun getEmptySlot(): Int {
        for (i in 1..44) {
            mc.thePlayer.inventoryContainer.getSlot(i).stack ?: return i
        }
        return -1
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null || (onlyGround.get() && !mc.thePlayer.onGround))
            return
        val packet = event.packet
        val heldItem = mc.thePlayer.heldItem?.item

        stop = packet is C07PacketPlayerDigging && packet.status == (C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)
        if (consumeModifyValue.get() && mc.thePlayer.isUsingItem && (heldItem is ItemFood || heldItem is ItemPotion || heldItem is ItemBucketMilk)) {
            if (consumePacketValue.equals("Grim")) {
                if (packet is S30PacketWindowItems) {
                    event.cancelEvent()
                    eatslow = false
                }
                if (packet is S2FPacketSetSlot) {
                    event.cancelEvent()
                }
                if (packet is C08PacketPlayerBlockPlacement) {
                    eatslow = true
                }
                if (packet is C07PacketPlayerDigging && packet.status == (C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
                    eatslow = true
                }
            }
        }
        when (packet) {
            is C08PacketPlayerBlockPlacement -> {
                if (packet.stack?.item != null && mc.thePlayer.heldItem?.item != null && packet.stack.item == mc.thePlayer.heldItem?.item) {
                    if ((consumePacketValue.get() == "UNCP" && (packet.stack.item is ItemFood || packet.stack.item is ItemPotion || packet.stack.item is ItemBucketMilk)) || (bowPacketValue.get() == "UNCP" && packet.stack.item is ItemBow)) {
                        shouldSwap = true;
                    }
                }
            }
        }

        if (antiSwitchItem.get() && packet is S09PacketHeldItemChange && (mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking)) {
            event.cancelEvent()
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(packet.heldItemHotbarIndex))
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
        }

        if (modeValue.equals("Medusa")) {
            if ((mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking) && sendPacket) {
                PacketUtils.sendPacketNoEvent(
                    C0BPacketEntityAction(
                        mc.thePlayer,
                        C0BPacketEntityAction.Action.STOP_SPRINTING
                    )
                )
                sendPacket = false
            }
            if (!mc.thePlayer.isUsingItem || !mc.thePlayer.isBlocking) {
                sendPacket = true
            }
        }

        if ((modeValue.equals("Matrix") || modeValue.equals("GrimAC")) && nextTemp) {
            if ((packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            } else if (packet is C03PacketPlayer || packet is C0APacketAnimation || packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) {
                packetBuf.add(packet as Packet<INetHandlerPlayServer>)
                event.cancelEvent()
            }
        } else if (teleportValue.get() && packet is S08PacketPlayerPosLook) {
            pendingFlagApplyPacket = true
            lastMotionX = mc.thePlayer.motionX
            lastMotionY = mc.thePlayer.motionY
            lastMotionZ = mc.thePlayer.motionZ
            when (teleportModeValue.get().lowercase()) {
                "vanillanosetback" -> {
                    val x = packet.x - mc.thePlayer.posX
                    val y = packet.y - mc.thePlayer.posY
                    val z = packet.z - mc.thePlayer.posZ
                    val diff = sqrt(x * x + y * y + z * z)
                    if (diff <= 8) {
                        event.cancelEvent()
                        pendingFlagApplyPacket = false
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                packet.y,
                                packet.z,
                                packet.getYaw(),
                                packet.getPitch(),
                                mc.thePlayer.onGround
                            )
                        )
                    }
                }
            }
        } else if (pendingFlagApplyPacket && packet is C06PacketPlayerPosLook) {
            pendingFlagApplyPacket = false
            if (teleportNoApplyValue.get()) {
                event.cancelEvent()
            }
            when (teleportModeValue.get().lowercase()) {
                "vanilla", "vanillanosetback" -> {
                    mc.thePlayer.motionX = lastMotionX
                    mc.thePlayer.motionY = lastMotionY
                    mc.thePlayer.motionZ = lastMotionZ
                }

                "custom" -> {
                    if (MovementUtils.isMoving()) {
                        MovementUtils.strafe(teleportCustomSpeedValue.get())
                    }

                    if (teleportCustomYValue.get()) {
                        if (lastMotionY > 0) {
                            mc.thePlayer.motionY = teleportCustomSpeedValue.get().toDouble()
                        } else {
                            mc.thePlayer.motionY = -teleportCustomSpeedValue.get().toDouble()
                        }
                    }
                }

                "decrease" -> {
                    mc.thePlayer.motionX = lastMotionX * teleportDecreasePercentValue.get()
                    mc.thePlayer.motionY = lastMotionY * teleportDecreasePercentValue.get()
                    mc.thePlayer.motionZ = lastMotionZ * teleportDecreasePercentValue.get()
                }
            }
        }
    }
    private fun isUNCPBlocking() = modeValue.get() == "UNCP" && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.thePlayer.heldItem?.item is ItemSword)
    fun usingItemFunc() = mc.thePlayer?.heldItem != null && (mc.thePlayer.isUsingItem || (mc.thePlayer.heldItem?.item is ItemSword) || isUNCPBlocking())

    override val tag: String
        get() = consumePacketValue.get()
}