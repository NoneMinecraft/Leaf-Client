package net.nonemc.leaf.launch.data.modernui.mainmenu;

import com.google.gson.Gson;
import net.minecraft.client.gui.*;
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.ButtonConfig;
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.ImageConfig;
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.PanelConfig;
import net.nonemc.leaf.launch.data.modernui.mainmenu.config.configs.TextConfig;
import net.nonemc.leaf.launch.data.modernui.mainmenu.gui.ButtonConfigGUI;
import net.nonemc.leaf.ui.client.altmanager.GuiAltManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.nonemc.leaf.launch.data.modernui.mainmenu.FileKt.getDir;
import static net.nonemc.leaf.launch.data.modernui.mainmenu.config.ButtonConfigsKt.createButton;
import static net.nonemc.leaf.launch.data.modernui.mainmenu.config.ButtonConfigsKt.saveButtonConfig;
import static net.nonemc.leaf.launch.data.modernui.mainmenu.element.ElementManagerKt.*;


public class MainMenu extends GuiScreen {
    public static final ArrayList<MainMenuButton> buttons = new ArrayList<>();
    public static MainMenuButton draggingButton = null;
    public static float dragOffsetX;
    public static float dragOffsetY;
    public static final Gson GSON = new Gson();
    public long lastRightClickTime = 0;
    public static Map<String, ButtonConfig> buttonConfigs = new HashMap<>();
    public static List<TextConfig> textConfigs = new ArrayList<>();
    public static List<PanelConfig> panelConfigs = new ArrayList<>();
    public static List<ImageConfig> imageConfigs = new ArrayList<>();

    private void initDefaultLayout() {
        final float buttonWidth = 100.0f;
        final float buttonHeight = 30.0f;
        final float verticalSpacing = 10.0f;
        final float totalHeight = 5 * buttonHeight + 4 * verticalSpacing;
        final float startY = (this.height - totalHeight) / 2.0f;
        final float centerX = (this.width - buttonWidth) / 2.0f;

        buttons.add(createButton("Single", () -> mc.displayGuiScreen(new GuiSelectWorld(this)),
                buttonWidth, buttonHeight, centerX, startY));
        buttons.add(createButton("Multi", () -> mc.displayGuiScreen(new GuiMultiplayer(this)),
                buttonWidth, buttonHeight, centerX, startY + buttonHeight + verticalSpacing));
        buttons.add(createButton("Alt", () -> mc.displayGuiScreen(new GuiAltManager(this)),
                buttonWidth, buttonHeight, centerX, startY + 2 * (buttonHeight + verticalSpacing)));
        buttons.add(createButton("Option", () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)),
                buttonWidth, buttonHeight, centerX, startY + 3 * (buttonHeight + verticalSpacing)));
        buttons.add(createButton("Language", () -> mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager())),
                buttonWidth, buttonHeight, centerX, startY + 4 * (buttonHeight + verticalSpacing)));
    }

    @Override
    public void initGui() {
        buttons.clear();
        createScript();
        createElement();
        loadElement();
        initDefaultLayout();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(0, 0, width, height, 0xFF202020, 0xFF202020);
        renderElement(mouseX,mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 1) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRightClickTime < 250 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                for (MainMenuButton button : buttons) {
                    if (button.isMouseOver(mouseX, mouseY)) {
                        mc.displayGuiScreen(new ButtonConfigGUI(this, button));
                        break;
                    }
                }
            }
            lastRightClickTime = currentTime;
        }
        if (mouseButton == 1 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            for (MainMenuButton button : buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    draggingButton = button;
                    dragOffsetX = mouseX - button.getX();
                    dragOffsetY = mouseY - button.getY();
                    break;
                }
            }
        } else {
            for (MainMenuButton button : buttons) {
                button.mouseClick(mouseX, mouseY);
            }
        }
        saveButtonConfig();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingButton = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Mouse.isButtonDown(1)) {
            int mouseX = Mouse.getEventX() * width / mc.displayWidth;
            int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

            for (MainMenuButton button : buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    float scaleFactor = wheel > 0 ? 1.1f : 0.9f;
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        button.scaleXSize(scaleFactor);
                    } else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        button.scaleYSize(scaleFactor);
                    } else {
                        button.scaleBothSize(scaleFactor);
                    }
                    break;
                }
            }
        }
    }
}
