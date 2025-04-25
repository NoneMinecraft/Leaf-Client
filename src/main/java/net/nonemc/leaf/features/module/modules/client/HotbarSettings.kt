package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.render.Animation
import net.nonemc.leaf.libs.render.EaseUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "Hotbar", category = ModuleCategory.CLIENT, array = false, defaultOn = true)
object HotbarSettings : Module() {
    val hotbarValue = ListValue("HotbarMode", arrayOf("Minecraft", "Rounded", "Full", "Rise"), "Rise")
    val hotbarAlphaValue = IntegerValue("HotbarAlpha", 70, 0, 255)
    val hotbarEaseValue = BoolValue("HotbarEase", false)
    private val hotbarAnimSpeedValue = IntegerValue("HotbarAnimSpeed", 10, 5, 20).displayable { hotbarEaseValue.get() }
    private val hotbarAnimTypeValue =
        EaseUtils.getEnumEasingList("HotbarAnimType").displayable { hotbarEaseValue.get() }
    private val hotbarAnimOrderValue =
        EaseUtils.getEnumEasingOrderList("HotbarAnimOrder").displayable { hotbarEaseValue.get() }

    private var easeAnimation: Animation? = null
    private var easingValue = 0
        get() {
            if (easeAnimation != null) {
                field = easeAnimation!!.value.toInt()
                if (easeAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    easeAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (easeAnimation == null || (easeAnimation != null && easeAnimation!!.to != value.toDouble())) {
                easeAnimation = Animation(
                    EaseUtils.EnumEasingType.valueOf(hotbarAnimTypeValue.get()),
                    EaseUtils.EnumEasingOrder.valueOf(hotbarAnimOrderValue.get()),
                    field.toDouble(),
                    value.toDouble(),
                    hotbarAnimSpeedValue.get() * 30L
                ).start()
            }
        }

    fun getHotbarEasePos(x: Int): Int {
        if (!hotbarEaseValue.get()) return x
        easingValue = x
        return easingValue
    }
}