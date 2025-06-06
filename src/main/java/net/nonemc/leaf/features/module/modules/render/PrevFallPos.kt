﻿package net.nonemc.leaf.features.module.modules.render

import net.minecraft.util.BlockPos
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.entity.EntityFallLib
import net.nonemc.leaf.libs.render.ColorUtils
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import java.awt.Color
import kotlin.math.abs

@ModuleInfo(name = "PrevFallPos", category = ModuleCategory.RENDER)
class PrevFallPos : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Box", "OtherBox", "Outline"), "Box")
    private val outlineWidthValue =
        FloatValue("Outline-Width", 3f, 0.5f, 5f).displayable { modeValue.equals("Outline") }
    private val fallDistValue = FloatValue("FallDist", 1.15F, 0F, 5F)
    private val colorRedValue = IntegerValue("R", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorGreenValue = IntegerValue("G", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorBlueValue = IntegerValue("B", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorAlphaValue = IntegerValue("A", 130, 0, 255)
    private val colorRainbowValue = BoolValue("Rainbow", false)

    private var pos: BlockPos? = null

    override fun onEnable() {
        pos = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        pos = if (!mc.thePlayer.onGround) {
            val fallingPlayer = EntityFallLib(mc.thePlayer)
            val collLoc = fallingPlayer.findCollision(60)
            if (abs((collLoc?.y ?: 0) - mc.thePlayer.posY) > (fallDistValue.get() + 1)) {
                collLoc
            } else {
                null
            }
        } else {
            null
        }
    }

    @EventTarget
    fun onRender3d(event: Render3DEvent) {
        pos ?: return

        val color = if (colorRainbowValue.get()) ColorUtils.rainbowWithAlpha(colorAlphaValue.get()) else Color(
            colorRedValue.get(),
            colorGreenValue.get(),
            colorBlueValue.get(),
            colorAlphaValue.get()
        )
        when (modeValue.get().lowercase()) {
            "box" -> {
                RenderUtils.drawBlockBox(pos, color, true, true, outlineWidthValue.get())
            }

            "otherbox" -> {
                RenderUtils.drawBlockBox(pos, color, false, true, outlineWidthValue.get())
            }

            "outline" -> {
                RenderUtils.drawBlockBox(pos, color, true, false, outlineWidthValue.get())
            }
        }
    }
}
