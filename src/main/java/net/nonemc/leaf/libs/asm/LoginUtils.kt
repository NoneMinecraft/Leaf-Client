
package net.nonemc.leaf.libs.asm

import me.liuli.elixir.account.CrackedAccount
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.SessionEvent
import net.nonemc.leaf.ui.altmanager.GuiAltManager
import net.minecraft.util.Session
import net.nonemc.leaf.libs.base.MinecraftInstance
import net.nonemc.leaf.libs.random.randomInt
import net.nonemc.leaf.libs.random.randomString

object LoginUtils : MinecraftInstance() {
    fun loginCracked(username: String) {
        mc.session = CrackedAccount().also { it.name = username }.session.let { Session(it.username, it.uuid, it.token, it.type) }
        Leaf.eventManager.callEvent(SessionEvent())
    }

    fun randomCracked() {
        var name = GuiAltManager.randomAltField.text

        while (name.contains("%n") || name.contains("%s")) {
            if (name.contains("%n")) {
                name = name.replaceFirst("%n", randomInt(0, 9).toString())
            }

            if (name.contains("%s")) {
                name = name.replaceFirst("%s", randomString(1))
            }
        }

        loginCracked(name)
    }
}