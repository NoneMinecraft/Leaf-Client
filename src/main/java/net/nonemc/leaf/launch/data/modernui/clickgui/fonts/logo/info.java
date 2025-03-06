package net.nonemc.leaf.launch.data.modernui.clickgui.fonts.logo;

import net.nonemc.leaf.launch.data.modernui.clickgui.fonts.api.FontManager;
import net.nonemc.leaf.launch.data.modernui.clickgui.fonts.impl.SimpleFontManager;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.tenacity.SideGui.SideGui;

public class info {
    public static String Name = "";

    public static String version = "";
    public static String username;
    public static FontManager fontManager = SimpleFontManager.create();
    private static info INSTANCE;
    private final SideGui sideGui = new SideGui();

    public static info getInstance() {
        try {
            if (INSTANCE == null) INSTANCE = new info();
            return INSTANCE;
        } catch (Throwable t) {
            throw t;
        }
    }

    public static FontManager getFontManager() {
        return fontManager;
    }

    public SideGui getSideGui() {
        return sideGui;
    }
}