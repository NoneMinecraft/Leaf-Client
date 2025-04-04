package net.nonemc.leaf.launch.data.modernui;

import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.PacketEvent;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleInfo;
import net.nonemc.leaf.launch.data.modernui.clickgui.ClickGui;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.*;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.dropdown.DropdownGUI;
import net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.tenacity.TenacityClickGUI;
import net.nonemc.leaf.launch.options.modernuiLaunchOption;
import net.nonemc.leaf.utils.render.ColorUtils;
import net.nonemc.leaf.utils.render.GetColorUtils;
import net.nonemc.leaf.value.BoolValue;
import net.nonemc.leaf.value.FloatValue;
import net.nonemc.leaf.value.IntegerValue;
import net.nonemc.leaf.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", category = ModuleCategory.CLIENT, keyBind = Keyboard.KEY_RSHIFT, canEnable = false)
public class ClickGUIModule extends Module {
    private final ListValue styleValue = new ListValue("Style", new String[]{"Better","Classic", "Light", "Novoline", "Astolfo", "LiquidBounce", "Null", "Slowly", "Black", "White"}, "Classic") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }
    };

    public static final BoolValue backback = new BoolValue("Background Accent",true);

    public static final ListValue scrollMode = new ListValue("Scroll Mode", new String[]{"Screen Height", "Value"},"Value");

    public static final ListValue colormode = new ListValue("Setting Accent", new String[]{"White", "Color"},"Color");
    public static final IntegerValue clickHeight = new IntegerValue("Tab Height", 250, 100, 500);
    public final FloatValue scaleValue = new FloatValue("Scale", 0.70F, 0.7F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 40);
    public final ListValue backgroundValue = new ListValue("Background", new String[] {"Default", "Gradient", "None"}, "None");

    public final ListValue animationValue = new ListValue("Animation", new String[] {"Bread", "Slide", "LiquidBounce", "Zoom", "Ziul", "None"}, "Ziul");
    public static final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    public static final IntegerValue colorRedValue = (IntegerValue) new IntegerValue("R", 0, 0, 255).displayable(() -> !colorRainbow.get());
    public static final IntegerValue colorGreenValue = (IntegerValue) new IntegerValue("G", 160, 0, 255).displayable(() -> !colorRainbow.get());
    public static final IntegerValue colorBlueValue = (IntegerValue) new IntegerValue("B", 255, 0, 255).displayable(() -> !colorRainbow.get());

    private static final ListValue clickguicolormode = new ListValue("ClickGuiColor",new String[]{"Drak","White"},"Drak");

    public final BoolValue getClosePrevious = (BoolValue) new BoolValue("ClosePrevious",true);

    public static boolean isLight (){
        if (clickguicolormode.get().equalsIgnoreCase("White")){
            return true;
        }
        return false;
    }

    public static Color generateColor() {
        return colorRainbow.get() ? ColorUtils.INSTANCE.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
    }
    @Override
    public void onEnable() {
        if (styleValue.get().equalsIgnoreCase("Classic")){
            mc.displayGuiScreen(new DropdownGUI());
        } else if (styleValue.get().equalsIgnoreCase("Tenacity")){
            mc.displayGuiScreen(new TenacityClickGUI());
        } else {
            updateStyle();
            mc.displayGuiScreen(modernuiLaunchOption.clickGui);
        }

    }

    private void updateStyle() {
        switch (styleValue.get().toLowerCase()) {
            case "liquidbounce":
                modernuiLaunchOption.clickGui.style = new LiquidBounceStyle();
                break;
            case "null":
                modernuiLaunchOption.clickGui.style = new NullStyle();
                break;
            case "slowly":
                modernuiLaunchOption.clickGui.style = new SlowlyStyle();
                break;
            case "black":
                modernuiLaunchOption.clickGui.style = new BlackStyle();
                break;
            case "white":
                modernuiLaunchOption.clickGui.style = new WhiteStyle();
                break;
            case "astolfo":
                modernuiLaunchOption.clickGui.style = new AstolfoStyle();
                break;
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onPacket(final PacketEvent event) {
        final Packet packet = event.getPacket();

        if (packet instanceof S2EPacketCloseWindow && mc.currentScreen instanceof ClickGui) {
            event.cancelEvent();
        }
    }
}
