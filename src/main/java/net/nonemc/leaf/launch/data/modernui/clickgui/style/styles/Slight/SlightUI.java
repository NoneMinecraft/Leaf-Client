package net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.Slight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.launch.data.modernui.clickgui.Panel;
import net.nonemc.leaf.launch.data.modernui.clickgui.elements.ButtonElement;
import net.nonemc.leaf.launch.data.modernui.clickgui.elements.ModuleElement;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.Style;
import net.nonemc.leaf.ui.font.Fonts;
import net.nonemc.leaf.ui.font.GameFontRenderer;
import net.nonemc.leaf.utils.MinecraftInstance;
import net.nonemc.leaf.utils.render.Colors;
import net.nonemc.leaf.utils.timer.TimerUtil;
import net.nonemc.leaf.value.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.Slight.RenderUtil.*;

public class SlightUI extends Style implements GuiYesNoCallback {

    public static ModuleCategory currentModuleType = ModuleCategory.COMBAT;
    public static Module currentModule = Leaf.moduleManager.getModuleInCategory(currentModuleType).get(0);
    public static float startX = 100.0F;
    public static float startY = 85.0F;
    public static Map doubleValueMap = new HashMap();
    public static Map IntValueMap = new HashMap();
    public float moduleStart = 0.0F;
    public int valueStart = 0;
    public Opacity opacity = new Opacity(0);
    public int opacityx = 255;
    public float animationopacity = 0.0F;
    public float animationMN = 0.0F;
    public float animationX = 0.0F;
    public float animationY = 0.0F;
    public float moveX = 0.0F;
    public float moveY = 0.0F;
    public GameFontRenderer LogoFont;
    public ArrayList modBooleanValue;
    public ArrayList modModeValue;
    public ArrayList modDoubleValue;
    public ArrayList modIntValue;
    boolean previousmouse = true;
    boolean mouse;
    boolean MIND = false;
    boolean bind;
    TimerUtil AnimationTimer;
    float animationDWheel;
    int finheight;
    float animheight;
    private Color buttonColor = new Color(0, 0, 0);
    private boolean isDraging;
    private boolean clickNotDraging;

    public SlightUI() {
        this.LogoFont = Fonts.fontSFUI35;
        this.bind = false;
        this.AnimationTimer = new TimerUtil();
        this.animheight = 0.0F;
        this.modBooleanValue = new ArrayList();
        this.modModeValue = new ArrayList();
        this.modDoubleValue = new ArrayList();
        this.modIntValue = new ArrayList();
    }

    @Override
    public void drawPanel(int mouseX, int mouseY, Panel panel) {
        if (this.isHovered(startX - 10.0F, startY - 40.0F, startX + 280.0F, startY + 25.0F, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (this.moveX == 0.0F && this.moveY == 0.0F) {
                this.moveX = (float) mouseX - startX;
                this.moveY = (float) mouseY - startY;
            } else {
                startX = (float) mouseX - this.moveX;
                startY = (float) mouseY - this.moveY;
            }

            this.previousmouse = true;
        } else if (this.moveX != 0.0F || this.moveY != 0.0F) {
            this.moveX = 0.0F;
            this.moveY = 0.0F;
        }

        float scale = 1.0F;
        ScaledResolution sr = new ScaledResolution(mc);

        if (sr.getScaledHeight() > 420 && sr.getScaledWidth() > 570) {
            scale = 1.0F;
        } else {
            scale = 0.8F;
        }

        this.opacity.interpolate((float) this.opacityx);
        boolean countMod = false;
        int[] counter = new int[1];
        int rainbowCol = UISlider.rainbow(System.nanoTime() * 3L, (float) counter[0], 1.0F).getRGB();
        Color col = new Color(rainbowCol);
        int Ranbow = (new Color(0, col.getGreen() / 3 + 40, col.getGreen() / 2 + 100)).getRGB();
        int Ranbow1 = (new Color(0, col.getGreen() / 4 + 20, col.getGreen() / 2 + 100)).getRGB();

        drawDimRect(startX - 40.0F, startY - 10.0F, startX + 300.0F, startY + 260.0F, Colors.getColor(32, 32, 32));
        drawGradientRect2(startX - 40.0F, startY - 12.0F, startX + 300.0F, startY - 10.0F, Ranbow, (new Color(4555775)).getRGB());
        RenderUtil.drawRect(startX + 65.0F, startY + 25.0F, startX + 165.0F, startY + 30.0F, (new Color(25, 145, 220)).getRGB());

        int m;

        for (m = 0; m < ModuleCategory.values().length; ++m) {
            ModuleCategory[] mY = ModuleCategory.values();

            if (mY[m] == currentModuleType) {
                this.finheight = m * 30;
                drawGradientRect2(startX - 30.0F, startY + 30.0F + this.animheight, startX - 29.0F, startY + 40.0F + this.animheight, Ranbow, (new Color(4555775)).getRGB());
                this.animheight = (float) getAnimationState(this.animheight, this.finheight, Math.max(100.0F, Math.abs((float) this.finheight - this.animheight) * 10.0F));
                if (this.animheight == (float) this.finheight) {
                    Fonts.fontSFUI35.drawString(mY[m].name(), startX - 25.0F, startY + 30.0F + (float) (m * 30), (new Color(255, 255, 255)).getRGB());
                } else {
                    Fonts.fontSFUI35.drawString(mY[m].name(), startX - 25.0F, startY + 30.0F + (float) (m * 30), (new Color(196, 196, 196)).getRGB());
                }
            } else {
                RenderUtil.drawRect(startX - 25.0F, startY + 50.0F + (float) (m * 30), startX + 60.0F, startY + 75.0F + (float) (m * 30), (new Color(255, 255, 255, 0)).getRGB());
                Fonts.fontSFUI35.drawString(mY[m].name(), startX - 25.0F, startY + 30.0F + (float) (m * 30), (new Color(196, 196, 196)).getRGB());
            }

            try {
                if (this.isCategoryHovered(startX - 40.0F, startY + 20.0F + (float) (m * 30), startX + 60.0F, startY + 45.0F + (float) (m * 40), mouseX, mouseY) && Mouse.isButtonDown(0) && !this.MIND) {
                    currentModuleType = mY[m];
                    currentModule = Leaf.moduleManager.getModuleInCategory(currentModuleType).size() != 0 ? Leaf.moduleManager.getModuleInCategory(currentModuleType).get(0) : null;
                    this.moduleStart = 0.0F;
                }
            } catch (Exception exception) {
                System.err.println(exception);
            }
        }

        m = Mouse.getDWheel();
        if (this.isCategoryHovered(startX + 60.0F, startY, startX + 200.0F, startY + 235.0F, mouseX, mouseY) && !this.MIND) {
            if (m < 0 && this.moduleStart < (float) (Leaf.moduleManager.getModuleInCategory(currentModuleType).size() - 1)) {
                ++this.moduleStart;
                this.animationDWheel = (float) getAnimationState(this.animationDWheel, 1.0D, 50.0D);
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.2F, 2.0F);
            }

            if (m > 0 && this.moduleStart > 0.0F) {
                --this.moduleStart;
                this.moduleStart = (float) getAnimationState(this.moduleStart, -1.0D, 50.0D);
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.2F, 2.0F);
            }
        } else {
            this.animationDWheel = 0.0F;
        }

        if (this.isCategoryHovered(startX - 40.0F, startY - 10.0F, startX + 300.0F, startY + 240.0F, mouseX, mouseY) && this.MIND) {
            if (m < 0 && this.valueStart < currentModule.getValues().size() - 1) {
                ++this.valueStart;
            }

            if (m > 0 && this.valueStart > 0) {
                --this.valueStart;
            }
        }

        if (currentModule != null) {
            this.modBooleanValue.clear();
            this.modModeValue.clear();
            this.modDoubleValue.clear();
            this.modIntValue.clear();
            float f = startY + 30.0F;

            int font;
            RenderUtil RenderUtil = null;
            for (font = 0; font < Leaf.moduleManager.getModuleInCategory(currentModuleType).size(); ++font) {
                Module value = Leaf.moduleManager.getModuleInCategory(currentModuleType).get(font);

                if (f > startY + 220.0F) {
                    break;
                }

                if ((float) font >= this.moduleStart) {
                    drawRect(startX + 75.0F, f, startX + 185.0F, f + 2.0F, (new Color(246, 246, 246, 100)).getRGB());
                    if (!value.getState()) {
                        drawDimRect(startX + 50.0F, f - 5.0F, startX + 285.0F, f + 20.0F, (new Color(38, 38, 37)).getRGB());
                        if (SlightUI.currentModule.getValues().size() > 0) {
                            drawDimRect(startX + 270.0F, f - 5.0F, startX + 285.0F, f + 20.0F, (new Color(44, 44, 45)).getRGB());
                            circle(startX + 277.0F, f + 2.0F, 0.7F, new Color(95, 95, 95));
                            circle(startX + 277.0F, f + 7.0F, 0.7F, new Color(95, 95, 95));
                            circle(startX + 277.0F, f + 12.0F, 0.7F, new Color(95, 95, 95));
                        }
                    } else {
                        drawDimRect(startX + 50.0F, f - 5.0F, startX + 285.0F, f + 20.0F, (new Color(55, 55, 55)).getRGB());
                    }

                    if (this.isSettingsButtonHovered(startX + 65.0F, f, startX + 285.0F, f + 8.0F + Fonts.fontSFUI35.getStringWidth(""), mouseX, mouseY) && !this.MIND) {
                        this.animationopacity = (float) getAnimationState(this.animationopacity, 0.30000001192092896D, 20.0D);
                        this.animationMN = (float) getAnimationState(this.animationMN, 10.0D, 100.0D);
                        if (!value.getState()) {
                            Fonts.fontSFUI35.drawString(value.getName(), startX + 70.0F + this.animationMN, f + 4.0F, (new Color(240, 240, 240)).getRGB(), false);
                        } else {
                            Fonts.fontSFUI35.drawString(value.getName(), startX + 70.0F + this.animationMN, f + 4.0F, (new Color(255, 255, 255)).getRGB(), false);
                        }
                    } else {
                        this.animationopacity = (float) getAnimationState(this.animationopacity, 0.0D, 20.0D);
                        this.animationMN = (float) getAnimationState(this.animationMN, 0.0D, 100.0D);
                        if (value.getState()) {
                            Fonts.fontSFUI35.drawString(value.getName(), startX + 70.0F + this.animationMN, f + 4.0F, (new Color(200, 200, 200)).getRGB(), false);
                        } else {
                            Fonts.fontSFUI35.drawString(value.getName(), startX + 70.0F + this.animationMN, f + 4.0F, (new Color(190, 190, 190)).getRGB(), false);
                        }
                    }

                    drawRect(startX + 50.0F, f - 5.0F, startX + 285.0F, f + 20.0F, reAlpha(Colors.WHITE.c, this.animationopacity));
                    if (value.getState()) {
                        drawGradientRect2(startX + 50.0F, f - 5.0F, startX + 51.0F, f + 20.0F, Ranbow, (new Color(4555775)).getRGB());
                    }

                    if (this.isSettingsButtonHovered(startX + 65.0F, f, startX + 285.0F, f + 8.0F + Fonts.fontSFUI35.getStringWidth(""), mouseX, mouseY) && !this.MIND) {
                        if (!this.previousmouse && Mouse.isButtonDown(0)) {
                            value.setState(!value.getState());

                            this.previousmouse = true;
                        }

                        if (!this.previousmouse && Mouse.isButtonDown(1)) {
                            this.previousmouse = true;
                        }
                    }

                    if (!Mouse.isButtonDown(0) && !this.MIND) {
                        this.previousmouse = false;
                    }

                    if (this.isSettingsButtonHovered(startX + 65.0F, f, startX + 285.0F + Fonts.fontSFUI35.getStringWidth(value.getName()), f + 8.0F + Fonts.fontSFUI35.getStringWidth(""), mouseX, mouseY) && Mouse.isButtonDown(1) && !this.MIND) {
                        currentModule = value;
                        Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5F, 4.0F);
                        this.valueStart = 0;
                        this.MIND = true;
                    }

                    f += 30.0F;
                }
            }

            for (font = 0; font < currentModule.getValues().size(); ++font) {

                Value value = currentModule.getValues().get(font);

                if (value instanceof BoolValue) {
                    this.modBooleanValue.add(value);
                }

                if (value instanceof ListValue) {
                    this.modModeValue.add(value);
                }

                if (value instanceof FloatValue) {
                    this.modDoubleValue.add(value);
                }

                if (value instanceof IntegerValue) {
                    this.modIntValue.add(value);
                }
            }

            f = startY + 12.0F;
            if (this.MIND) {
                if (this.isCategoryHovered(startX - 40.0F, startY - 10.0F, startX + 300.0F, startY + 240.0F, mouseX, mouseY)) {
                    if (m < 0 && this.valueStart < (currentModule.getValues().size() - 1) * 12) {
                        this.valueStart += 12;
                    }

                    if (m > 0 && this.valueStart - 12 >= 0) {
                        this.valueStart -= 12;
                    } else if (m > 0) {
                        this.valueStart = 0;
                    }
                }

                if (this.animationX == 0.0F) {
                }

                this.animationX = (float) getAnimationState(this.animationX, 390.0D, 600.0D);
                this.animationY = (float) getAnimationState(this.animationY, 120.0D, 800.0D);
                GL11.glPushMatrix();
                GL11.glEnable(3089);
                doGlScissor((int) (startX - 40.0F), 0, (int) this.animationX, height());
                drawDimRect(startX - 40.0F, startY - 10.0F, startX + 300.0F, startY + 8.0F, Colors.getColor(44, 44, 45));
                drawDimRect(startX - 40.0F, startY + 8.0F, startX + 300.0F, startY + 260.0F, Colors.getColor(37, 37, 38));
                circle(startX + 292.0F, startY - 4.0F, 4.0F, (new Color(-14848033)).brighter());
                if (this.isSettingsButtonHovered(startX + 288.0F, startY - 6.0F, startX + 344.0F, startY + 2.0F, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    this.MIND = false;
                }

                GameFontRenderer gamefontrenderer = Fonts.fontSFUI35;

                Fonts.fontSFUI35.drawString(currentModule.getName(), startX - 35.0F, startY - 8.0F, -1);
                GL11.glPushMatrix();
                if (this.animationX == 390.0F) {
                    doGlScissor((int) startX - 40, (int) startY + 8, (int) startX + 300, height());
                }

                float x;
                Iterator iterator;

                for (iterator = this.modBooleanValue.iterator(); iterator.hasNext(); f += 20.0F) {
                    BoolValue value1 = (BoolValue) iterator.next();

                    if (f - (float) this.valueStart > startY + 220.0F) {
                        break;
                    }

                    Gui.drawRect(1, 1, 1, 1, -1);
                    x = startX + 250.0F;
                    gamefontrenderer.drawString(value1.getName(), startX - 30.0F, f - (float) this.valueStart, (new Color(255, 255, 255)).getRGB());
                    if (value1.getValue().booleanValue()) {
                        this.buttonColor = (new Color(-14848033)).brighter();
                    } else {
                        this.buttonColor = new Color(80, 80, 80);
                    }

                    circle(x + 35.0F, f - (float) this.valueStart + 2.0F, 4.0F, this.buttonColor.getRGB());
                    if (this.isCheckBoxHovered(x + 30.0F, f - (float) this.valueStart, x + 38.0F, f - (float) this.valueStart + 9.0F, mouseX, mouseY)) {
                        if (!this.previousmouse && Mouse.isButtonDown(0)) {
                            this.previousmouse = true;
                            this.mouse = true;
                        }

                        if (this.mouse) {
                            value1.setValue(Boolean.valueOf(!value1.getValue().booleanValue()));
                            this.mouse = false;
                        }
                    }

                    if (!Mouse.isButtonDown(0)) {
                        this.previousmouse = false;
                    }
                }

                UISlider uislider;

                for (iterator = this.modDoubleValue.iterator(); iterator.hasNext(); f += 22.0F) {
                    FloatValue floatvalue = (FloatValue) iterator.next();

                    if (f - (float) this.valueStart > startY + 220.0F) {
                        break;
                    }

                    if (doubleValueMap.containsKey(floatvalue)) {
                        uislider = (UISlider) doubleValueMap.get(floatvalue);
                    } else {
                        uislider = new UISlider(floatvalue);
                        doubleValueMap.put(floatvalue, uislider);
                    }

                    uislider.drawAll(startX + 45.0F, f - (float) this.valueStart, mouseX, mouseY);
                }

                for (iterator = this.modIntValue.iterator(); iterator.hasNext(); f += 22.0F) {
                    IntegerValue integervalue = (IntegerValue) iterator.next();

                    if (f - (float) this.valueStart > startY + 220.0F) {
                        break;
                    }

                    if (IntValueMap.containsKey(integervalue)) {
                        uislider = (UISlider) IntValueMap.get(integervalue);
                    } else {
                        uislider = new UISlider(integervalue);
                        IntValueMap.put(integervalue, uislider);
                    }

                    uislider.drawAlll(startX + 45.0F, f - (float) this.valueStart, mouseX, mouseY);
                }

                for (iterator = this.modModeValue.iterator(); iterator.hasNext(); f += 20.0F) {
                    ListValue listvalue = (ListValue) iterator.next();

                    if (f - (float) this.valueStart > startY + 220.0F) {
                        break;
                    }

                    x = startX + 250.0F;
                    drawRect(x - 40.0F, f - (float) this.valueStart - 1.0F, x + 40.0F, f - (float) this.valueStart + 12.0F, (new Color(60, 60, 60)).getRGB());
                    Fonts.fontSFUI35.drawString(listvalue.getName(), startX - 30.0F, f - (float) this.valueStart, (new Color(255, 255, 255)).getRGB());
                    Fonts.fontSFUI35.drawCenteredString((String) listvalue.getValue(), x - 2.0F, f - (float) this.valueStart + 2.0F, -1);
                    if (this.isStringHovered(x - 40.0F, f - (float) this.valueStart - 1.0F, x + 40.0F, f - (float) this.valueStart + 12.0F, mouseX, mouseY)) {
                        if (Mouse.isButtonDown(0) && !this.previousmouse) {
                            String current = listvalue.getValue();

                            listvalue.set(listvalue.getValues()[listvalue.getModeListNumber(current) + 1 >= listvalue.getValues().length ? 0 : listvalue.getModeListNumber(current) + 1]);
                            this.previousmouse = true;
                        }

                        if (!Mouse.isButtonDown(0)) {
                            this.previousmouse = false;
                        }
                    }
                }

                GL11.glPopMatrix();
                GL11.glDisable(3089);
                GL11.glPopMatrix();
            } else {
                this.animationX = (float) getAnimationState(this.animationX, 0.0D, 800.0D);
                this.animationY = (float) getAnimationState(this.animationY, 0.0D, 800.0D);
            }
        }

    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public void onGuiClosed() {
        this.opacity.setOpacity(0.0F);
    }


    @Override
    public void drawDescription(int mouseX, int mouseY, String text) {

    }

    @Override
    public void drawButtonElement(int mouseX, int mouseY, ButtonElement buttonElement) {

    }

    @Override
    public void drawModuleElement(int mouseX, int mouseY, ModuleElement moduleElement) {

    }

    @Override
    public void confirmClicked(boolean b, int i) {

    }
}
