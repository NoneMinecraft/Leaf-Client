package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.BoolValue

@ModuleInfo(name = "Target", category = ModuleCategory.CLIENT, canEnable = false)
object Target : Module() {
    val playerValue = BoolValue("Player", true)
    val animalValue = BoolValue("Animal", false)
    val mobValue = BoolValue("Mob", true)
    val invisibleValue = BoolValue("Invisible", false)
    val deadValue = BoolValue("Dead", false)

    override fun handleEvents() = true
}