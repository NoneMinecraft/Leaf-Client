
package net.nonemc.leaf.utils.login

import me.liuli.elixir.account.CrackedAccount
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.SessionEvent
import net.nonemc.leaf.ui.client.altmanager.GuiAltManager
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.utils.misc.RandomUtils
import net.minecraft.util.Session

object LoginUtils : MinecraftInstance() {
    fun loginCracked(username: String) {
        mc.session = CrackedAccount().also { it.name = username }.session.let { Session(it.username, it.uuid, it.token, it.type) }
        Leaf.eventManager.callEvent(SessionEvent())
    }

    fun randomCracked() {
        var name = GuiAltManager.randomAltField.text

        while (name.contains("%n") || name.contains("%s")) {
            if (name.contains("%n")) {
                name = name.replaceFirst("%n", RandomUtils.nextInt(0, 9).toString())
            }

            if (name.contains("%s")) {
                name = name.replaceFirst("%s", RandomUtils.randomString(1))
            }
        }

        loginCracked(name)
    }
}