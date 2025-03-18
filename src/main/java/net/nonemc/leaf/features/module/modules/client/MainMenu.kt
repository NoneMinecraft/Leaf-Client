package net.nonemc.leaf.features.module.modules.client

import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.value.IntegerValue
import net.nonemc.leaf.value.ListValue

@ModuleInfo(name = "MainMenu", category = ModuleCategory.CLIENT, canEnable = false)
class MainMenu : Module() {
    companion object {
        val backgroundMode = ListValue("BackGroundMode", arrayOf("Old","New","Other","NoStar"),"New")
        val buttonColorAR = IntegerValue("ButtonColorA-R", 0, 0, 255)
        val buttonColorAG = IntegerValue("ButtonColorA-G", 0, 0, 255)
        val buttonColorAB = IntegerValue("ButtonColorA-B", 0, 0, 255)
        val buttonColorAA = IntegerValue("ButtonColorA-A", 150, 0, 255)

        val buttonColorBR = IntegerValue("ButtonColorB-R", 183, 0, 255)
        val buttonColorBG = IntegerValue("ButtonColorB-G", 255, 0, 255)
        val buttonColorBB = IntegerValue("ButtonColorB-B", 0, 0, 255)
        val buttonColorBA = IntegerValue("ButtonColorB-A", 180, 0, 255)
    }
}