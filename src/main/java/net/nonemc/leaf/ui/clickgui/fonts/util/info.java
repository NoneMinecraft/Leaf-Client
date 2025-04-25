
package net.nonemc.leaf.ui.clickgui.fonts.util;

import net.nonemc.leaf.ui.clickgui.fonts.api.FontManager;
import net.nonemc.leaf.ui.clickgui.fonts.impl.SimpleFontManager;

public class info {

    public static String version = "";
    private static info INSTANCE;
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