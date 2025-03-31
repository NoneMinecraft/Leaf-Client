package net.nonemc.leaf.launch.data.modernui.mainmenu.script

import net.nonemc.leaf.launch.data.modernui.mainmenu.scriptDir
import java.io.File

val TEXT_SCRIPT_FILE = File(scriptDir, "Text-Main.js")
val PANEL_SCRIPT_FILE = File(scriptDir, "Panel-Main.js")
val IMAGE_SCRIPT_FILE = File(scriptDir, "Image-Main.js")

fun createScriptDir() {
    if (!scriptDir.exists()) {
        scriptDir.mkdirs()
    }
}
fun createTextScript() {
    if (!TEXT_SCRIPT_FILE.exists()) {
        TEXT_SCRIPT_FILE.writeText("true")
    }
}
fun createPanelScript() {
    if (!PANEL_SCRIPT_FILE.exists()) {
        PANEL_SCRIPT_FILE.writeText("true")
    }
}
fun createImageScript() {
    if (!IMAGE_SCRIPT_FILE.exists()) {
        IMAGE_SCRIPT_FILE.writeText("true")
    }
}