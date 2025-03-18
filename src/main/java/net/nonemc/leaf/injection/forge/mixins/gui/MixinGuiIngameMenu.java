package net.nonemc.leaf.injection.forge.mixins.gui;

import net.nonemc.leaf.utils.ServerUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends MixinGuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        if(!this.mc.isIntegratedServerRunning())
            this.buttonList.add(new GuiButton(1337, this.width / 2 - 100, this.height / 4 + 128, "%ui.reconnect%"));

        this.buttonList.add(new GuiButton(727, this.width / 2 - 100, this.height - 30, "Keybind Helper"));
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_,CallbackInfo callbackInfo) {

    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if(button.id == 1337) {
            mc.theWorld.sendQuittingDisconnectingPacket();
            ServerUtils.connectToLastServer();
        }
    }
}