package net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.light.ModuleSettings;

import net.nonemc.leaf.ui.font.Fonts;
import net.nonemc.leaf.ui.font.GameFontRenderer;
import net.nonemc.leaf.value.*;

public abstract class Setting {
    protected final GameFontRenderer font = Fonts.font40;

    protected final boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    /**
     * Draw the ListValue
     */
    public abstract void drawListValue(boolean previousMouse, int mouseX, int mouseY, float mY, float startX, ListValue listValue);

    /**
     * Draw the TextValue
     */
    public abstract void drawTextValue(float startX, float mY, TextValue textValue);

    /**
     * Draw the FloatValue
     */
    public abstract void drawFloatValue(int mouseX, float mY, float startX, boolean previousMouse, boolean buttonDown, FloatValue floatValue);

    /**
     * Draw the IntegerValue
     */
    public abstract void drawIntegerValue(int mouseX, float mY, float startX, boolean previousMouse, boolean buttonDown, IntegerValue integerValue);

    /**
     * Draw the BoolValue
     */
    public abstract void drawBoolValue(boolean mouse, int mouseX, int mouseY, float startX, float mY, BoolValue boolValue);

    /**
     * Draw the ColorValue
     */
    public abstract void drawColorValue(float startX, float mY, float x, int mouseX, int mouseY, Value.ColorValue colorValue);

}