package net.nonemc.leaf.script.api.global

import net.nonemc.leaf.utils.ClientUtils

object Chat {
    @Suppress("unused")
    @JvmStatic
    fun print(message: String) {
        ClientUtils.displayChatMessage(message)
    }

    @Suppress("unused")
    @JvmStatic
    fun alert(message: String) {
        ClientUtils.displayAlert(message)
    }
}