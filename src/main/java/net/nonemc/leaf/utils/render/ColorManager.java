package net.nonemc.leaf.utils.render;

import java.awt.*;

public class ColorManager {
    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        rainbowState %= 360.0D;
        return Color.getHSBColor((float) (rainbowState / 360.0D), 0.8F, 0.7F).brighter().getRGB();
    }

    public static int as() {
        int[] counter;
        int[] arrn = counter = new int[]{0};
        arrn[0] = arrn[0] + 1;
        return ColorManager.getRainbow3(counter[0] * 20);
    }

    public static int getRainbow3(int tick) {
        double d = 0;
        double delay = Math.ceil((double) ((System.currentTimeMillis() + (tick * 2L)) / 5L));
        float rainbow = (double) ((float) (d / 360.0)) < 0.5 ? -((float) (delay / 360.0)) : (float) ((delay %= 360.0) / 360.0);
        return Color.getHSBColor(rainbow, 0.5f, 1.0f).getRGB();
    }

    public static Color rainbow(long time, float count, float fade) {
        float hue = ((float) time + (1.0f + count) * 2.0E8f) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);
    }
}
