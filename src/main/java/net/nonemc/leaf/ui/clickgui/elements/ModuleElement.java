package net.nonemc.leaf.ui.clickgui.elements;

import net.nonemc.leaf.features.module.Module;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.nonemc.leaf.ui.UILaunchOption;
import org.lwjgl.input.Mouse;

import static net.nonemc.leaf.features.module.modules.misc.MurderDetector.mc;

public class ModuleElement extends ButtonElement {

    private final Module module;

    private boolean showSettings;
    private float settingsWidth = 0F;
    private boolean wasPressed;

    public int slowlySettingsYPos;
    public int slowlyFade;

    public ModuleElement(final Module module) {
        super(null);

        this.displayName = module.getLocalizedName();
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float button) {
        UILaunchOption.clickGui.style.drawModuleElement(mouseX, mouseY, this);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
            module.toggle();
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }

        if(mouseButton == 1 && isHovering(mouseX, mouseY) && isVisible()) {
            showSettings = !showSettings;
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }

    public Module getModule() {
        return module;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    public boolean isntPressed() {
        return !wasPressed;
    }

    public void updatePressed() {
        wasPressed = Mouse.isButtonDown(0);
    }

    public float getSettingsWidth() {
        return settingsWidth;
    }

    public void setSettingsWidth(float settingsWidth) {
        this.settingsWidth = settingsWidth;
    }
}
