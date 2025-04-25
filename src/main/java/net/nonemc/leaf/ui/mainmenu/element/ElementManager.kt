package net.nonemc.leaf.ui.mainmenu.element

import net.nonemc.leaf.ui.mainmenu.config.*
import net.nonemc.leaf.ui.mainmenu.script.createImageScript
import net.nonemc.leaf.ui.mainmenu.script.createPanelScript
import net.nonemc.leaf.ui.mainmenu.script.createScriptDir
import net.nonemc.leaf.ui.mainmenu.script.createTextScript

fun renderElement(mouseX:Int, mouseY:Int){
    renderCustomTexts()
    renderCustomPanel()
    renderCustomButton(mouseX, mouseY)
    renderImage()
}

fun loadElement(){
    loadImageConfig()
    loadPanelConfig()
    loadTextConfigurations()
    loadTextConfig()
}

fun createElement(){
    createDir();
    createButtonConfig();
    createPanelConfig();
    createTextConfig();
    createImageConfig();
}

fun createScript(){
    createScriptDir()
    createTextScript()
    createPanelScript()
    createImageScript()
}