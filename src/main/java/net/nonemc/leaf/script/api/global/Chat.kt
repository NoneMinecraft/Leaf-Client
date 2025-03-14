﻿package net.nonemc.leaf.script.api.global

import net.nonemc.leaf.utils.ClientUtils

/**
 * Object used by the script API to provide an easier way of calling chat-related methods.
 */
object Chat {

    /**
     * Prints a message to the chat (client-side)
     * @param message Message to be printed
     */
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