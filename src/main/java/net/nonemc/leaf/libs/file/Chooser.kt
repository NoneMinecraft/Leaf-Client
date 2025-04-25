package net.nonemc.leaf.libs.file

import net.nonemc.leaf.libs.base.mc
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame

fun openFileChooser(): File? {
    if (net.nonemc.leaf.libs.base.mc.isFullScreen) net.nonemc.leaf.libs.base.mc.toggleFullscreen()
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