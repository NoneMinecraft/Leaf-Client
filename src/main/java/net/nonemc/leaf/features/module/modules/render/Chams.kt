
package net.nonemc.leaf.features.module.modules.render

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.utils.render.ColorUtils
import net.nonemc.leaf.utils.render.RenderUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import org.lwjgl.opengl.GL11

@ModuleInfo(name = "Chams", category = ModuleCategory.RENDER)
class Chams : Module() {
    val targetsValue = BoolValue("Targets", true)
    val chestsValue = BoolValue("Chests", true)
    val itemsValue = BoolValue("Items", true)

    private val colorRainbowValue = BoolValue("Rainbow", false)
    private val colorRedValue = IntegerValue("Red", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255).displayable { !colorRainbowValue.get() }
    private val colorAlphaValue = IntegerValue("Alpha", 200, 0, 255)

    fun setColor() {
        if (colorRainbowValue.get()) {
            RenderUtils.glColor(ColorUtils.rainbowWithAlpha(colorAlphaValue.get()))
        } else {
            GL11.glColor4f(colorRedValue.get() / 255f, colorGreenValue.get() / 255f, colorBlueValue.get() / 255f, colorAlphaValue.get() / 255f)
        }
    }
}