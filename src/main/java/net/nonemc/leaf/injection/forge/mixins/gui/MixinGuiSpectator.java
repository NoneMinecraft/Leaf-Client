package net.nonemc.leaf.injection.forge.mixins.gui;

import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.Render2DEvent;
import net.nonemc.leaf.injection.access.StaticStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSpectator.class)
public class MixinGuiSpectator {

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution p_175264_1_, float p_175264_2_, CallbackInfo callbackInfo) {
        Leaf.eventManager.callEvent(new Render2DEvent(p_175264_2_, StaticStorage.scaledResolution));
    }
}