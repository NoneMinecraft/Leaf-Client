package net.nonemc.leaf.injection.forge.mixins.render;

import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.render.Glint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = {LayerArmorBase.class})
public abstract class MixinLayerArmorBase implements LayerRenderer<EntityLivingBase> {

    @ModifyArgs(method = "renderGlint", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableLighting()V", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 0), require = 1, allow = 1)
    private void renderGlint(Args args) {
        Glint glint = Leaf.moduleManager.getModule(Glint.class);
        if (glint.getState()) {
            int n = glint.getColor().getRGB();
            args.set(0, (Object) ((float) (n >> 16 & 0xFF) / 255.0f));
            args.set(1, (Object) ((float) (n >> 8 & 0xFF) / 255.0f));
            args.set(2, (Object) ((float) (n & 0xFF) / 255.0f));
            args.set(3, (Object) ((float) (n >> 24 & 0xFF) / 255.0f));
        }
    }
}
