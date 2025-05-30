﻿package net.nonemc.leaf.script.api.global

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.ui.hud.element.elements.Notification
import net.nonemc.leaf.ui.hud.element.elements.NotifyType

object Notifications {
    @Suppress("unused")
    @JvmStatic
    fun create(name: String, content: String, notify: String, time: Int) {
        var notifytype = NotifyType.INFO
        when (notify.lowercase()) {
            "success" -> notifytype = NotifyType.SUCCESS

            "info" -> notifytype = NotifyType.INFO

            "error" -> notifytype = NotifyType.ERROR

            "warning" -> notifytype = NotifyType.WARNING
        }
        Leaf.hud.addNotification(
            Notification(
                name,
                content,
                notifytype,
                time
            )
        )
    }
}