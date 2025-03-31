package net.nonemc.leaf.features.module

import lombok.Getter
import net.nonemc.leaf.launch.data.modernui.clickgui.utils.normal.Main
import net.nonemc.leaf.launch.data.modernui.clickgui.utils.objects.Drag
import net.nonemc.leaf.launch.data.modernui.clickgui.utils.render.Scroll

enum class ModuleCategory(val displayName: String, val configName: String, val htmlIcon: String) {
    COMBAT("Combat", "Combat", "&#xe000;"),
    PLAYER("Player", "Player", "&#xe7fd;"),
    MOVEMENT("Movement", "Movement", "&#xe566;"),
    RENDER("Render", "Render", "&#xe417;"),
    CLIENT("Client", "Client", "&#xe869;"),
    WORLD("World", "World", "&#xe55b;"),
    MISC("Misc", "Misc", "&#xe5d3;"),
    Rage("Rage", "Rage", "&#xe5d9;"),
    DEBUG("Debug", "Debug", "&#xe5d8;"),
    EXPLOIT("Exploit", "Exploit", "&#xe868;");

    var namee: String? = null
    var posX = 0
    var expanded = false

    @Getter
    val scroll: Scroll = Scroll()

    @Getter
    var drag: Drag? = null
    var posY = 20

    open fun ModuleCategory(name: String?) {
        namee = name
        posX = 40 + Main.categoryCount * 120
        drag = Drag(posX.toFloat(), posY.toFloat())
        expanded = true
        Main.categoryCount++
    }

}