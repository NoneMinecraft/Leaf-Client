package net.nonemc.leaf.ui.mainmenu.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class BackGroundGUI extends GuiScreen {
    private GuiTextField inputField;
    private final GuiScreen parent;
    private final InputCallback callback;

    public interface InputCallback {
        void onInput(String input);
    }

    public BackGroundGUI(GuiScreen parent, InputCallback callback) {
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.inputField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 10, 200, 20);
        this.inputField.setMaxStringLength(200);
        this.inputField.setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            String text = inputField.getText();
            if (!text.trim().isEmpty()) {
                callback.onInput(text.trim());
            }
            mc.displayGuiScreen(parent);
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        } else {
            inputField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Description" +
                ":", width / 2, height / 2 - 30, 0xFFFFFF);
        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        inputField.updateCursorCounter();
    }
}
