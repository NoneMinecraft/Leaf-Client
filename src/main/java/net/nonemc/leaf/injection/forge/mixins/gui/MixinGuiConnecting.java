package net.nonemc.leaf.injection.forge.mixins.gui;

import net.nonemc.leaf.libs.base.ServerLib;
import net.nonemc.leaf.libs.extensions.RendererExtensionKt;
import net.nonemc.leaf.libs.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public abstract class MixinGuiConnecting extends GuiScreen {

    @Inject(method = "connect", at = @At("HEAD"))
    private void headConnect(final String ip, final int port, CallbackInfo callbackInfo) {
        ServerLib.serverData = new ServerData("", ip + ":" + port, false);
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        RenderUtils.drawLoadingCircle(this.width / 2, this.height / 4 + 70);

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = serverData.serverIP;

        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, "Connecting to", this.width / 2, this.height / 4 + 110, 0xFFFFFF, true);
        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, ip, this.width / 2, this.height / 4 + 120, 0x5281FB, true);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}