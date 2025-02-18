package net.ccbluex.liquidbounce.launch.data.modernui.mainmenu;

import net.ccbluex.liquidbounce.ui.client.altmanager.*;
import net.ccbluex.liquidbounce.utils.MainMenuButton;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;
import net.ccbluex.liquidbounce.utils.render.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ModernGuiMainMenu extends GuiScreen {
    private final ArrayList<MainMenuButton> buttons = new ArrayList<>();
    private MainMenuButton toggleUIButton; // UI切换按钮
    private ScaledResolution res;
    private float sidebarX = -120.0f;
    private boolean hoveringSidebar = false;
    private final float sidebarWidth = 120.0f;
    private final float sidebarHeight = 250.0f;
    private final float animationSpeed = 5.0f;
    private boolean isAlternativeLayout = false;
    private boolean isLoading = false;
    private long loadingStartTime = 0;
    private ResourceLocation backgroundImage = new ResourceLocation("leaf/background.png");
    private ResourceLocation image = new ResourceLocation("leaf/image.png");
    public ModernGuiMainMenu() {}

    @Override
    public void initGui() {
        this.buttons.clear();
        res = new ScaledResolution(this.mc);
        initDefaultLayout();
        super.initGui();
    }

    private void initDefaultLayout() {
        final float buttonWidth = 100.0f;
        final float buttonHeight = 15.0f;
        final float startY = (this.height - sidebarHeight) / 2.0f + 20.0f;

        this.buttons.add(new MainMenuButton(this, 0, "G", "Single", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this)), buttonWidth, buttonHeight, 10, startY));
        this.buttons.add(new MainMenuButton(this, 1, "H", "Multi", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this)), buttonWidth, buttonHeight, 10, startY + 30.0f));
        this.buttons.add(new MainMenuButton(this, 2, "I", "Alt", () -> this.mc.displayGuiScreen(new GuiAltManager(this)), buttonWidth, buttonHeight, 10, startY + 60.0f));
        this.buttons.add(new MainMenuButton(this, 3, "K", "Option", () -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings)), buttonWidth, buttonHeight, 10, startY + 90.0f));
        this.buttons.add(new MainMenuButton(this, 4, "L", "Language", () -> this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager())), buttonWidth, buttonHeight, 10, startY + 120.0f));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (isLoading) {
            long elapsedTime = System.currentTimeMillis() - loadingStartTime;
            if (elapsedTime >= 1000) {
                isLoading = false;
                isAlternativeLayout = !isAlternativeLayout;
                backgroundImage = new ResourceLocation("leaf/background.png");
                initGui();
            } else {
                int alpha = (int) ((elapsedTime / 1000.0f) * 255);
                this.drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 0, alpha).getRGB(), new Color(0, 0, 0, alpha).getRGB());
                return;
            }
        }

        this.drawGradientRect(0, 0, this.width, this.height, 0xFF202020, 0xFF202020);
        RenderUtils.drawImage(backgroundImage, 0, 0, this.res.getScaledWidth(), this.res.getScaledHeight());
        if (!isAlternativeLayout) {
            hoveringSidebar = mouseX < sidebarWidth && mouseY > (this.height - sidebarHeight) / 2 && mouseY < (this.height + sidebarHeight) / 2;
            sidebarX += (hoveringSidebar ? animationSpeed : -animationSpeed);
            sidebarX = Math.max(-sidebarWidth, Math.min(0, sidebarX));
            GlStateManager.pushMatrix();
            GlStateManager.translate(sidebarX, 0, 0);
        }

        for (MainMenuButton button : buttons) {
            button.draw(mouseX, mouseY);
        }

        if (!isAlternativeLayout) {
            GlStateManager.popMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (MainMenuButton button : buttons) {
            button.mouseClick(mouseX, mouseY, mouseButton);
        }
    }
}
