package net.nonemc.leaf.injection.forge.mixins.block;

import net.minecraft.block.BlockSoulSand;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.movement.NoSlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand {

    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void onEntityCollidedWithBlock(CallbackInfo callbackInfo) {
        final NoSlow noSlow = Leaf.moduleManager.getModule(NoSlow.class);

        if (noSlow.getState() && noSlow.getSoulsandValue().get())
            callbackInfo.cancel();
    }
}