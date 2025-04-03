package net.nonemc.leaf.launch.data.modernui.mainmenu.script

import net.nonemc.leaf.launch.data.modernui.mainmenu.MainMenu
import net.nonemc.leaf.launch.data.modernui.mainmenu.scriptDir
import java.io.File
val TEXT_DIR = File("$scriptDir\\Text")
val PANEL_DIR = File("$scriptDir\\Panel")
val IMAGE_DIR = File("$scriptDir\\Image")
val TEXT_FILES = listOf(
    File(TEXT_DIR, "Text-Main.js") to "true",
    File(TEXT_DIR, "Text-X.js") to "1.0",
    File(TEXT_DIR, "Text-Y.js") to "1.0",
    File(TEXT_DIR, "Text-R.js") to "1",
    File(TEXT_DIR, "Text-G.js") to "1",
    File(TEXT_DIR, "Text-B.js") to "1",
    File(TEXT_DIR, "Text-A.js") to "255" ,
    File(TEXT_DIR, "Text-Scale.js") to "255"
)

val PANEL_FILES = listOf(
    File(PANEL_DIR, "Panel-Main.js") to "true",
    File(PANEL_DIR, "Panel-X.js") to "1.0",
    File(PANEL_DIR, "Panel-Y.js") to "1.0",
    File(PANEL_DIR, "Panel-Radius.js") to "1.0",
    File(PANEL_DIR, "Panel-R.js") to "1",
    File(PANEL_DIR, "Panel-G.js") to "1",
    File(PANEL_DIR, "Panel-B.js") to "1",
    File(PANEL_DIR, "Panel-A.js") to "255"
)
val IMAGE_FILES = listOf(
    File(IMAGE_DIR, "Image-Main.js") to "true",
    File(IMAGE_DIR, "Image-X.js") to "1.0",
    File(IMAGE_DIR, "Image-Y.js") to "1.0",
    File(IMAGE_DIR, "Image-W.js") to "1.0",
    File(IMAGE_DIR, "Image-H.js") to "1.0",
    File(IMAGE_DIR, "Image-A.js") to "255"
)

val IMAGE_SCRIPT_FILE = File(scriptDir, "Image-Main.js")

fun createScriptDir() {
    if (!PANEL_DIR.exists()) {
        PANEL_DIR.mkdirs()
    }
    if (!TEXT_DIR.exists()) {
        TEXT_DIR.mkdirs()
    }
    if (!IMAGE_DIR.exists()) {
        IMAGE_DIR.mkdirs()
    }
}
fun createTextScript() {
    TEXT_FILES.forEach { (file, content) ->
        if (!file.exists()) {
            file.writeText(content)
        }
    }
}
fun createPanelScript() {
    PANEL_FILES.forEach { (file, content) ->
        if (!file.exists()) {
            file.writeText(content)
        }
    }
}
fun createImageScript() {
    IMAGE_FILES.forEach { (file, content) ->
        if (!file.exists()) {
            file.writeText(content)
        }
    }
}