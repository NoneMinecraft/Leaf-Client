package net.nonemc.leaf.injection.forge.mixins.render;

import net.minecraft.client.renderer.chunk.VisGraph;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph {

    @Inject(method = "func_178606_a", at = @At("HEAD"), cancellable = true)
    private void func_178606_a(final CallbackInfo callbackInfo) {
        if (Leaf.moduleManager.getModule(XRay.class).getState())
            callbackInfo.cancel();
    }
}
