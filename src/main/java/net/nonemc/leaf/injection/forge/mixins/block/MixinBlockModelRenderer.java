package net.nonemc.leaf.injection.forge.mixins.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {

    @Inject(method = "renderModelAmbientOcclusion", at = @At("HEAD"), cancellable = true)
    private void renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSide, final CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        final XRay xray = Leaf.moduleManager.getModule(XRay.class);

        if (xray.getState() && !xray.getXrayBlocks().contains(blockIn))
            booleanCallbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "renderModelStandard", at = @At("HEAD"), cancellable = true)
    private void renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides, final CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        final XRay xray = Leaf.moduleManager.getModule(XRay.class);

        if (xray.getState() && !xray.getXrayBlocks().contains(blockIn))
            booleanCallbackInfoReturnable.setReturnValue(false);
    }
}
