package net.nonemc.leaf.injection.forge.mixins.performance;

import net.nonemc.leaf.injection.access.IMixinWorldAccess;
import net.minecraft.world.WorldManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value=WorldManager.class)
public abstract class MixinWorldManager implements IMixinWorldAccess {
    @Override
    public void notifyLightSet(int n, int n2, int n3) {
    }
}
