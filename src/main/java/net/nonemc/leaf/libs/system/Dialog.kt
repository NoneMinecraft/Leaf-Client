package net.nonemc.leaf.libs.system

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.util.*
import javax.swing.JOptionPane

fun showDialog(title: String, message: String) {
    if (SystemTray.isSupported()) {
        try {
            val tray = SystemTray.getSystemTray()
            val image = Toolkit.getDefaultToolkit().createImage(ByteArray(0))
            val trayIcon = TrayIcon(image)
            trayIcon.isImageAutoSize = true
            tray.add(trayIcon)
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    try {
        when {
            os.contains("mac") -> {
                Runtime.getRuntime().exec(arrayOf("osascript", "-e", "display notification \"$message\" with title \"$title\""))
            }
            os.contains("linux") -> {
                Runtime.getRuntime().exec(arrayOf("notify-send", title, message))
            }
            else -> {
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE)
    }
    return
}