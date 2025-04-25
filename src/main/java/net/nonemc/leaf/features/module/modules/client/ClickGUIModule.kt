package net.nonemc.leaf.features.module.modules.client

import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.PacketEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.file.dir
import net.nonemc.leaf.file.loadConfig
import net.nonemc.leaf.libs.render.ColorUtils.rainbow
import net.nonemc.leaf.ui.clickgui.ClickGuiConfig
import net.nonemc.leaf.ui.UILaunchOption
import net.nonemc.leaf.ui.clickgui.ClickGui
import net.nonemc.leaf.ui.clickgui.style.styles.LiquidBounceStyle
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue
import java.awt.Color
import java.io.File
import java.util.*

@ModuleInfo(name = "ClickGUI", category = ModuleCategory.CLIENT, canEnable = false)
class ClickGUIModule : Module() {
    private val styleValue = ListValue(
        "Style",
        arrayOf("Classic", "Astolfo", "LiquidBounce", "Null", "Slowly", "Black", "White"),
        "LiquidBounce"
    )
    @JvmField
    val scaleValue: FloatValue = FloatValue("Scale", 0.70f, 0.7f, 2f)
    @JvmField
    val maxElementsValue: IntegerValue = IntegerValue("MaxElements", 15, 1, 40)
    @JvmField
    val backgroundValue: ListValue = ListValue("Background", arrayOf("Default", "Gradient", "None"), "None")

    @JvmField
    val animationValue: ListValue =
        ListValue("Animation", arrayOf("Bread", "Slide", "LiquidBounce", "Zoom", "Ziul", "None"), "Ziul")
    @JvmField
    val getClosePrevious: BoolValue = BoolValue("ClosePrevious", true)

    override fun onEnable() {
        updateStyle()
        mc.displayGuiScreen(UILaunchOption.clickGui)
    }

    private fun updateStyle() {
        when (styleValue.get().lowercase(Locale.getDefault())) {
            "liquidbounce" ->{
                UILaunchOption.clickGui = ClickGui()
                UILaunchOption.clickGui.style = LiquidBounceStyle()
                UILaunchOption.clickGuiConfig =
                    ClickGuiConfig(File(dir, "clickgui.json"))
                loadConfig(UILaunchOption.clickGuiConfig)
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S2EPacketCloseWindow && mc.currentScreen is ClickGui) {
            event.cancelEvent()
        }
    }

    companion object {
        val colorRainbow: BoolValue = BoolValue("Rainbow", false)
        val colorRedValue: IntegerValue =
            IntegerValue("R", 0, 0, 255).displayable { !colorRainbow.get() } as IntegerValue
        val colorGreenValue: IntegerValue =
            IntegerValue("G", 160, 0, 255).displayable { !colorRainbow.get() } as IntegerValue
        val colorBlueValue: IntegerValue =
            IntegerValue("B", 255, 0, 255).displayable { !colorRainbow.get() } as IntegerValue

        @JvmStatic
        fun generateColor(): Color {
            return if (colorRainbow.get()) rainbow() else Color(
                colorRedValue.get(),
                colorGreenValue.get(),
                colorBlueValue.get()
            )
        }
    }
}
