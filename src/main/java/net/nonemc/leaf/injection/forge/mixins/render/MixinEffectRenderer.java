package net.nonemc.leaf.injection.forge.mixins.render;


import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.nonemc.leaf.features.module.modules.client.Animations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer {

    @Shadow
    protected abstract void updateEffectLayer(int layer);

    @Shadow
    private List<EntityParticleEmitter> particleEmitters;

    /**
     * @author Mojang
     * @author Marco
     */
    @Overwrite
    public void updateEffects() {
        try {
            for (int i = 0; i < 4; ++i)
                this.updateEffectLayer(i);

            for (final Iterator<EntityParticleEmitter> it = this.particleEmitters.iterator(); it.hasNext(); ) {
                final EntityParticleEmitter entityParticleEmitter = it.next();

                entityParticleEmitter.onUpdate();

                if (entityParticleEmitter.isDead)
                    it.remove();
            }
        } catch (final ConcurrentModificationException ignored) {
        }
    }

    @Inject(
            method = {
                    "addBlockDestroyEffects",
                    "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V"
            }, at = @At("HEAD"), cancellable = true
    )
    private void removeBlockBreakingParticles(CallbackInfo ci) {
        if (Animations.getNoBlockParticles().get())
            ci.cancel();
    }

    // this is added by forge, so this shouldn't be remapped (and causes a compile error if it is)
    @Inject(
            method = "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/MovingObjectPosition;)V",
            at = @At("HEAD"), cancellable = true, remap = false
    )
    private void removeBlockBreakingParticles_Forge(CallbackInfo ci) {
        if (Animations.getNoBlockParticles().get())
            ci.cancel();
    }
}