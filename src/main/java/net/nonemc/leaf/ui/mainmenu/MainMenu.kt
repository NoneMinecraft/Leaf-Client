package net.nonemc.leaf.ui.mainmenu

import com.google.gson.Gson
import drawDynamicImage
import net.minecraft.client.gui.*
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.file.backgroundDir
import net.nonemc.leaf.file.backgroundFile
import net.nonemc.leaf.ui.mainmenu.config.configs.ButtonConfig
import net.nonemc.leaf.ui.mainmenu.config.configs.ImageConfig
import net.nonemc.leaf.ui.mainmenu.config.configs.PanelConfig
import net.nonemc.leaf.ui.mainmenu.config.configs.TextConfig
import net.nonemc.leaf.ui.mainmenu.config.createButton
import net.nonemc.leaf.ui.mainmenu.config.saveButtonConfig
import net.nonemc.leaf.ui.mainmenu.element.createElement
import net.nonemc.leaf.ui.mainmenu.element.createScript
import net.nonemc.leaf.ui.mainmenu.element.loadElement
import net.nonemc.leaf.ui.mainmenu.element.renderElement
import net.nonemc.leaf.ui.mainmenu.gui.BackGroundGUI
import net.nonemc.leaf.ui.mainmenu.gui.ButtonConfigGUI
import net.nonemc.leaf.ui.mainmenu.thread.AIBackGroundThread
import net.nonemc.leaf.libs.ai.downloadImage
import net.nonemc.leaf.libs.ai.getUrl
import net.nonemc.leaf.libs.ai.json.loadAIImageJson
import net.nonemc.leaf.libs.system.Time.hasTimeChanged
import net.nonemc.leaf.ui.altmanager.GuiAltManager
import net.nonemc.leaf.libs.render.RenderUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.File
import java.io.IOException

class MainMenu : GuiScreen() {
    private var lastRightClickTime: Long = 0
    private var res: ScaledResolution? = null
    private val backgroundImage = ResourceLocation("leaf/background.png")
    private fun initDefaultLayout() {
        val buttonWidth = 100.0f
        val buttonHeight = 30.0f
        val verticalSpacing = 10.0f
        val totalHeight = 5 * buttonHeight + 4 * verticalSpacing
        val startY = (this.height - totalHeight) / 2.0f
        val centerX = (this.width - buttonWidth) / 2.0f

        buttons.add(
            createButton(
                "Single", { mc.displayGuiScreen(GuiSelectWorld(this)) },
                buttonWidth, buttonHeight, centerX, startY
            )
        )
        buttons.add(
            createButton(
                "Multi", { mc.displayGuiScreen(GuiMultiplayer(this)) },
                buttonWidth, buttonHeight, centerX, startY + buttonHeight + verticalSpacing
            )
        )
        buttons.add(
            createButton(
                "Alt", { mc.displayGuiScreen(GuiAltManager(this)) },
                buttonWidth, buttonHeight, centerX, startY + 2 * (buttonHeight + verticalSpacing)
            )
        )
        buttons.add(
            createButton(
                "Option", { mc.displayGuiScreen(GuiOptions(this, mc.gameSettings)) },
                buttonWidth, buttonHeight, centerX, startY + 3 * (buttonHeight + verticalSpacing)
            )
        )
        buttons.add(
            createButton(
                "Language", { mc.displayGuiScreen(GuiLanguage(this, mc.gameSettings, mc.languageManager)) },
                buttonWidth, buttonHeight, centerX, startY + 4 * (buttonHeight + verticalSpacing)
            )
        )
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        if (keyCode == Keyboard.KEY_G) {
            mc.displayGuiScreen(BackGroundGUI(this) { input ->
                downloadImage(getUrl(input, 1920, 1080), backgroundFile)
            })
        }
    }

    override fun initGui() {
        res = ScaledResolution(this.mc)
        buttons.clear()
        createScript()
        createElement()
        loadElement()
        initDefaultLayout()
        super.initGui()
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        try {
            loadElement()
            try {
                drawDynamicImage(backgroundFile.toString(), 0, 0, res!!.scaledWidth, res!!.scaledHeight)
            } catch (e: Exception) {
                RenderUtils.drawImage(backgroundImage, 0, 0, res!!.scaledWidth, res!!.scaledHeight)
            }
            AIBackGroundThread.run {
                if (hasTimeChanged() && loadAIImageJson(File(backgroundDir, "AIBackGround.json")) != null) {
                    getAIBackGround(1920, 1080)
                }
            }
            renderElement(mouseX, mouseY)
            super.drawScreen(mouseX, mouseY, partialTicks)
        }catch (e:Exception){
            println(e)
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 1) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastRightClickTime < 250 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                for (button in buttons) {
                    if (button.isMouseOver(mouseX, mouseY)) {
                        mc.displayGuiScreen(ButtonConfigGUI(this, button))
                        break
                    }
                }
            }
            lastRightClickTime = currentTime
        }
        if (mouseButton == 1 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            for (button in buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    draggingButton = button
                    dragOffsetX = mouseX - button.x
                    dragOffsetY = mouseY - button.y
                    break
                }
            }
        } else {
            for (button in buttons) {
                button.mouseClick(mouseX, mouseY)
            }
        }
        saveButtonConfig()
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        draggingButton = null
        super.mouseReleased(mouseX, mouseY, state)
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        val wheel = Mouse.getEventDWheel()
        if (wheel != 0 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Mouse.isButtonDown(1)) {
            val mouseX = Mouse.getEventX() * width / mc.displayWidth
            val mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1

            for (button in buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    val scaleFactor = if (wheel > 0) 1.1f else 0.9f
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        button.scaleXSize(scaleFactor)
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        button.scaleYSize(scaleFactor)
                    } else {
                        button.scaleBothSize(scaleFactor)
                    }
                    break
                }
            }
        }
    }

    companion object {
        val buttons: ArrayList<MainMenuButton> = ArrayList()
        var draggingButton: MainMenuButton? = null
        var dragOffsetX: Float = 0f
        var dragOffsetY: Float = 0f
        val GSON: Gson = Gson()
        var buttonConfigs: Map<String, ButtonConfig> = HashMap()
        var textConfigs: List<TextConfig> = ArrayList()
        var panelConfigs: List<PanelConfig> = ArrayList()
        var imageConfigs: List<ImageConfig> = ArrayList()
    }
}
