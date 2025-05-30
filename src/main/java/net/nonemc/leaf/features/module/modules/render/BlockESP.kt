﻿package net.nonemc.leaf.features.module.modules.render

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.block.BlockLib.getBlock
import net.nonemc.leaf.libs.block.BlockLib.getBlockName
import net.nonemc.leaf.libs.render.ColorUtils.rainbow
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.libs.timer.MSTimer
import net.nonemc.leaf.value.*
import java.awt.Color

@ModuleInfo(name = "BlockESP", category = ModuleCategory.RENDER)
class BlockESP : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Box", "OtherBox", "Outline", "2D"), "Box")
    private val outlineWidth = FloatValue("Outline-Width", 3f, 0.5f, 5f).displayable { modeValue.equals("Outline") }
    private val blockValue = BlockValue("Block", 168)
    private val radiusValue = IntegerValue("Radius", 40, 5, 120)
    private val colorRedValue = IntegerValue("R", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorGreenValue = IntegerValue("G", 179, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorBlueValue = IntegerValue("B", 72, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorRainbowValue = BoolValue("Rainbow", false)
    private val searchTimer = MSTimer()
    private val posList: MutableList<BlockPos> = ArrayList()
    private var color = Color.CYAN
    private var thread: Thread? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        color = if (colorRainbowValue.get()) rainbow() else Color(
            colorRedValue.get(),
            colorGreenValue.get(),
            colorBlueValue.get()
        )
        if (searchTimer.hasTimePassed(1000L) && (thread == null || !thread!!.isAlive)) {
            val radius = radiusValue.get()
            val selectedBlock = Block.getBlockById(blockValue.get())
            if (selectedBlock == null || selectedBlock === Blocks.air) return
            thread = Thread({
                val blockList: MutableList<BlockPos> = ArrayList()
                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val xPos = mc.thePlayer.posX.toInt() + x
                            val yPos = mc.thePlayer.posY.toInt() + y
                            val zPos = mc.thePlayer.posZ.toInt() + z
                            val blockPos = BlockPos(xPos, yPos, zPos)
                            val block = getBlock(blockPos)
                            if (block === selectedBlock) blockList.add(blockPos)
                        }
                    }
                }
                searchTimer.reset()
                synchronized(posList) {
                    posList.clear()
                    posList.addAll(blockList)
                }
            }, "BlockESP-BlockFinder")
            thread!!.start()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        synchronized(posList) {
            for (blockPos in posList) {
                when (modeValue.get().lowercase()) {
                    "box" -> {
                        RenderUtils.drawBlockBox(blockPos, color, true, true, outlineWidth.get())
                    }

                    "otherbox" -> {
                        RenderUtils.drawBlockBox(blockPos, color, false, true, outlineWidth.get())
                    }

                    "outline" -> {
                        RenderUtils.drawBlockBox(blockPos, color, true, false, outlineWidth.get())
                    }

                    "2d" -> {
                        RenderUtils.draw2D(blockPos, color.rgb, Color.BLACK.rgb)
                    }
                }
            }
        }
    }

    override val tag: String
        get() = getBlockName(blockValue.get())
}