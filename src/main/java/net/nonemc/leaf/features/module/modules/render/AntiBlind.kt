
package net.nonemc.leaf.features.module.modules.render

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.ClientShutdownEvent
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

@ModuleInfo(name = "AntiBlind", category = ModuleCategory.RENDER)
class AntiBlind : Module() {
    val confusionEffectValue = BoolValue("Confusion", true)
    val pumpkinEffectValue = BoolValue("Pumpkin", true)
    val fireEffectValue = FloatValue("FireAlpha", 0.3f, 0f, 1f)
    private val brightValue = ListValue("Bright", arrayOf("None", "Gamma", "NightVision"), "Gamma")
    val bossHealth = BoolValue("Boss-Health", true)

    private var prevGamma = -1f

    override fun onEnable() {
        prevGamma = mc.gameSettings.gammaSetting
    }

    override fun onDisable() {
        if (prevGamma == -1f) return
        mc.gameSettings.gammaSetting = prevGamma
        prevGamma = -1f
        if (mc.thePlayer != null) mc.thePlayer.removePotionEffectClient(Potion.nightVision.id)
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent) {
        if (state || Leaf.moduleManager[XRay::class.java]!!.state) {
            when (brightValue.get().lowercase()) {
                "gamma" -> if (mc.gameSettings.gammaSetting <= 100f) mc.gameSettings.gammaSetting++
                "nightvision" -> mc.thePlayer.addPotionEffect(PotionEffect(Potion.nightVision.id, 1337, 1))
            }
        } else if (prevGamma != -1f) {
            mc.gameSettings.gammaSetting = prevGamma
            prevGamma = -1f
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onShutdown(event: ClientShutdownEvent) {
        onDisable()
    }
}