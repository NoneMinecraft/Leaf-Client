package net.nonemc.leaf.injection.forge.mixins.gui;

import me.liuli.elixir.account.MinecraftAccount;
import net.nonemc.leaf.features.special.AntiForge;
import net.nonemc.leaf.features.special.AutoReconnect;
import net.nonemc.leaf.libs.base.ServerLib;
import net.nonemc.leaf.libs.extensions.RendererExtensionKt;
import net.nonemc.leaf.libs.session.SessionLib;
import net.nonemc.leaf.ui.altmanager.GuiAltManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.nonemc.leaf.libs.asm.LoginUtils;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import static net.nonemc.leaf.file.FileConfigManagerKt.*;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends MixinGuiScreen {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0");

    @Shadow
    private int field_175353_i;

    @Shadow @Final private GuiScreen parentScreen;
    private GuiButton reconnectButton;
    private GuiSlider autoReconnectDelaySlider;
    private GuiButton forgeBypassButton;
    private int reconnectTimer;
    private String infoStr = "null";

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        reconnectTimer = 0;
        SessionLib.handleConnection();

        final ServerData server= ServerLib.serverData;
        infoStr="§fPlaying on: "+mc.session.getUsername()+" | "+server.serverIP;
        buttonList.add(reconnectButton = new GuiButton(1, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 22, 98, 20, "%ui.reconnect%"));

        buttonList.add(autoReconnectDelaySlider =
                new GuiSlider(2, this.width / 2 + 2, this.height / 2 + field_175353_i / 2
                        + this.fontRendererObj.FONT_HEIGHT + 22, 98, 20, "AutoReconnect: ",
                        "ms", AutoReconnect.MIN, AutoReconnect.MAX, AutoReconnect.INSTANCE.getDelay(), false, true,
                        guiSlider -> {
                            AutoReconnect.INSTANCE.setDelay(guiSlider.getValueInt());

                            this.reconnectTimer = 0;
                            this.updateReconnectButton();
                            this.updateSliderText();
                        }));

        buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 98, 20, "%ui.disconnect.randomAlt%"));
        buttonList.add(new GuiButton(4, this.width / 2 + 2, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 98, 20, "%ui.disconnect.randomOffline%"));
        buttonList.add(forgeBypassButton = new GuiButton(5, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 66, "%ui.antiForge%: " + (AntiForge.INSTANCE.getEnabled() ? "%ui.on%" : "%ui.off%")));

        updateSliderText();
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch (button.id) {
            case 1:
                ServerLib.connectToLastServer();
                break;
            case 3:
                final List<MinecraftAccount> accounts = getAccountsConfig().getAltManagerMinecraftAccounts();
                if (accounts.isEmpty()) break;

                final MinecraftAccount minecraftAccount = accounts.get(new Random().nextInt(accounts.size()));
                GuiAltManager.Companion.login(minecraftAccount);
                ServerLib.connectToLastServer();
                break;
            case 4:
                LoginUtils.INSTANCE.randomCracked();
                ServerLib.connectToLastServer();
                break;
            case 5:
                AntiForge.INSTANCE.setEnabled(!AntiForge.INSTANCE.getEnabled());
                forgeBypassButton.displayString = "%ui.antiForge%: " + (AntiForge.INSTANCE.getEnabled() ? "%ui.on%" : "%ui.off%");
                saveConfig(getSpecialConfig());
                break;
        }
    }

    @Override
    public void updateScreen() {
        if (AutoReconnect.INSTANCE.isEnabled()) {
            reconnectTimer++;
            if (reconnectTimer > AutoReconnect.INSTANCE.getDelay() / 50)
                ServerLib.connectToLastServer();
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, infoStr, this.width / 2F, this.height / 2F + field_175353_i / 2F + this.fontRendererObj.FONT_HEIGHT + 100, 0,false);
        if (AutoReconnect.INSTANCE.isEnabled()) {
            this.updateReconnectButton();
        }
    }

    private void updateSliderText() {
        if (this.autoReconnectDelaySlider == null)
            return;

        if (!AutoReconnect.INSTANCE.isEnabled()) {
            this.autoReconnectDelaySlider.displayString = "AutoReconnect: Off";
        } else {
            this.autoReconnectDelaySlider.displayString = "AutoReconnect: " + DECIMAL_FORMAT.format(AutoReconnect.INSTANCE.getDelay() / 1000.0) + "s";
        }
    }

    private void updateReconnectButton() {
        if (reconnectButton != null)
            reconnectButton.displayString = "Reconnect" + (AutoReconnect.INSTANCE.isEnabled() ? " (" + (AutoReconnect.INSTANCE.getDelay() / 1000 - reconnectTimer / 20) + ")" : "");
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void keyTyped(char typedChar, int keyCode, CallbackInfo callbackInfo) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parentScreen);
        }
    }
}