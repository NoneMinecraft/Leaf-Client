package net.nonemc.leaf.script.api.global

import net.nonemc.leaf.Leaf.displayAlert
import net.nonemc.leaf.Leaf.displayChatMessage

object Chat {
    @Suppress("unused")
    @JvmStatic
    fun print(message: String) {
        displayChatMessage(message)
    }

    @Suppress("unused")
    @JvmStatic
    fun alert(message: String) {
        displayAlert(message)
    }
}