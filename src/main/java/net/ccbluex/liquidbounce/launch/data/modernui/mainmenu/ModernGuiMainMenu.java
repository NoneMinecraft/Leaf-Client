/*
by None
仿制非打滑
 */
package net.ccbluex.liquidbounce.launch.data.modernui.mainmenu;

import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.ui.client.altmanager.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;
import net.ccbluex.liquidbounce.utils.render.*;
import java.awt.*;
import net.ccbluex.liquidbounce.font.*;
import java.util.*;
import java.io.*;

public class ModernGuiMainMenu extends GuiScreen {
    public ArrayList<MainMenuButton> butt;
    private ScaledResolution res;
    private float currentX;
    private float currentY;

    public ModernGuiMainMenu() {
        this.butt = new ArrayList<MainMenuButton>();
    }

    public void initGui() {
        this.butt.clear();
        final float buttonWidth = 50.0f;
        final float buttonHeight = 22.0f;
        final float startX = this.width / 2.0f - buttonWidth / 2.0f;
        final float startY = this.height / 2.0f - 110.0f;
        this.butt.add(new MainMenuButton(this, 0, "G", "Single", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this)), buttonWidth, buttonHeight, startX - 120.0f, startY + 104.0f));
        this.butt.add(new MainMenuButton(this, 1, "H", "Multi", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this)), buttonWidth, buttonHeight, startX - 60.0f, startY + 104.0f));
        this.butt.add(new MainMenuButton(this, 2, "I", "Alt", () -> this.mc.displayGuiScreen(new GuiAltManager(this)), buttonWidth, buttonHeight, startX, startY + 104.0f));
        this.butt.add(new MainMenuButton(this, 4, "K", "Option", () -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings)), buttonWidth, buttonHeight, startX + 60.0f, startY + 104.0f));
        this.butt.add(new MainMenuButton(this, 5, "L", "Language", () -> this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager())), buttonWidth, buttonHeight, startX + 120.0f, startY + 104.0f));

        this.res = new ScaledResolution(this.mc);
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            this.drawGradientRect(0, 0, this.width, this.height, 16777215, 16777215);
            int h = this.height;
            int w = this.width;
            float xDiff = ((float) (mouseX - h / 2) - this.currentX) / (float) this.res.getScaleFactor();
            float yDiff = ((float) (mouseY - w / 2) - this.currentY) / (float) this.res.getScaleFactor();
            this.currentX += xDiff * 0.3F;
            this.currentY += yDiff * 0.3F;
            GlStateManager.translate(this.currentX / 30.0F, this.currentY / 15.0F, 0.0F);

            RenderUtils.drawImage(new ResourceLocation("leaf/background.png"), 0, 0, this.res.getScaledWidth(), this.res.getScaledHeight());

            GlStateManager.translate(-this.currentX / 30.0F, -this.currentY / 15.0F, 0.0F);

            RenderUtils.drawRoundedCornerRect(this.width / 2.0F - 150.0F, this.height / 2.0F - 10.0F, this.width / 2.0F + 150.0F, this.height / 2.0F + 20.0F, 5.0f, new Color(0, 0, 0, 80).getRGB());
            FontLoaders.F20.drawCenteredString("Leaf Client", this.width / 2.0F, this.height / 2.0F - 25.0F, new Color(255, 255, 255).getRGB());

            for (final MainMenuButton button : this.butt) {
                button.draw(mouseX, mouseY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (final MainMenuButton button : this.butt) {
            button.mouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateScreen() {
        this.res = new ScaledResolution(this.mc);
        super.updateScreen();
    }
}
