package net.nonemc.leaf.launch.data.modernui.clickgui.fonts.logo;

import net.nonemc.leaf.launch.data.modernui.clickgui.fonts.api.FontManager;
import net.nonemc.leaf.launch.data.modernui.clickgui.fonts.impl.SimpleFontManager;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.tenacity.SideGui.SideGui;

public class info {
    public static String Name = "";

    public static String version = "";
    public static String username;
    private final SideGui sideGui = new SideGui();
    private static info INSTANCE;

    public SideGui getSideGui() {
        return sideGui;
    }

    public static info getInstance() {
        try {
            if (INSTANCE == null) INSTANCE = new info();
            return INSTANCE;
        } catch (Throwable t) {
            throw t;
        }
    }

    public static FontManager fontManager = SimpleFontManager.create();

    public static FontManager getFontManager() {
        return fontManager;
    }
}