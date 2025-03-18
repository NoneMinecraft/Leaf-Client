//Test
package net.nonemc.leaf;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.nonemc.leaf.ui.font.Fonts;

import static net.nonemc.leaf.KeyKt.loadBanKey;
import static net.nonemc.leaf.features.module.modules.rage.rage.render.OtherKt.drawText;
import static net.nonemc.leaf.utils.render.RenderUtils.drawRect;
import static net.nonemc.leaf.utils.render.SmoothRenderUtils.drawRoundedRect;
import static net.nonemc.leaf.utils.render.SmoothRenderUtils.mc;

public class Key {
public static void drawBan(int mouseX , int mouseY){
    if (loadBanKey() > 1000) {
        ScaledResolution sr = new ScaledResolution(mc);
        int screenWidth = sr.getScaledWidth();
        final int redBarHeight = 15;
        drawRect(0, 0, screenWidth, redBarHeight, rgbaToColor(154, 0, 0, 255));

        boolean isHoveringBanBar = mouseX >= 0 && mouseX <= screenWidth &&
                mouseY >= 0 && mouseY <= redBarHeight;
        if (isHoveringBanBar) {
            final String warningText = "由于作弊违规,该账号已被安全检查封禁";
            final int textWidth = mc.fontRendererObj.getStringWidth(warningText);
            final int textHeight = 8;

            final int horizontalPadding = 16;
            final int verticalPadding = 10;
            final int panelWidth = textWidth + horizontalPadding * 2;
            final int panelHeight = textHeight + verticalPadding * 2;

            final int panelX = (screenWidth - panelWidth) / 2;
            final int panelY = redBarHeight + 5;


            drawRoundedRect(
                    panelX,
                    panelY,
                    panelX + panelWidth,
                    panelY + panelHeight,
                    2,
                    rgbaToColor(0, 0, 0, 170)
            );

            final int textX = panelX + horizontalPadding;
            final int textY = panelY + (panelHeight - textHeight) / 2;

            mc.fontRendererObj.drawStringWithShadow(
                    warningText,
                    textX,
                    textY,
                    0xFFFFFFFF
            );
        }
        String text = "VAC  ( Valve 反作弊 ) ";
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int x = (screenWidth - textWidth) / 2;
        int y = 4;

        drawText(Fonts.noto,text,x,y,255,255,255);
    }else if (!MainValue.Companion.getCanRun()){
        ScaledResolution sr = new ScaledResolution(mc);
        int screenWidth = sr.getScaledWidth();
        final int yellowBarHeight = 15;
        drawRect(0, 0, screenWidth, yellowBarHeight, rgbaToColor(210, 210, 0, 255));

        boolean isHoveringBanBar = mouseX >= 0 && mouseX <= screenWidth &&
                mouseY >= 0 && mouseY <= yellowBarHeight;
        if (isHoveringBanBar) {
            final String warningText = "VAC已将您的游戏行为标记为违规.";
            final int textWidth = mc.fontRendererObj.getStringWidth(warningText);
            final int textHeight = 8;

            final int horizontalPadding = 16;
            final int verticalPadding = 10;
            final int panelWidth = textWidth + horizontalPadding * 2;
            final int panelHeight = textHeight + verticalPadding * 2;

            final int panelX = (screenWidth - panelWidth) / 2;
            final int panelY = yellowBarHeight + 5;


            drawRoundedRect(
                    panelX,
                    panelY,
                    panelX + panelWidth,
                    panelY + panelHeight,
                    2,
                    rgbaToColor(0, 0, 0, 170)
            );

            final int textX = panelX + horizontalPadding;
            final int textY = panelY + (panelHeight - textHeight) / 2;

            mc.fontRendererObj.drawStringWithShadow(
                    warningText,
                    textX,
                    textY,
                    0xFFFFFFFF
            );
        }
        String text = "全球冷却时间  永久";
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int x = (screenWidth - textWidth) / 2;
        int y = 4;

        drawText(Fonts.noto,text,x,y,255,255,255);

    }
}
    public static int rgbaToColor(int r, int g, int b, int a) {
        r = MathHelper.clamp_int(r, 0, 255);
        g = MathHelper.clamp_int(g, 0, 255);
        b = MathHelper.clamp_int(b, 0, 255);
        a = MathHelper.clamp_int(a, 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}