/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
package net.nonemc.leaf.features.module.modules.combat

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.FloatValue

@ModuleInfo(name = "Reach", category = ModuleCategory.COMBAT)
object Reach : Module() {
    val combatReachValue = FloatValue("CombatReach", 3.5f, 3f, 7f)
    val buildReachValue = FloatValue("BuildReach", 5f, 4.5f, 7f)

    val maxRange: Float
        get() {
            val combatRange = combatReachValue.get()
            val buildRange = buildReachValue.get()

            return if (combatRange > buildRange) combatRange else buildRange
        }

    val hitReach: Float
        get() = if (state) {
            combatReachValue.get()
        } else {
            3f
        }
}
