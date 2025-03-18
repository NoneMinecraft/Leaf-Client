package net.nonemc.leaf.launch.data.modernui.clickgui.style;

import net.nonemc.leaf.launch.data.modernui.clickgui.Panel;
import net.nonemc.leaf.launch.data.modernui.clickgui.elements.ButtonElement;
import net.nonemc.leaf.launch.data.modernui.clickgui.elements.ModuleElement;
import net.nonemc.leaf.utils.MinecraftInstance;

public abstract class Style extends MinecraftInstance {

    public abstract void drawPanel(final int mouseX, final int mouseY, final Panel panel);

    public abstract void drawDescription(final int mouseX, final int mouseY, final String text);

    public abstract void drawButtonElement(final int mouseX, final int mouseY, final ButtonElement buttonElement);

    public abstract void drawModuleElement(final int mouseX, final int mouseY, final ModuleElement moduleElement);

}
