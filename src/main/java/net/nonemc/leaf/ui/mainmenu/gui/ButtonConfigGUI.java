package net.nonemc.leaf.ui.mainmenu.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.nonemc.leaf.ui.mainmenu.MainMenuButton;
import net.nonemc.leaf.ui.mainmenu.config.configs.ButtonConfig;

import java.awt.*;
import java.io.IOException;

import static net.nonemc.leaf.ui.mainmenu.config.ButtonConfigsKt.saveButtonConfig;
import static net.nonemc.leaf.ui.mainmenu.config.ButtonConfigsKt.updateButtonConfig;

public class ButtonConfigGUI extends GuiScreen {
    private final GuiScreen parent;
    private final MainMenuButton button;
    private GuiTextField xField, yField, xScaleField, yScaleField, textScaleField;
    private GuiTextField rField, gField, bField, aField, radiusField;
    private final int spacing = 25;

    public ButtonConfigGUI(GuiScreen parent, MainMenuButton button) {
        this.parent = parent;
        this.button = button;
    }

    @Override
    public void initGui() {
        int centerX = width / 2;
        int startY = height / 4 - 60;
        int fieldWidth = 100;
        int fieldHeight = 20;
        int spacing = 25;

        xField = new GuiTextField(0, fontRendererObj, centerX - 50, startY, fieldWidth, fieldHeight);
        xField.setText(String.format("%.1f", button.getX()));

        yField = new GuiTextField(1, fontRendererObj, centerX - 50, startY + spacing, fieldWidth, fieldHeight);
        yField.setText(String.format("%.1f", button.getY()));

        xScaleField = new GuiTextField(2, fontRendererObj, centerX - 50, startY + 2*spacing, fieldWidth, fieldHeight);
        xScaleField.setText(String.format("%.2f", button.getXScale()));

        yScaleField = new GuiTextField(3, fontRendererObj, centerX - 50, startY + 3*spacing, fieldWidth, fieldHeight);
        yScaleField.setText(String.format("%.2f", button.getYScale()));

        textScaleField = new GuiTextField(4, fontRendererObj, centerX - 50, startY + 4*spacing, fieldWidth, fieldHeight);
        textScaleField.setText(String.format("%.2f", button.getTextScale()));

        Color color = new Color(button.getColor(), true);
        rField = new GuiTextField(5, fontRendererObj, centerX - 50, startY + 5*spacing, fieldWidth, fieldHeight);
        rField.setText(Integer.toString(color.getRed()));

        gField = new GuiTextField(6, fontRendererObj, centerX - 50, startY + 6*spacing, fieldWidth, fieldHeight);
        gField.setText(Integer.toString(color.getGreen()));

        bField = new GuiTextField(7, fontRendererObj, centerX - 50, startY + 7*spacing, fieldWidth, fieldHeight);
        bField.setText(Integer.toString(color.getBlue()));

        aField = new GuiTextField(8, fontRendererObj, centerX - 50, startY + 8*spacing, fieldWidth, fieldHeight);
        aField.setText(Integer.toString(color.getAlpha()));

        radiusField = new GuiTextField(9, fontRendererObj, centerX - 50, startY + 9*spacing, fieldWidth, fieldHeight);
        radiusField.setText(Integer.toString(button.getCornerRadius()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int centerX = width / 2;
        int startY = height / 4 - 60;

        String[] labels = {
                "X Position", "Y Position",
                "X Scale (0.5-2.0)", "Y Scale (0.5-2.0)", "Text Scale (0.5-2.0)",
                "Red (0-255)", "Green (0-255)", "Blue (0-255)", "Alpha (0-255)",
                "Corner Radius"
        };

        for (int i = 0; i < labels.length; i++) {
            drawCenteredString(fontRendererObj, labels[i],
                    centerX - 90, startY + 3 + i * spacing, 0xFFFFFF);
        }
        xField.drawTextBox();
        yField.drawTextBox();
        xScaleField.drawTextBox();
        yScaleField.drawTextBox();
        textScaleField.drawTextBox();
        rField.drawTextBox();
        gField.drawTextBox();
        bField.drawTextBox();
        aField.drawTextBox();
        radiusField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        xField.textboxKeyTyped(typedChar, keyCode);
        yField.textboxKeyTyped(typedChar, keyCode);
        xScaleField.textboxKeyTyped(typedChar, keyCode);
        yScaleField.textboxKeyTyped(typedChar, keyCode);
        textScaleField.textboxKeyTyped(typedChar, keyCode);
        rField.textboxKeyTyped(typedChar, keyCode);
        gField.textboxKeyTyped(typedChar, keyCode);
        bField.textboxKeyTyped(typedChar, keyCode);
        aField.textboxKeyTyped(typedChar, keyCode);
        radiusField.textboxKeyTyped(typedChar, keyCode);

        try {
            int r = clamp(Integer.parseInt(rField.getText()));
            int g = clamp(Integer.parseInt(gField.getText()));
            int b = clamp(Integer.parseInt(bField.getText()));
            int a = clamp(Integer.parseInt(aField.getText()));
            Color color = new Color(r, g, b, a);
            ButtonConfig newConfig = new ButtonConfig(
                    Float.parseFloat(xField.getText()),
                    Float.parseFloat(yField.getText()),
                    Float.parseFloat(xScaleField.getText()),
                    Float.parseFloat(yScaleField.getText()),
                    Float.parseFloat(textScaleField.getText()),
                    color.getRGB(),
                    Integer.parseInt(radiusField.getText())
            );

            updateButtonConfig(button, newConfig);
        } catch (NumberFormatException e) {
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        xField.mouseClicked(mouseX, mouseY, mouseButton);
        yField.mouseClicked(mouseX, mouseY, mouseButton);
        xScaleField.mouseClicked(mouseX, mouseY, mouseButton);
        yScaleField.mouseClicked(mouseX, mouseY, mouseButton);
        textScaleField.mouseClicked(mouseX, mouseY, mouseButton);
        rField.mouseClicked(mouseX, mouseY, mouseButton);
        gField.mouseClicked(mouseX, mouseY, mouseButton);
        bField.mouseClicked(mouseX, mouseY, mouseButton);
        aField.mouseClicked(mouseX, mouseY, mouseButton);
        radiusField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        saveButtonConfig();
        super.onGuiClosed();
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
