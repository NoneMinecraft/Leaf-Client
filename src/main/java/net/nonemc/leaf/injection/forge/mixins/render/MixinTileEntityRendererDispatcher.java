package net.nonemc.leaf.injection.forge.mixins.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Inject(method = "renderTileEntity", at = @At("HEAD"), cancellable = true)
    private void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage, final CallbackInfo callbackInfo) {
        final XRay xray = Leaf.moduleManager.getModule(XRay.class);

        if (xray.getState() && !xray.getXrayBlocks().contains(tileentityIn.getBlockType()))
            callbackInfo.cancel();
    }
}
