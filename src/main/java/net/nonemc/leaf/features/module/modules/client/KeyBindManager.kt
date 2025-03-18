
package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "KeyBindManager", category = ModuleCategory.CLIENT, keyBind = Keyboard.KEY_RMENU, canEnable = false)
class KeyBindManager : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(Leaf.keyBindManager)
    }
}