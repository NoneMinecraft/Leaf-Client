package net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.light;

import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.light.LightSettings.InputBox;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.light.ModuleSettings.Settings;
import net.nonemc.leaf.ui.client.hud.designer.GuiHudDesigner;
import net.nonemc.leaf.ui.font.Fonts;
import net.nonemc.leaf.utils.AnimationHelper;
import net.nonemc.leaf.utils.render.RenderUtils;
import net.nonemc.leaf.value.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class LightClickGUI extends GuiScreen implements GuiYesNoCallback {
    private ModuleCategory currentCategory = ModuleCategory.COMBAT;
    private Module currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
    private float startX = 50, startY = 25;
    private int moduleStart = 0;
    private int valueStart = 0;
    private boolean previousMouse = true;
    private boolean mouse;
    private float moveX = 0, moveY = 0;
    private final FontRenderer defaultFont = Fonts.font35;
    private final FontRenderer logoFont = Fonts.font40;
    private boolean rightClickMouse = false;
    private boolean categoryMouse = false;
    private int animationHeight = 0;
    private float guiScale = 0;
    private final AnimationHelper alphaAnim = new AnimationHelper();
    private final AnimationHelper valueAnim = new AnimationHelper();
    private int categoryYpos = 0;
    private InputBox searchBox;
    private boolean firstSetAnimation;
    public LightClickGUI() {
        firstSetAnimation = false;
        alphaAnim.resetAlpha();
        valueAnim.resetAlpha();
    }

    @Override
    public void initGui() {
        firstSetAnimation = false;
        alphaAnim.resetAlpha();
        valueAnim.resetAlpha();
        this.searchBox = new InputBox(1, (int)startX, (int)startY + 20, 45, 8);
    }

    @Override
    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException {
        super.mouseClicked(p_mouseClicked_1_,p_mouseClicked_2_,p_mouseClicked_3_);
        searchBox.mouseClicked(p_mouseClicked_1_,p_mouseClicked_2_,p_mouseClicked_3_);
    }

    @Override
    public void updateScreen() {
        searchBox.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
        if(typedChar == 9 && this.searchBox.isFocused()) {
            this.searchBox.setFocused(!this.searchBox.isFocused());
        }

        this.searchBox.textboxKeyTyped(typedChar, keyCode);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // yes hud designer here
        RenderUtils.drawImage(new ResourceLocation( "leaf/ui/clickgui/hud.png"), 9, height - 41, 32, 32);

        // set up bool animations?
        if(!firstSetAnimation) {
            for (Module i : Leaf.moduleManager.getModules()) {
                i.getAnimation().animationX = i.getState() ? 5 : -5;
                for(Value<?> j : i.getValues()) {
                    if(j instanceof BoolValue) {
                        BoolValue boolValue = (BoolValue) j;
                        boolValue.getAnimation().animationX = boolValue.get() ? 5 : -5;
                    }
                }
            }
            firstSetAnimation = true;
        }

        // serach box
        searchBox.xPosition = (int) startX;
        searchBox.yPosition = (int) (startY + 5);
        this.searchBox.setMaxStringLength(20);

        // animate alpha
        if(alphaAnim.getAlpha() == 250)
            alphaAnim.alpha = 255;
        else
            alphaAnim.updateAlpha(25);
        //整个界面的alpha
        if(valueAnim.getAlpha() == 240)
            alphaAnim.alpha = 255;
        else
            valueAnim.updateAlpha(30);
        //value界面的alpha

        // animate scale
        if(guiScale < 70)
            guiScale += 10;//启动动画
        GlStateManager.scale(guiScale / 100, guiScale / 100, guiScale / 100);

        // make that variable gurl
        Settings settings = new Settings(valueAnim);//value的settings

        // huddesigner icon sense clik
        if (Mouse.isButtonDown(0) && mouseX >= 5 && mouseX <= 50 && mouseY <= height - 5 && mouseY >= height - 50)
            mc.displayGuiScreen(new GuiHudDesigner()); //进入自定义HUD界面

        // click = move target hud
        if (isHovered(startX - 5 - 30, startY - 60, startX + 400 - 30, startY + 25 - 60, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            // 移动窗口
            if (moveX == 0 && moveY == 0) {
                moveX = mouseX - startX;
                moveY = mouseY - startY;
            } else {
                startX = mouseX - moveX;
                startY = mouseY - moveY;
            }
            this.previousMouse = true;
        } else if (moveX != 0 || moveY != 0) {
            moveX = 0;
            moveY = 0;
        }


        // new sexy background
        RenderUtils.drawRoundedRect2((int) startX - 5, (int) startY, (int) startX + 400, (int) startY + 310, 4,
            new Color(20, 25, 25, 250).getRGB());
        RenderUtils.drawRoundedRect2((int) startX - 5, (int) startY, (int) startX + 400, (int) startY + 25, 4,
            new Color(22, 91, 196, 250).getRGB());



            // search box time
            defaultFont.drawString(searchBox.getText().isEmpty() && !searchBox.isFocused() ? "Search..." : searchBox.getText(), (int) (startX + 3), (int) (startY + 25), new Color(80, 80, 80).getRGB());

            // no module???? holder text
            if(currentModule == null) {
                logoFont.drawStringWithShadow("No Modules Selected", startX + 80, startY + 130, new Color(100,100,100).getRGB());
            }
    //        if(isHovered(startX + 130, startY + 7, startX + 190, startY + 15, mouseX, mouseY) && Mouse.isButtonDown(0))
    //            hovered = true;
    //        else if(!isHovered(startX + 130, startY + 7, startX + 190, startY + 15, mouseX, mouseY) && Mouse.isButtonDown(0))
    //            hovered = false;
            //   FontManager.logo24.drawCenteredStringWithShadow("L", startX + 5, startY + 8, new Color(49, 86, 255).getRGB(),255);
            //FontManager.logo24.drawCenteredStringWithShadow("ight", startX + 5 + FontManager.logo24.getStringWidth("L") + 8, startY + 8, new Color(255,255,255).getRGB(),255); // 客户端名字





            // draw that lil circle that tells you what category u at /////////////////////
            RenderUtils.drawSuperCircle(startX - 5, startY + animationHeight, 5, new Color(100, 100,255).getRGB());
            animationHeight += Math.round((categoryYpos - animationHeight)/2.5);


            // mouse scroll now est variable
            int m = Mouse.getDWheel();//鼠标滚轮.

            // smth
            if(searchBox.getText().isEmpty()) {
                if (this.isCategoryHovered(startX + 60 - 30, startY + 40 - 60, startX + 200 - 30, startY + 280 - 60, mouseX, mouseY)) {
                    if (m < 0 && moduleStart < Leaf.moduleManager.getModuleInCategory(currentCategory).size() - 8) {
                        moduleStart++;
                    }
                    if (m > 0 && moduleStart > 0) {
                        moduleStart--;
                    }
                }

                if (this.isCategoryHovered(startX + 200 - 30, startY - 60, startX + 400 - 30, startY + 280 - 60, mouseX, mouseY)) {
                    if (m < 0 && valueStart < currentModule.getValues().size() - 11) {
                        valueStart++;
                    }
                    if (m > 0 && valueStart > 0) {
                        valueStart--;
                    }
                }

            }

            // draw current categorys category's name
            logoFont.drawString(currentCategory.getDisplayName(), (int) (startX + 60), (int) (startY + 10),
                    new Color(100, 100, 100,alphaAnim.getAlpha()).getRGB());
            RenderUtils.circle(startX + 390, startY + 8, 1.5F, new Color(31, 158, 255).getRGB());
            if (isCheckBoxHovered(startX + 388, startY + 6, startX + 391, startY + 9, mouseX, mouseY)
                    && Mouse.isButtonDown(0)) {
                mc.displayGuiScreen(new GuiExit());
            }


            if(!searchBox.getText().isEmpty()) {
                if (this.isCategoryHovered(startX + 60 - 30, startY + 40 - 60, startX + 200 - 30, startY + 280 - 60, mouseX, mouseY)) {
                    if (m < 0 && moduleStart < Leaf.moduleManager.getModuleInCategory(currentCategory).size() - 8) {
                        moduleStart++;
                    }
                    if (m > 0 && moduleStart > 0) {
                        moduleStart--;
                    }
                }

                if (this.isCategoryHovered(startX + 200 - 30, startY + 40 - 60, startX + 400 - 30, startY + 280 - 60, mouseX, mouseY)) {
                    if (m < 0 && valueStart < currentModule.getValues().size() - 11) {
                        valueStart++;
                    }
                    if (m > 0 && valueStart > 0) {
                        valueStart--;
                    }
            }
                float mY = startY + 30;
                for(int i = 0; i < Leaf.moduleManager.getModulesByName(searchBox.getText()).size(); i++) {
                    Module module = Leaf.moduleManager.getModulesByName(searchBox.getText()).get(i);
                    if (mY > startY + 250)
                        break;
                    if (i < moduleStart) {
                        continue;
                    }
                    int moduleColor = new Color(118, 117, 117,alphaAnim.getAlpha()).getRGB();
                    if (isSettingsButtonHovered(startX + 160, mY, startX + 180, mY + 10, mouseX, mouseY)) {
                        if (!this.previousMouse && Mouse.isButtonDown(0)) {
                            module.setState(!module.getState());
                            previousMouse = true;
                        }
                        if (!this.previousMouse && Mouse.isButtonDown(1)) {
                            previousMouse = true;
                        }
                    }
                    RenderUtils.drawRoundedRect2(startX + 160, mY + 6, startX + 180, mY + 16, 4, module.getState() && module.getAnimation().getAnimationX() >= 3F ? new Color(70, 255, 70,alphaAnim.getAlpha()).getRGB() : new Color(114, 118, 125,alphaAnim.getAlpha()).getRGB());
                    RenderUtils.circle(startX + 170 + module.getAnimation().getAnimationX(), mY + 11, 4, module.getState() ? new Color(255,255,255,alphaAnim.getAlpha()).getRGB() : new Color(164, 168, 175,alphaAnim.getAlpha()).getRGB());
                    if(module.getAnimation().getAnimationX() > -5F && !module.getState())
                        module.getAnimation().animationX -= 1F;
                    else if(module.getAnimation().getAnimationX() < 5F && module.getState())
                        module.getAnimation().animationX += 1F;
                    defaultFont.drawString(module.getName(), (int) (startX + 65), (int) (mY + 6), moduleColor);
                    defaultFont.drawString("KeyBind: " + (!Keyboard.getKeyName(module.getKeyBind()).equalsIgnoreCase("NONE") ? Keyboard.getKeyName(module.getKeyBind()) : "None"), (int) (startX + 65), (int) (mY + 15), new Color(80, 80, 80, alphaAnim.getAlpha()).getRGB());
                    if (!Mouse.isButtonDown(0)) {
                        this.previousMouse = false;
                    }
                    if (isSettingsButtonHovered(startX + 50, mY - 8, startX + 200, mY + 20, mouseX, mouseY)
                            && Mouse.isButtonDown(1) && !rightClickMouse && currentModule != module) {
                        currentModule = module;
                        valueAnim.resetAlpha();
                        valueStart = 0;
                        rightClickMouse = true;
                    }
                    if (rightClickMouse && !Mouse.isButtonDown(1))
                        rightClickMouse = false;
                    mY += 28;
                }
            }
            if (currentModule != null) {
                logoFont.drawString(currentModule.getName(), (int) (startX + 205), (int) (startY + 10),
                        new Color(100, 100, 100,valueAnim.getAlpha()).getRGB());
                float mY = startY + 30;
                if(searchBox.getText().isEmpty()) {
                    for (int i = 0; i < Leaf.moduleManager.getModuleInCategory(currentCategory).size(); i++) {
                        Module module = Leaf.moduleManager.getModuleInCategory(currentCategory).get(i);
                        if (mY > startY + 250)
                            break;
                        if (i < moduleStart)
                            continue;
                        int moduleColor = new Color(118, 117, 117,alphaAnim.getAlpha()).getRGB();
                        if (isSettingsButtonHovered(startX + 160, mY, startX + 180, mY + 10, mouseX, mouseY)) {
                            if (!this.previousMouse && Mouse.isButtonDown(0)) {
                                module.setState(!module.getState());
                                previousMouse = true;
                            }
                            if (!this.previousMouse && Mouse.isButtonDown(1)) {
                                previousMouse = true;
                            }
                        }
                        RenderUtils.drawRoundedRect2(startX + 160, mY + 6, startX + 180, mY + 16, 4, module.getState() && module.getAnimation().getAnimationX() >= 3F ? new Color(70, 255, 70,alphaAnim.getAlpha()).getRGB() : new Color(114, 118, 125,alphaAnim.getAlpha()).getRGB());
                        RenderUtils.circle(startX + 170 + module.getAnimation().getAnimationX(), mY + 11, 4, module.getState() ? new Color(255,255,255,alphaAnim.getAlpha()).getRGB() : new Color(164, 168, 175,alphaAnim.getAlpha()).getRGB());
                        if(module.getAnimation().getAnimationX() > -5F && !module.getState())
                            module.getAnimation().animationX -= 1F;
                        else if(module.getAnimation().getAnimationX() < 5F && module.getState())
                            module.getAnimation().animationX += 1F;
                        defaultFont.drawString(module.getName(), (int) (startX + 65), (int) (mY + 6), moduleColor);
                        defaultFont.drawString("KeyBind: " + (!Keyboard.getKeyName(module.getKeyBind()).equalsIgnoreCase("NONE") ? Keyboard.getKeyName(module.getKeyBind()) : "None"), (int) (startX + 65), (int) (mY + 15), /*!module.getState() ? */new Color(80, 80, 80, alphaAnim.getAlpha()).getRGB() /*: new Color(220, 220, 220).getRGB()*/);
                        if (!Mouse.isButtonDown(0)) {
                            this.previousMouse = false;
                        }

                        if (isSettingsButtonHovered(startX + 50, mY - 8, startX + 200, mY + 20, mouseX, mouseY)
                                && Mouse.isButtonDown(1) && !rightClickMouse && currentModule != module) {
                            currentModule = module;
                            valueAnim.resetAlpha();
                            valueStart = 0;
                            rightClickMouse = true;
                        }
                        if (rightClickMouse && !Mouse.isButtonDown(1))
                            rightClickMouse = false;
                        mY += 28;
                    }
                }
                mY = startY + 30;
                if (currentModule.getValues().isEmpty())
                    logoFont.drawString("No Module Selected", (int) (startX + 250), (int) (startY + 130),
                            new Color(100, 100, 100,valueAnim.getAlpha()).getRGB());
                for (int i = 0; i < currentModule.getValues().size(); i++) {
                    if (mY > startY + 260)
                        break;
                    if (i < valueStart) {
                        continue;
                    }
                    Value<?> value = currentModule.getValues().get(i);
                    if (value instanceof FloatValue) {
                        FloatValue floatValue = (FloatValue) value;
                        float x = startX + 300;
                        settings.drawFloatValue(mouseX, mY, startX, previousMouse,this.isButtonHovered(x, mY - 2, x + 100, mY + 7, mouseX, mouseY), floatValue);
                        if (!Mouse.isButtonDown(0)) {
                            this.previousMouse = false;
                        }
                        mY += 20;
                    }
                    if (value instanceof IntegerValue) {
                        IntegerValue integerValue = (IntegerValue) value;
                        float x = startX + 300;
                        settings.drawIntegerValue(mouseX, mY, startX, previousMouse, this.isButtonHovered(x, mY - 2, x + 100, mY + 7, mouseX, mouseY), integerValue);
                        if (!Mouse.isButtonDown(0)) {
                            this.previousMouse = false;
                        }
                        mY += 20;
                    }
                    if(value instanceof Value.ColorValue) {
                        Value.ColorValue colorValue = (Value.ColorValue) value;
                        settings.drawColorValue(startX,mY,startX + 300, mouseX,mouseY, colorValue);
                        if (!Mouse.isButtonDown(0)) {
                            this.previousMouse = false;
                        }
                        mY += 20;
                    }
                    if (value instanceof BoolValue) {
                        BoolValue boolValue = (BoolValue) value;
                        float x = startX + 325;
                        settings.drawBoolValue(mouse,mouseX,mouseY,startX,mY,boolValue);
                        if (this.isCheckBoxHovered(x + 30, mY - 2, x + 50, mY + 8, mouseX, mouseY)) {
                            if (!this.previousMouse && Mouse.isButtonDown(0)) {
                                this.previousMouse = true;
                                this.mouse = true;
                            }
                            if (this.mouse) {
                                boolValue.set(!boolValue.get());
                                this.mouse = false;
                            }
                        }
                        mY += 20;
                    }
                    if(value instanceof TextValue) {
                        TextValue textValue = (TextValue) value;
                        settings.drawTextValue(startX, mY, textValue);
                        mY += 20;
                    }
                    if (value instanceof ListValue) {
                        float x = startX + 295;
                        ListValue listValue = (ListValue) value;
                        settings.drawListValue(previousMouse,mouseX,mouseY,mY,startX,listValue);
                        if (this.isStringHovered(x, mY - 5, x + 80, mY + 11, mouseX, mouseY)) {
                            this.previousMouse = Mouse.isButtonDown(0);
                        }
                        mY += 25;
                    }
                }
            }

            // detect mouse clicking on categories
            if(categoryMouse && !Mouse.isButtonDown(0))
                categoryMouse = false;
        if (isCategoryHovered(startX + 11 - 30, startY + 33 - 60, startX + 25 - 30, startY + 57 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.COMBAT) {
                    currentCategory = ModuleCategory.COMBAT;
                    categoryMouse = true;
                    categoryYpos = 40;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 73 - 60, startX + 35 - 30, startY + 97 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.MOVEMENT) {
                    currentCategory = ModuleCategory.MOVEMENT;
                    categoryMouse = true;
                    categoryYpos = 80;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 113 - 60, startX + 35 - 30, startY + 137 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.WORLD) {
                    currentCategory = ModuleCategory.WORLD;
                    categoryMouse = true;
                    categoryYpos = 120;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 153 - 60, startX + 35 - 30, startY + 177 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.PLAYER) {
                    currentCategory = ModuleCategory.PLAYER;
                    categoryMouse = true;
                    categoryYpos = 160;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 193 - 60, startX + 35 - 30, startY + 217 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.EXPLOIT) {
                    currentCategory = ModuleCategory.EXPLOIT;
                    categoryMouse = true;
                    categoryYpos = 200;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 233 - 60, startX + 35 - 30, startY + 257 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.MISC) {
                    currentCategory = ModuleCategory.MISC;
                    categoryMouse = true;
                    categoryYpos = 240;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
        }

        if (isCategoryHovered(startX + 11 - 30, startY + 273 - 60, startX + 35 - 30, startY + 297 - 60, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0) && !categoryMouse) {
                if (currentCategory != ModuleCategory.CLIENT) {
                    currentCategory = ModuleCategory.CLIENT;
                    categoryMouse = true;
                    categoryYpos = 280;
                    if(searchBox.getText().isEmpty()) {
                        moduleStart = 0;
                        currentModule = Leaf.moduleManager.getModuleInCategory(currentCategory).get(0);
                        alphaAnim.resetAlpha();
                        valueAnim.resetAlpha();
                    }
                }
            }
    }

            searchBox.drawTextBox();
            // 判断category所处的位置是否被按下或者被略过
            RenderUtils.drawGradientSideways(startX + 50, startY, startX + 55, startY + 310, new Color(0, 0, 0, 60).getRGB(),
                    new Color(0, 0, 0, 0).getRGB());//255,255,255,30

            RenderUtils.drawGradientSideways(startX + 200, startY, startX + 205, startY + 310,
                    new Color(0, 0, 0, 70).getRGB(), new Color(0,0,0,0).getRGB());//239,237,237,30

            RenderUtils.circle(startX + 23, startY + 45, 12,
                    isCategoryHovered(startX + 11, startY + 33, startX + 35, startY + 57, mouseX, mouseY)
                            || currentCategory == ModuleCategory.COMBAT ? new Color(180, 180, 180).getRGB() : new Color(255,255,255).getRGB());
            RenderUtils.circle(startX + 23, startY + 85, 12,
                    isCategoryHovered(startX + 11, startY + 73, startX + 35, startY + 97, mouseX, mouseY)
                            || currentCategory == ModuleCategory.MOVEMENT ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB());
            RenderUtils.circle(startX + 23, startY + 125, 12,
                    isCategoryHovered(startX + 11, startY + 113, startX + 35, startY + 137, mouseX, mouseY)
                            || currentCategory == ModuleCategory.WORLD ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB());
            RenderUtils.circle(startX + 23, startY + 165, 12,
                    isCategoryHovered(startX + 11, startY + 153, startX + 35, startY + 177, mouseX, mouseY)
                            || currentCategory == ModuleCategory.PLAYER ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB()); // 画出category的圆
            RenderUtils.circle(startX + 23, startY + 205, 12,
                    isCategoryHovered(startX + 11, startY + 193, startX + 35, startY + 217, mouseX, mouseY)
                            || currentCategory == ModuleCategory.EXPLOIT ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB());
            RenderUtils.circle(startX + 23, startY + 245, 12,
                    isCategoryHovered(startX + 11, startY + 233, startX + 35, startY + 257, mouseX, mouseY)
                            || currentCategory == ModuleCategory.MISC ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB());
            RenderUtils.circle(startX + 23, startY + 285, 12,
                    isCategoryHovered(startX + 11, startY + 273, startX + 35, startY + 297, mouseX, mouseY)
                            || currentCategory == ModuleCategory.CLIENT ? new Color(180,180,180).getRGB() : new Color(255,255,255).getRGB());

            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Combat.png"), (int) startX + 17, (int) startY + 40, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Movement.png"), (int) startX + 17, (int) startY + 80, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/World.png"), (int) startX + 17, (int) startY + 120, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Player.png"), (int) startX + 17, (int) startY + 160, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Exploit.png"), (int) startX + 17, (int) startY + 200, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Misc.png"), (int) startX + 17, (int) startY + 240, 12, 12);
            RenderUtils.drawImage(new ResourceLocation("leaf/ui/clickgui/light/Client.png"), (int) startX + 17, (int) startY + 280, 12, 12);
    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
    }

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
    }

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
    }

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

}
