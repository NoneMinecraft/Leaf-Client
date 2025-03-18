package net.nonemc.leaf.injection.forge.mixins.render;

import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.TextEvent;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {
    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
    private String renderString(String string) {
        if (string == null || Leaf.eventManager == null)
            return string;

        final TextEvent textEvent = new TextEvent(string);
        Leaf.eventManager.callEvent(textEvent);
        return textEvent.getText();
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0)
    private String getStringWidth(String string) {
        if (string == null || Leaf.eventManager == null)
            return string;

        final TextEvent textEvent = new TextEvent(string);
        Leaf.eventManager.callEvent(textEvent);
        return textEvent.getText();
    }

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    public void drawString(String p_drawString_1_, float p_drawString_2_, float p_drawString_3_, int p_drawString_4_, boolean p_drawString_5_, CallbackInfoReturnable<Integer> cir) {
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    public void getStringWidth(String p_getStringWidth_1_, CallbackInfoReturnable<Integer> cir) {
    }
}
