package net.nonemc.leaf.ui.mainmenu.config
import net.nonemc.leaf.file.mainMenuDir
import java.io.File

val TEXT_CONFIG_FILE = File(mainMenuDir, "MainMenu-Texts.json")
val BUTTON_CONFIG_FILE = File(mainMenuDir, "MainMenu-Data.json")
val PANEL_CONFIG_FILE = File(mainMenuDir, "MainMenu-Panel.json")
val IMAGE_CONFIG_FILE = File(mainMenuDir, "MainMenu-Image.json")
fun createDir() {
    if (!mainMenuDir.exists()) {
        mainMenuDir.mkdirs()
    }
}

fun createTextConfig() {
    if (!TEXT_CONFIG_FILE.exists()) {
        TEXT_CONFIG_FILE.writeText("[{\n" +
                "  \"id\": 0,\n" +
                "  \"text\":\"Leaf Client\",\n" +
                "  \"x\":10.0,\n" +
                "  \"y\":0.0,\n" +
                "  \"red\":100,\n" +
                "  \"green\":200,\n" +
                "  \"blue\":255,\n" +
                "  \"alpha\":255,\n" +
                "  \"scale\":2.0,\n" +
                "  \"centered\":true},\n" +
                "{\n" +
                "  \"id\": 2,\"text\":\"4.9.0\",\"x\":91.0,\"y\":1,\"red\":255,\"green\":255,\"blue\":255,\"alpha\":255,\"scale\":1.0,\"centered\":false}\n" +
                "]")
    }
}

fun createButtonConfig() {
    if (!BUTTON_CONFIG_FILE.exists()) {
        BUTTON_CONFIG_FILE.writeText("{\"Language\":{\"x\":33.0,\"y\":158.5,\"xScale\":0.6,\"yScale\":0.6,\"textScale\":1.0,\"color\":-1258291201,\"cornerRadius\":2},\"Single\":{\"x\":33.0,\"y\":78.5,\"xScale\":0.6,\"yScale\":0.6,\"textScale\":1.0,\"color\":-1258291201,\"cornerRadius\":2},\"Alt\":{\"x\":33.0,\"y\":118.5,\"xScale\":0.6,\"yScale\":0.6,\"textScale\":1.0,\"color\":-1258291201,\"cornerRadius\":2},\"Multi\":{\"x\":33.0,\"y\":98.5,\"xScale\":0.6,\"yScale\":0.6,\"textScale\":1.0,\"color\":-1258291201,\"cornerRadius\":2},\"Option\":{\"x\":33.0,\"y\":138.5,\"xScale\":0.6,\"yScale\":0.6,\"textScale\":1.0,\"color\":-1258291201,\"cornerRadius\":2}}\n")
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