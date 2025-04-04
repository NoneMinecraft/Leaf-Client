﻿package net.nonemc.leaf.utils.misc

import net.nonemc.leaf.utils.MinecraftInstance
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Consumer
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane

object MiscUtils : MinecraftInstance() {

    fun showErrorPopup(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
    }

    fun showURL(url: String) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun openFileChooser(): File? {
        if (mc.isFullScreen) mc.toggleFullscreen()
        val fileChooser = JFileChooser()
        val frame = JFrame()
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
        frame.isVisible = true
        frame.toFront()
        frame.isVisible = false
        val action = fileChooser.showOpenDialog(frame)
        frame.dispose()
        return if (action == JFileChooser.APPROVE_OPTION) fileChooser.selectedFile else null
    }

    fun <T> make(`object`: T, consumer: Consumer<T>): T {
        consumer.accept(`object`)
        return `object`
    }
}