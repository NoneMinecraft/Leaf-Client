package net.nonemc.leaf.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Fonts {

    @FontDetails(fontName = "Light", fontSize = 32, fileName = "regular.ttf")
    public static GameFontRenderer font32;
    @FontDetails(fontName = "superLight", fontSize = 28, fileName = "regular.ttf")
    public static GameFontRenderer font28;

    public static TTFFontRenderer fontVerdana;

    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static GameFontRenderer font35;

    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static GameFontRenderer font40;

    @FontDetails(fontName = "Roboto Medium", fontSize = 72)
    public static GameFontRenderer font72;

    @FontDetails(fontName = "Roboto Medium", fontSize = 30)
    public static GameFontRenderer fontSmall;

    @FontDetails(fontName = "Roboto Medium", fontSize = 24)
    public static GameFontRenderer fontTiny;

    @FontDetails(fontName = "Roboto Medium", fontSize = 52)
    public static GameFontRenderer fontLarge;

    @FontDetails(fontName = "SF", fontSize = 35)
    public static GameFontRenderer fontSFUI35;

    @FontDetails(fontName = "SF", fontSize = 40)
    public static GameFontRenderer fontSFUI40;

    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static GameFontRenderer fontBold180;

    @FontDetails(fontName = "Tahoma", fontSize = 35)
    public static GameFontRenderer fontTahoma;

    @FontDetails(fontName = "Tahoma", fontSize = 30)
    public static GameFontRenderer fontTahoma30;

    public static TTFFontRenderer fontTahomaSmall;

    @FontDetails(fontName = "Bangers", fontSize = 45)
    public static GameFontRenderer fontBangers;

    @FontDetails(fontName = "ICONFONT_50", fontSize = 50)
    public static GameFontRenderer ICONFONT_50;

    @FontDetails(fontName = "SF", fontSize = 40)
    public static GameFontRenderer SF;

    @FontDetails(fontName = "SFUI40", fontSize = 20)
    public static GameFontRenderer SFUI40;

    @FontDetails(fontName = "SFUI35", fontSize = 18)
    public static GameFontRenderer SFUI35;

    @FontDetails(fontName = "SFUI24", fontSize = 10)
    public static GameFontRenderer SFUI24;

    @FontDetails(fontName = "Icon", fontSize = 18)
    public static GameFontRenderer icon18;

    @FontDetails(fontName = "Icon", fontSize = 15)
    public static GameFontRenderer icon15;

    @FontDetails(fontName = "Icon", fontSize = 10)
    public static GameFontRenderer icon10;

    @FontDetails(fontName = "Notosanskr-regular", fontSize = 18)
    public static GameFontRenderer noto;

    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = Minecraft.getMinecraft().fontRendererObj;

    @FontDetails(fontName = "jello40", fontSize = 40)
    public static GameFontRenderer fontJello40;

    @FontDetails(fontName = "Jello30", fontSize = 30)
    public static GameFontRenderer fontJello30;

    private static final List<GameFontRenderer> CUSTOM_FONT_RENDERERS = new ArrayList<>();

    public static void loadFonts() {
        long l = System.currentTimeMillis();;

        noto = new GameFontRenderer(getFont("notosanskr-regular.ttf", 35));
        font35 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 35));
        font40 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 40));
        font72 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 72));
        fontSmall = new GameFontRenderer(getFont("Roboto-Medium.ttf", 30));
        fontTiny = new GameFontRenderer(getFont("Roboto-Medium.ttf", 24));
        fontLarge = new GameFontRenderer(getFont("Roboto-Medium.ttf", 60));

        fontSFUI35 = new GameFontRenderer(getFont("sfui.ttf", 35));
        fontSFUI40 = new GameFontRenderer(getFont("sfui.ttf", 40));
        ICONFONT_50 = new GameFontRenderer(getFont("stylesicons.ttf", 50));
        SF = new GameFontRenderer(getFont("SF.ttf", 20));
        SFUI40 = new GameFontRenderer(getFont("sfui.ttf", 20));
        SFUI35 = new GameFontRenderer(getFont("sfui.ttf", 18));
        SFUI24 = new GameFontRenderer(getFont("sfui.ttf", 10));
        fontSFUI35 = new GameFontRenderer(getFont("sf.ttf", 35));
        fontSFUI40 = new GameFontRenderer(getFont("sf.ttf", 40));
        fontBold180 = new GameFontRenderer(getFont("Roboto-Bold.ttf", 180));
        fontTahomaSmall = new TTFFontRenderer(getFont("Tahoma.ttf", 11));
        fontVerdana = new TTFFontRenderer(getFont("Verdana.ttf", 7));
        fontBangers = new GameFontRenderer(getFont("Bangers-Regular.ttf", 45));
        icon18 = new GameFontRenderer(getFontcustom(18, "Icon"));
        icon15 = new GameFontRenderer(getFontcustom(15, "Icon"));
        icon10 = new GameFontRenderer(getFontcustom(10, "Icon"));
        fontTahoma = new GameFontRenderer(getFont("Tahoma.ttf", 35));
        fontTahoma30 = new GameFontRenderer(getFont("Tahoma.ttf", 30));
        fontBangers = new GameFontRenderer(getFont("Bangers.ttf", 45));
        fontJello30 = new GameFontRenderer(getFont("jello.ttf", 30));
        fontJello40 = new GameFontRenderer(getFont("jello.ttf", 40));

        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                if (fontDetails != null) {
                    if (!fontDetails.fileName().isEmpty())
                        field.set(null, new GameFontRenderer(getFont(fontDetails.fileName(), fontDetails.fontSize())));
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Font getFontcustom(int size, String fontname) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("leaf/font/" + fontname + ".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    public static FontRenderer getFontRenderer(final String name, final int size) {
        if (name.equals("Minecraft")) {
            return minecraftFont;
        }

        for (final FontRenderer fontRenderer : getFonts()) {
            if (fontRenderer instanceof GameFontRenderer) {
                GameFontRenderer liquidFontRenderer = (GameFontRenderer) fontRenderer;
                final Font font = liquidFontRenderer.getDefaultFont().getFont();

                if (font.getName().equals(name) && font.getSize() == size)
                    return liquidFontRenderer;
            }
        }

        return minecraftFont;
    }

    public static Object[] getFontDetails(final FontRenderer fontRenderer) {
        if (fontRenderer instanceof GameFontRenderer) {
            final Font font = ((GameFontRenderer) fontRenderer).getDefaultFont().getFont();
            return new Object[]{font.getName(), font.getSize()};
        }

        return new Object[]{"Minecraft", -1};
    }

    public static List<FontRenderer> getFonts() {
        final List<FontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof FontRenderer) fonts.add((FontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }
    private static Font getFont(final String fontName, final int size) {
        return new Font("default", Font.PLAIN, size);
    }
}