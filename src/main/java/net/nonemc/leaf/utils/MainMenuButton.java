package net.nonemc.leaf.utils;

import net.minecraft.client.gui.*;
import net.nonemc.leaf.ui.font.Fonts;
import net.nonemc.leaf.utils.render.RenderUtils;

import java.awt.*;

public class MainMenuButton {
    private final GuiScreen parent;
    private final int id;
    private final String icon;
    private final String text;
    private final Runnable action;
    private final float width;
    private final float height;
    private float x;
    private float y;

    public MainMenuButton(final GuiScreen parent, final int id, final String icon, final String text, final Runnable action, final float width, final float height, final float x, final float y) {
        this.parent = parent;
        this.id = id;
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void draw(final int mouseX, final int mouseY) {
        boolean isMouseOver = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
        int baseColor = new Color(0, 0, 0, 180).getRGB();
        int otherColor = new Color(8, 255, 0, 150).getRGB();
        RenderUtils.drawRoundedRect(this.x, this.y, this.x + width, this.y + this.height, 1, baseColor);

        if (!isMouseOver) {
            RenderUtils.drawRoundedRect(width + 7, this.y, width + 3 + 7, this.y + this.height, 0.8f, otherColor);
        } else {
            RenderUtils.drawRoundedRect(width + 10, this.y, width + 3 + 10, this.y + this.height, 0.0f, otherColor);
        }
        Fonts.font40.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + this.height / 2.0f - 5.25f, new Color(255, 255, 255).getRGB());
    }

    public void mouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            this.action.run();
        }
    }

    public void setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public interface Executor {
        void execute();
    }
}
