package net.nonemc.leaf.injection.forge.mixins.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.features.module.modules.client.FOV;
import net.nonemc.leaf.ui.cape.GuiCapeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (!getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()))
            return;


        if (GuiCapeManager.INSTANCE.getNowCape() != null)
            callbackInfoReturnable.setReturnValue(GuiCapeManager.INSTANCE.getNowCape().getCape());
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final FOV fovModule = Leaf.moduleManager.getModule(FOV.class);

        if (fovModule.getState()) {
            float newFOV = fovModule.getFovValue().get();

            if (!this.isUsingItem()) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            if (this.getItemInUse().getItem() != Items.bow) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            int i = this.getItemInUseDuration();
            float f1 = (float) i / 20.0f;
            f1 = f1 > 1.0f ? 1.0f : f1 * f1;
            newFOV *= 1.0f - f1 * 0.15f;
            callbackInfoReturnable.setReturnValue(newFOV);
        }
    }
}
