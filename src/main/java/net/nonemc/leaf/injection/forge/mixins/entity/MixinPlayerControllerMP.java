package net.nonemc.leaf.injection.forge.mixins.entity;

import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.AttackEvent;
import net.nonemc.leaf.event.ClickWindowEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"), cancellable = true)
    private void attackEntity(EntityPlayer entityPlayer, Entity targetEntity, CallbackInfo callbackInfo) {
        if(targetEntity == null)
            return;

        final AttackEvent event = new AttackEvent(targetEntity);
        Leaf.eventManager.callEvent(event);
        if(event.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "windowClick", at = @At("HEAD"), cancellable = true)
    private void windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn, CallbackInfoReturnable<ItemStack> callbackInfo) {
        final ClickWindowEvent event = new ClickWindowEvent(windowId, slotId, mouseButtonClicked, mode);
        Leaf.eventManager.callEvent(event);

        if (event.isCancelled())
            callbackInfo.cancel();
    }
}