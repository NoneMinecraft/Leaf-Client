package net.nonemc.leaf.libs.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public enum SmoothRenderUtils {
    INSTANCE;
    public static Minecraft mc;

    public static boolean isHovering(final int n, final int n2, final float n3, final float n4, final float n5, final float n6) {
        final boolean b = n > n3 && n < n5 && n2 > n4 && n2 < n6;
        return b;
    }

    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public static int rainbow(final int delay) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 10.0);
        return Color.getHSBColor((float) ((rainbow %= 360.0) / 360.0), 0.5f, 1.0f).getRGB();
    }

    public static int rainbow(final int delay, final float slowspeed) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / (double) slowspeed);
        return Color.getHSBColor((float) ((rainbow %= 360.0) / 360.0), 0.5f, 1.0f).getRGB();
    }

    public static void drawBorderedRect(final float x, final float y, final float x1, final float y1, final float width, final int borderColor) {
        r2DUtils.enableGL2D();
        glColor(borderColor);
        r2DUtils.drawRect(x + width, y, x1 - width, y + width);
        r2DUtils.drawRect(x, y, x + width, y1);
        r2DUtils.drawRect(x1 - width, y, x1, y1);
        r2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
        r2DUtils.disableGL2D();
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        drawImage(image, x, y, width, height, 1.0f);
    }

    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height, final float alpha) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height, final Color color) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255.0f, color.getBlue() / 255.0f, color.getRed() / 255.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawRect(float left, float top, float right, float bottom, final int color) {
        if (left < right) {
            final float f3 = left;
            left = right;
            right = f3;
        }
        if (top < bottom) {
            final float f3 = top;
            top = bottom;
            bottom = f3;
        }
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer WorldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f4, f5, f6, f3);
        WorldRenderer.begin(7, DefaultVertexFormats.POSITION);
        WorldRenderer.pos(left, bottom, 0.0).endVertex();
        WorldRenderer.pos(right, bottom, 0.0).endVertex();
        WorldRenderer.pos(right, top, 0.0).endVertex();
        WorldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void color(final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static void setColor(final Color c) {
        GL11.glColor4d(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
    }
    public static void arc(final float x, final float y, final float start, final float end, final float radius, final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final int color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        final Tessellator var15 = Tessellator.getInstance();
        final WorldRenderer var16 = var15.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(final double x2, final double d2, final double x22, final double e2, final float l1, final int col1, final int col2) {
        drawRect(x2, d2, x22, e2, col2);
        final float f2 = (col1 >> 24 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f4 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f5 = (col1 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRect(double d, double e, double g, double h, final int color) {
        if (d < g) {
            final int i = (int) d;
            d = g;
            g = i;
        }
        if (e < h) {
            final int j = (int) e;
            e = h;
            h = j;
        }
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f4, f5, f6, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(d, h, 0.0).endVertex();
        worldrenderer.pos(g, h, 0.0).endVertex();
        worldrenderer.pos(g, e, 0.0).endVertex();
        worldrenderer.pos(d, e, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }

    public static class r2DUtils {
        public static void enableGL2D() {
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(true);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glHint(3155, 4354);
        }

        public static void disableGL2D() {
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
            GL11.glDisable(2848);
            GL11.glHint(3154, 4352);
            GL11.glHint(3155, 4352);
        }

        public static void drawRect(double x2, double y2, double x1, double y1, int color) {
            r2DUtils.enableGL2D();
            r2DUtils.glColor(color);
            r2DUtils.drawRect(x2, y2, x1, y1);
            r2DUtils.disableGL2D();
        }

        private static void drawRect(double x2, double y2, double x1, double y1) {
            GL11.glBegin(7);
            GL11.glVertex2d(x2, y1);
            GL11.glVertex2d(x1, y1);
            GL11.glVertex2d(x1, y2);
            GL11.glVertex2d(x2, y2);
            GL11.glEnd();
        }

        public static void glColor(int hex) {
            float alpha = (float) (hex >> 24 & 255) / 255.0f;
            float red = (float) (hex >> 16 & 255) / 255.0f;
            float green = (float) (hex >> 8 & 255) / 255.0f;
            float blue = (float) (hex & 255) / 255.0f;
            GL11.glColor4f(red, green, blue, alpha);
        }

        public static void drawRect(float x, float y, float x1, float y1, int color) {
            r2DUtils.enableGL2D();
            glColor(color);
            r2DUtils.drawRect(x, y, x1, y1);
            r2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
            r2DUtils.enableGL2D();
            glColor(borderColor);
            r2DUtils.drawRect(x + width, y, x1 - width, y + width);
            r2DUtils.drawRect(x, y, x + width, y1);
            r2DUtils.drawRect(x1 - width, y, x1, y1);
            r2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
            r2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
            r2DUtils.enableGL2D();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            r2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
            r2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
            r2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
            r2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
            r2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            r2DUtils.disableGL2D();
        }

        public static void drawHLine(float x, float y, float x1, int y1) {
            if (y < x) {
                float var5 = x;
                x = y;
                y = var5;
            }
            r2DUtils.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
        }

        public static void drawVLine(float x, float y, float x1, int y1) {
            if (x1 < y) {
                float var5 = y;
                y = x1;
                x1 = var5;
            }
            r2DUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
        }

        public static void drawRect(float x, float y, float x1, float y1) {
            GL11.glBegin(7);
            GL11.glVertex2f(x, y1);
            GL11.glVertex2f(x1, y1);
            GL11.glVertex2f(x1, y);
            GL11.glVertex2f(x, y);
            GL11.glEnd();
        }
    }
}
