package net.nonemc.leaf.launch.data.modernui.mainmenu.config

import net.nonemc.leaf.launch.data.modernui.mainmenu.dir
import java.io.File

val TEXT_CONFIG_FILE = File(dir, "MainMenu-Texts.json")
val BUTTON_CONFIG_FILE = File(dir, "MainMenu-Data.json")
val PANEL_CONFIG_FILE = File(dir, "MainMenu-Panel.json")
val IMAGE_CONFIG_FILE = File(dir, "MainMenu-Image.json")

fun createDir() {
    if (!dir.exists()) {
        dir.mkdirs()
    }
}

fun createTextConfig() {
    if (!TEXT_CONFIG_FILE.exists()) {
        TEXT_CONFIG_FILE.writeText(
            "[{\"text\":\"Leaf Client\",\"x\":46.0,\"y\":2.0,\"red\":100,\"green\":200,\"blue\":255,\"alpha\":255,\"scale\":2.0,\"centered\":true,\"fontPath\":\"\",\"fontSize\": 24},\n" +
                    "{\"text\":\"4.9.0\",\"x\":91.0,\"y\":1,\"red\":255,\"green\":255,\"blue\":255,\"alpha\":255,\"scale\":1.0,\"centered\":false,\"fontPath\":\"\",\"fontSize\": 10}\n" + "]"
        )
    }
}

fun createButtonConfig() {
    if (!BUTTON_CONFIG_FILE.exists()) {
        BUTTON_CONFIG_FILE.writeText("{\"Language\":{\"x\":52.0,\"y\":218.5,\"xScale\":1.0,\"yScale\":1.0,\"textScale\":1.0,\"color\":-932944641,\"cornerRadius\":5},\"Single\":{\"x\":55.0,\"y\":67.5,\"xScale\":1.0,\"yScale\":1.0,\"textScale\":1.0,\"color\":-932944641,\"cornerRadius\":5},\"Alt\":{\"x\":54.0,\"y\":141.5,\"xScale\":1.0,\"yScale\":1.0,\"textScale\":1.0,\"color\":-932944641,\"cornerRadius\":5},\"Multi\":{\"x\":54.0,\"y\":103.5,\"xScale\":1.0,\"yScale\":1.0,\"textScale\":1.0,\"color\":-932944641,\"cornerRadius\":5},\"Option\":{\"x\":53.0,\"y\":177.5,\"xScale\":1.0,\"yScale\":1.0,\"textScale\":1.0,\"color\":-932944641,\"cornerRadius\":5}}")
    }
}

fun createPanelConfig() {
    if (!PANEL_CONFIG_FILE.exists()) {
        PANEL_CONFIG_FILE.writeText("[]")
    }
}
fun createImageConfig() {
    if (!IMAGE_CONFIG_FILE.exists()) {
        IMAGE_CONFIG_FILE.writeText("[]")
    }
}